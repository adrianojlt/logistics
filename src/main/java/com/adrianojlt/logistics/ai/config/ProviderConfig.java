package com.adrianojlt.logistics.ai.config;

import lombok.Data;

@Data
public class ProviderConfig {
    private String url;
    private String model;
    private String apiKey;
}
