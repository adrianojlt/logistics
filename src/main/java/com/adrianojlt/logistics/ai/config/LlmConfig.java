package com.adrianojlt.logistics.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class LlmConfig {

    private String activeProvider;
    private Map<String, ProviderConfig> providers;

    public ProviderConfig getActiveProviderConfig() {

        ProviderConfig config = providers.get(activeProvider);

        if (config == null) {
            throw new IllegalStateException("No LLM provider configured for: " + activeProvider);
        }

        return config;
    }
}
