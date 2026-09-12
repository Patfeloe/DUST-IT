package com.dustit.model;

/**
 * The learning event types from Section 17 of the requirements doc.
 * Each one gets published to the SQS queue and processed asynchronously -
 * this is what makes the architecture event-driven rather than just a
 * list of names in a document.
 */
public enum LearningEventType {
    TOPIC_SEARCHED,
    RESOURCE_VIEWED,
    LESSON_STARTED,
    QUESTION_ANSWERED,
    ASSESSMENT_COMPLETED,
    TOPIC_COMPLETED,
    TOPIC_SKIPPED,
    COMPETENCY_UPDATED,
    NOTE_SAVED,
    OFFLINE_SESSION_COMPLETED
}