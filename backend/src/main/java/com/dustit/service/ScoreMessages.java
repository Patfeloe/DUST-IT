package com.dustit.service;

/**
 * The encouraging DUST-IT score messages from Section 11 of the
 * requirements doc - turns a raw percentage into motivating feedback
 * instead of just a number.
 */
public final class ScoreMessages {

    private ScoreMessages() {
    }

    public static String forScore(double score) {
        if (score < 40) {
            return "Not yet competent - let's work on this together.";
        } else if (score < 60) {
            return "You're about to dust this topic!";
        } else if (score < 75) {
            return "Never been proud 🤭 Look at you being a master!";
        } else if (score < 90) {
            return "EISH! Look at you! You're dusting this!";
        } else {
            return "HAIBO! You came to destroy this topic!";
        }
    }
}