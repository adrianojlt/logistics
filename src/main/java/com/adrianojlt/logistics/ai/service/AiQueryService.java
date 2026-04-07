package com.adrianojlt.logistics.ai.service;

import com.adrianojlt.logistics.ai.dto.AiQueryResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiQueryService {

    private static final String SQL_SYSTEM_PROMPT =
        "You are a SQL expert. Given a database schema and a user question, " +
        "generate a single valid SQL SELECT query. " +
        "Return ONLY the raw SQL query. No explanations, no markdown, " +
        "no code blocks, no backticks.";

    private static final String ANSWER_SYSTEM_PROMPT =
        "You are a helpful data analyst. Given a user question and raw data " +
        "from a database query, provide a clear, concise, human-readable answer " +
        "in plain English. Do not mention SQL, tables, or technical details.";

    private final LlmService llmService;
    private final SchemaService schemaService;
    private final JdbcTemplate jdbcTemplate;

    public AiQueryResponseDTO query(String question) {

        String schema = schemaService.getDatabaseSchema();

        String sqlPrompt = buildSqlPrompt(schema, question, null, null);
        String generatedSql = cleanSql(llmService.chat(SQL_SYSTEM_PROMPT, sqlPrompt));

        if (!isSafeQuery(generatedSql)) {
            return unsafeQueryError(question, generatedSql, "Generated query is not a SELECT statement. Only read operations are permitted.");
        }

        List<Map<String, Object>> results;
        try {
            results = jdbcTemplate.queryForList(generatedSql);
        } catch (DataAccessException firstError) {

            log.warn("SQL execution failed on first attempt", firstError);

            String retryPrompt = buildSqlPrompt(schema, question, generatedSql, firstError.getMessage());
            String retrySql = cleanSql(llmService.chat(SQL_SYSTEM_PROMPT, retryPrompt));

            if (!isSafeQuery(retrySql)) {
                return unsafeQueryError(question, retrySql, "Retry generated an unsafe query.");
            }

            try {
                results = jdbcTemplate.queryForList(retrySql);
                return humanizeResults(question, retrySql, results);
            } catch (DataAccessException retryError) {
                log.error("SQL execution failed on retry", retryError);
                return AiQueryResponseDTO.builder()
                    .question(question)
                    .generatedSql(retrySql)
                    .success(false)
                    .errorMessage("Could not generate a valid query after retry. Please rephrase your question.")
                    .build();
            }
        }

        return humanizeResults(question, generatedSql, results);
    }

    private AiQueryResponseDTO unsafeQueryError(String question, String sql, String message) {
        return AiQueryResponseDTO.builder()
            .question(question)
            .generatedSql(sql)
            .success(false)
            .errorMessage(message)
            .build();
    }

    private AiQueryResponseDTO humanizeResults(String question, String sql, List<Map<String, Object>> results) {

        String answerPrompt = "Question: " + question + "\n\n" + "Data: " + results.toString();
        String answer = llmService.chat(ANSWER_SYSTEM_PROMPT, answerPrompt);

        return AiQueryResponseDTO.builder()
            .question(question)
            .generatedSql(sql)
            .answer(answer)
            .success(true)
            .build();
    }

    private String buildSqlPrompt(String schema, String question, String failedSql, String error) {
        if (failedSql != null) {
            return "The following SQL query failed with error: " + error + "\n\n" +
                   "Failed SQL: " + failedSql + "\n\n" +
                   "Schema:\n" + schema + "\n" +
                   "Question: " + question + "\n\n" +
                   "Generate a corrected SQL SELECT query. Return ONLY the SQL.";
        }

        return "Schema:\n" + schema + "\n" + "Question: " + question;
    }

    private boolean isSafeQuery(String sql) {
        return sql.trim().toUpperCase().startsWith("SELECT");
    }

    private String cleanSql(String raw) {
        return raw.replaceAll("```sql", "").replaceAll("```", "").trim();
    }
}
