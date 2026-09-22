package com.dustit.competency;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Consumes ASSESSMENT_COMPLETED events from SQS (published by
 * EventPublisherService in the backend) and recalculates concept-level
 * competency (Section 10 of the requirements doc) into the
 * competency_results table.
 *
 * Deliberately plain JDBC, not JPA/Hibernate: this is a separate,
 * independently-scaling, independently-failing function (build-order
 * step 8) - see the module README for the full reasoning. It only reads
 * and writes tables the Spring Boot backend already created via
 * ddl-auto=update; it never changes the schema itself.
 *
 * If this function has a bug or is misconfigured, students can still
 * take assessments and get their raw score - only the concept-level
 * competency recalculation is affected, and it will simply lag until
 * this is fixed and SQS redelivers or the backlog is reprocessed.
 */
public class CompetencyCalculatorHandler implements RequestHandler<SQSEvent, Void> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String dbUrl = System.getenv("DB_URL");
    private final String dbUsername = System.getenv("DB_USERNAME");
    private final String dbPassword = System.getenv("DB_PASSWORD");

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage message : event.getRecords()) {
            try {
                processMessage(message.getBody(), context);
            } catch (Exception e) {
                // Don't let one bad/malformed message fail the whole batch -
                // log it and move on. SQS will retry this specific message
                // on its own, according to the queue's redrive policy.
                context.getLogger().log("Failed to process message: " + e.getMessage() + "\n");
            }
        }
        return null;
    }

    private void processMessage(String body, Context context) throws Exception {
        JsonNode root = MAPPER.readTree(body);
        String eventType = root.path("eventType").asText();

        // This function only acts on ASSESSMENT_COMPLETED. The other
        // LearningEventType values (TOPIC_SEARCHED, RESOURCE_VIEWED, etc.)
        // are published for future analytics consumers, not this one.
        if (!"ASSESSMENT_COMPLETED".equals(eventType)) {
            return;
        }

        String studentId = root.path("studentId").asText();
        JsonNode answers = root.path("data").path("answers");

        if (studentId.isBlank() || !answers.isArray()) {
            context.getLogger().log("Skipping malformed ASSESSMENT_COMPLETED event: " + body + "\n");
            return;
        }

        Set<Long> conceptIds = new LinkedHashSet<>();
        for (JsonNode answer : answers) {
            for (JsonNode conceptIdNode : answer.path("conceptIds")) {
                conceptIds.add(conceptIdNode.asLong());
            }
        }

        if (conceptIds.isEmpty()) {
            // None of the questions in this attempt were tagged to a
            // concept yet - nothing to recalculate.
            return;
        }

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword)) {
            for (Long conceptId : conceptIds) {
                double score = calculateScore(conn, studentId, conceptId);
                String status = statusFor(score);
                upsertCompetencyResult(conn, studentId, conceptId, score, status);
            }
        }
    }

    /**
     * Cumulative competency: the percentage of every answer this student
     * has EVER given to a question tagged with this concept, across every
     * attempt - not just the attempt that triggered this event. A single
     * attempt often only touches a given concept once or twice, so a
     * meaningful competency score needs the full history, not just the
     * latest attempt.
     */
    private double calculateScore(Connection conn, String studentId, Long conceptId) throws SQLException {
        String sql = "SELECT "
                + "  COUNT(*) AS total, "
                + "  COUNT(*) FILTER (WHERE a.correct) AS correct_count "
                + "FROM answers a "
                + "JOIN attempts att ON a.attempt_id = att.id "
                + "JOIN question_concepts qc ON qc.question_id = a.question_id "
                + "WHERE att.student_id = ? AND qc.concept_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setLong(2, conceptId);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                long total = rs.getLong("total");
                long correct = rs.getLong("correct_count");
                return total == 0 ? 0.0 : (correct * 100.0) / total;
            }
        }
    }

    /**
     * Competency thresholds - not specified anywhere else in the project;
     * this is a judgment call made in this function, not a documented
     * requirement. Easy to move to an env var later if 80/50 isn't the
     * right bar once real students start using this.
     */
    private String statusFor(double score) {
        if (score >= 80.0) return "Competent";
        if (score >= 50.0) return "Developing";
        return "Needs practice";
    }

    private void upsertCompetencyResult(Connection conn, String studentId, Long conceptId,
                                        double score, String status) throws SQLException {
        String sql = "INSERT INTO competency_results (student_id, concept_id, score, status, updated_at) "
                + "VALUES (?, ?, ?, ?, now()) "
                + "ON CONFLICT (student_id, concept_id) "
                + "DO UPDATE SET score = EXCLUDED.score, "
                + "              status = EXCLUDED.status, "
                + "              updated_at = EXCLUDED.updated_at";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setLong(2, conceptId);
            stmt.setDouble(3, score);
            stmt.setString(4, status);
            stmt.executeUpdate();
        }
    }
}