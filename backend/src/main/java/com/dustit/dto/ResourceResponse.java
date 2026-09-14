package com.dustit.dto;

import com.dustit.model.Resource;

public class ResourceResponse {

    private final Long id;
    private final Long topicId;
    private final String title;
    private final String url;
    private final String resourceType;
    private final String source;
    private final String thumbnailUrl;

    public ResourceResponse(Resource resource, String thumbnailUrl) {
        this.id = resource.getId();
        this.topicId = resource.getTopic().getId();
        this.title = resource.getTitle();
        this.url = resource.getUrl();
        this.resourceType = resource.getResourceType();
        this.source = resource.getSource();
        this.thumbnailUrl = thumbnailUrl;
    }

    public Long getId() {
        return id;
    }

    public Long getTopicId() {
        return topicId;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getSource() {
        return source;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}