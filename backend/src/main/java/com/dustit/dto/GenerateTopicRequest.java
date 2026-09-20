package com.dustit.dto;

import jakarta.validation.constraints.NotBlank;

public class GenerateTopicRequest {

    @NotBlank(message = "query is required")
    private String query;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}