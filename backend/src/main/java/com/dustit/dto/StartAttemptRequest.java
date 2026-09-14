package com.dustit.dto;

/**
 * studentId here is only a fallback for local testing without Cognito
 * configured (see AuthUtil / SecurityConfig) - once COGNITO_ISSUER_URI is
 * set, the authenticated JWT's subject is used instead, regardless of
 * what's sent here. Not validated as required, since it's optional once
 * real auth is in place.
 */
public class StartAttemptRequest {

    private String studentId;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}