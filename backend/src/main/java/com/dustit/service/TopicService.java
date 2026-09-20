package com.dustit.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.dustit.dto.CreateTopicRequest;
import com.dustit.model.Topic;
import com.dustit.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.List;
import java.util.Map;



/**
 * Business logic for topics. Right now this is thin (just delegates to
 * the repository), but this is where things like "don't allow duplicate
 * topic titles within a subject" or "generate a slug for the URL" will
 * live as the app grows - the controller shouldn't know about those rules.
 */
@Service
public class TopicService {

    @Value("${GEMINI_API_KEY:}")
    private String geminiApiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    public Optional<Topic> getTopicById(Long id) {
        return topicRepository.findById(id);
    }

    public Topic createTopic(CreateTopicRequest request) {
        Topic topic = new Topic(request.getTitle(), request.getDescription(), request.getSubject());
        return topicRepository.save(topic);
    }

    public List<Topic> searchTopics(String query) {
        if (query == null || query.isBlank()) {
            return getAllTopics();
        }
        return topicRepository
                .findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrSubjectContainingIgnoreCase(
                        query, query, query);
    }

    public Topic generateTopicWithAI(String query) throws Exception {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY is not set - cannot generate a topic.");
        }

        String prompt = """
            A student searched for: "%s"

            Respond with ONLY valid JSON (no markdown, no code fences) in this exact shape:
            {"title": "...", "subject": "...", "description": "..."}

            - title: a short, clear topic name
            - subject: one category word, e.g. Accounting, Programming, Auditing
            - description: a clear explanation a student could learn from, 3-5 sentences
            """.formatted(query);

        String requestBody = objectMapper.writeValueAsString(Map.of(
                "contents", List.of(Map.of(
                        "parts", List.of(Map.of("text", prompt))
                ))
        ));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.6-flash:generateContent?key="
                                + geminiApiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("GEMINI RAW RESPONSE: " + response.body());

        JsonNode root = objectMapper.readTree(response.body());
        String aiText = root.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();


        // Gemini sometimes wraps JSON in ```json fences despite being asked not to - strip them.
        aiText = aiText.replaceAll("```json", "").replaceAll("```", "").trim();

        JsonNode topicJson = objectMapper.readTree(aiText);
        Topic topic = new Topic(
                topicJson.path("title").asText(query),
                topicJson.path("description").asText(""),
                topicJson.path("subject").asText("General")
        );

        return topicRepository.save(topic);
    }
}