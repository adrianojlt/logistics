package com.adrianojlt.logistics.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SchemaService {

    private final JdbcTemplate jdbcTemplate;

    public String getDatabaseSchema() {

        StringBuilder schema = new StringBuilder();

        List<String> tables = jdbcTemplate.queryForList(
            "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES " +
            "WHERE TABLE_SCHEMA = DATABASE() ORDER BY TABLE_NAME",
            String.class
        );

        for (String table : tables) {
            schema.append("Table: ").append(table).append("\n");
            schema.append("Columns:\n");

            List<Map<String, Object>> columns = jdbcTemplate.queryForList(
                "SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE " +
                "FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_NAME = ? AND TABLE_SCHEMA = DATABASE() " +
                "ORDER BY ORDINAL_POSITION",
                table
            );

            for (Map<String, Object> col : columns) {
                schema.append("  - ")
                      .append(col.get("COLUMN_NAME"))
                      .append(" (").append(col.get("DATA_TYPE")).append(")")
                      .append(Boolean.parseBoolean(
                          String.valueOf(col.get("IS_NULLABLE"))) ? " nullable" : " not null")
                      .append("\n");
            }

            schema.append("\n");
        }

        return schema.toString();
    }
}
