package com.dustit.model;

import jakarta.persistence.*;

/**
 * A learning resource attached to a Topic - a video, article, or other
 * external material (Section 4.3 of the requirements doc).
 *
 * DUST-IT doesn't copy the resource content itself (copyright - see
 * Section 4.3), just metadata and a link to the original. The optional
 * thumbnailKey points at an image stored in S3, which DUST-IT *does*
 * own the rights to display.
 */
@Entity
@Table(name = "resources")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @Column(nullable = false)
    private String title;

    // Link to the original resource - DUST-IT never copies the content itself
    @Column(nullable = false)
    private String url;

    // e.g. "VIDEO", "ARTICLE", "PRACTICE_QUESTIONS"
    @Column(nullable = false)
    private String resourceType;

    // Where it came from, e.g. "YouTube", "Investopedia" - Section 4.3
    // requires identifying the original source
    private String source;

    // S3 object key for an optional thumbnail image, e.g. "thumbnails/abc123.jpg"
    // Null if no thumbnail has been uploaded for this resource.
    private String thumbnailKey;

    protected Resource() {
        // required by JPA
    }

    public Resource(Topic topic, String title, String url, String resourceType, String source) {
        this.topic = topic;
        this.title = title;
        this.url = url;
        this.resourceType = resourceType;
        this.source = source;
    }

    public Long getId() {
        return id;
    }

    public Topic getTopic() {
        return topic;
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

    public String getThumbnailKey() {
        return thumbnailKey;
    }

    public void setThumbnailKey(String thumbnailKey) {
        this.thumbnailKey = thumbnailKey;
    }
}
