// DUST-IT API helper.
//
// Centralizes calls to the backend so other JS files don't hardcode URLs.

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

async function fetchTopic(topicId) {
    const response = await fetch(`${API_BASE_URL}/topics/${topicId}`);
    if (!response.ok) {
        throw new Error(`Topic ${topicId} not found`);
    }
    return response.json();
}

async function fetchConceptsForTopic(topicId) {
    const response = await fetch(`${API_BASE_URL}/topics/${topicId}/concepts`);
    return response.json();
}

async function fetchResourcesForTopic(topicId) {
    const response = await fetch(`${API_BASE_URL}/topics/${topicId}/resources`);
    return response.json();
}

async function fetchAssessmentsForTopic(topicId) {
    const response = await fetch(`${API_BASE_URL}/topics/${topicId}/assessments`);
    return response.json();
}

function getOrCreateGuestStudentId() {
    const key = "dustit_student_id";
    let id = localStorage.getItem(key);
    if (!id) {
        id = "guest-" + crypto.randomUUID();
        localStorage.setItem(key, id);
    }
    return id;
}

async function fetchQuestionsForAssessment(assessmentId) {
    const response = await fetch(`${API_BASE_URL}/assessments/${assessmentId}/questions`);
    return response.json();
}

async function startAttempt(assessmentId) {
    const response = await fetch(`${API_BASE_URL}/assessments/${assessmentId}/attempts`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ studentId: getOrCreateGuestStudentId() }),
    });
    if (!response.ok) {
        throw new Error("Could not start attempt");
    }
    return response.json();
}

async function submitAnswer(attemptId, questionId, selectedOptionIndex) {
    const response = await fetch(`${API_BASE_URL}/attempts/${attemptId}/answers`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ questionId, selectedOptionIndex }),
    });
    if (!response.ok) {
        throw new Error("Could not submit answer");
    }
    return response.json();
}

async function completeAttempt(attemptId) {
    const response = await fetch(`${API_BASE_URL}/attempts/${attemptId}/complete`, {
        method: "POST",
    });
    if (!response.ok) {
        throw new Error("Could not complete attempt");
    }
    return response.json();
}

async function fetchCompetencyForStudent(studentId) {
    const response = await fetch(`${API_BASE_URL}/students/${studentId}/competency`);
    if (!response.ok) {
        throw new Error("Could not load competency");
    }
    return response.json();
}

async function fetchCompetencyForStudent(studentId) {
    const response = await fetch(`${API_BASE_URL}/students/${encodeURIComponent(studentId)}/competency`);
    if (!response.ok) {
        throw new Error("Could not load competency");
    }
    return response.json();
}