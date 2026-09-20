// DUST-IT API helper - placeholder.
//
// Centralizes calls to the backend so other JS files don't hardcode URLs.
// Filled in as real endpoints (topics, assessments, competency) are built.

const API_BASE_URL = "http://localhost:8080/api";

async function checkBackendHealth() {
    const response = await fetch(`${API_BASE_URL}/health`);
    return response.json();
}

async function fetchTopics() {
    const response = await fetch(`${API_BASE_URL}/topics`);
    return response.json();
}

async function searchTopics(query) {
    const response = await fetch(`${API_BASE_URL}/topics?query=${encodeURIComponent(query)}`);
    return response.json();
}