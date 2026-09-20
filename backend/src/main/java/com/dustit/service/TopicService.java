package com.dustit.service;

import com.dustit.dto.CreateTopicRequest;
import com.dustit.model.Topic;
import com.dustit.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;



/**
 * Business logic for topics. Right now this is thin (just delegates to
 * the repository), but this is where things like "don't allow duplicate
 * topic titles within a subject" or "generate a slug for the URL" will
 * live as the app grows - the controller shouldn't know about those rules.
 */
@Service
public class TopicService {

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
}