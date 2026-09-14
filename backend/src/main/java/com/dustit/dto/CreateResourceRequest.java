package com.dustit.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateResourceRequest {

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "url is required")
    private String url;

    @NotBlank(message = "resourceType is required")
    private String resourceType;

    private String source;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}