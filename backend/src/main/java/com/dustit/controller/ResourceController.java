package com.dustit.controller;

import com.dustit.dto.CreateResourceRequest;
import com.dustit.dto.ResourceResponse;
import com.dustit.model.Resource;
import com.dustit.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 *   GET  /api/topics/{topicId}/resources           - list resources for a topic
 *   POST /api/topics/{topicId}/resources            - add a resource to a topic
 *   POST /api/resources/{id}/thumbnail (multipart)  - upload a thumbnail image to S3
 */
@RestController
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping("/api/topics/{topicId}/resources")
    public List<ResourceResponse> getResourcesForTopic(@PathVariable Long topicId) {
        return resourceService.getResourcesForTopic(topicId).stream()
                .map(resource -> new ResourceResponse(resource, resourceService.getThumbnailUrl(resource)))
                .toList();
    }

    @PostMapping("/api/topics/{topicId}/resources")
    public ResponseEntity<ResourceResponse> createResource(
            @PathVariable Long topicId,
            @Valid @RequestBody CreateResourceRequest request) {

        return resourceService.createResource(topicId, request)
                .map(resource -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(new ResourceResponse(resource, resourceService.getThumbnailUrl(resource))))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/resources/{id}/thumbnail")
    public ResponseEntity<ResourceResponse> uploadThumbnail(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        return resourceService.uploadThumbnail(id, file)
                .map(resource -> ResponseEntity.ok(new ResourceResponse(resource, resourceService.getThumbnailUrl(resource))))
                .orElse(ResponseEntity.notFound().build());
    }
}
