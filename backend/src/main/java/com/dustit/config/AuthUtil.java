package com.dustit.config;

import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Resolves "who is making this request" from the verified Cognito JWT
 * when auth is configured, falling back to a client-supplied studentId
 * for local development when it isn't (see SecurityConfig).
 *
 * Once COGNITO_ISSUER_URI is set, the JWT's `sub` claim is always used -
 * the client-supplied value is only ever a fallback for testing without
 * Cognito set up, never trusted once real auth is in place. This is the
 * actual security fix build-order step 9 is for: a student can no longer
 * pretend to be someone else just by editing a request body.
 */
public final class AuthUtil {

    private AuthUtil() {
    }

    public static String resolveStudentId(Jwt jwt, String fallbackStudentId) {
        if (jwt != null) {
            return jwt.getSubject();
        }
        return fallbackStudentId;
    }
}