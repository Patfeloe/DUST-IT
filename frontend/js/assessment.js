// DUST-IT - Take an assessment.
//
// Expects the URL to carry ?assessmentId=&topicId=&title=&difficulty=
// (set by learn.js's Start button - there's no GET /api/assessments/{id}
// to look these up from just the id). Starts an attempt, renders every
// question as a single form, and on submit records each answer then
// completes the attempt to get the score + encouraging message.

function getParams() {
    const params = new URLSearchParams(window.location.search);
    return {
        assessmentId: params.get("assessmentId"),
        topicId: params.get("topicId"),
        title: params.get("title") || "Assessment",
        difficulty: params.get("difficulty") || "",
    };
}

function titleCase(word) {
    if (!word) return "";
    return word.charAt(0) + word.slice(1).toLowerCase();
}

function escapeHtml(value) {
    const div = document.createElement("div");
    div.textContent = value == null ? "" : value;
    return div.innerHTML;
}

function renderQuestions(questions) {
    const bodyEl = document.getElementById("assessment-body");

    if (questions.length === 0) {
        bodyEl.innerHTML = '<p class="empty-state">No questions in this assessment yet.</p>';
        return;
    }

    const form = document.createElement("form");
    form.id = "assessment-form";

    questions.forEach((question, index) => {
        const block = document.createElement("div");
        block.className = "question";

        const optionsHtml = question.options
            .map(
                (option, optionIndex) => `
                <label class="option">
                    <input type="radio" name="q-${question.id}" value="${optionIndex}" required />
                    <span>${escapeHtml(option)}</span>
                </label>
            `
            )
            .join("");

        block.innerHTML = `
            <h4>${index + 1}. ${escapeHtml(question.text)}</h4>
            <div class="options">${optionsHtml}</div>
        `;
        form.appendChild(block);
    });

    const submitBtn = document.createElement("button");
    submitBtn.type = "submit";
    submitBtn.className = "btn btn-primary";
    submitBtn.textContent = "Submit assessment";
    form.appendChild(submitBtn);

    bodyEl.innerHTML = "";
    bodyEl.appendChild(form);
}

function renderResult(result) {
    const bodyEl = document.getElementById("assessment-body");
    const { topicId } = getParams();
    const backHref = topicId ? `learn.html?id=${topicId}` : "learn.html";

    bodyEl.innerHTML = `
        <div class="result">
            <p class="score">${Math.round(result.score)}%</p>
            <p class="message">${escapeHtml(result.message)}</p>
            <div class="result-actions">
                <a class="btn btn-primary" href="${escapeHtml(window.location.pathname + window.location.search)}">Try again</a>
                <a class="btn" href="${backHref}">Back to topic</a>
            </div>
        </div>
    `;
}

async function handleSubmit(event, attemptId, questions) {
    event.preventDefault();
    const form = event.target;
    const submitBtn = form.querySelector("button[type=submit]");
    submitBtn.disabled = true;
    submitBtn.textContent = "Submitting\u2026";

    try {
        for (const question of questions) {
            const selected = form.querySelector(`input[name="q-${question.id}"]:checked`);
            if (!selected) continue;
            await submitAnswer(attemptId, question.id, Number(selected.value));
        }
        const result = await completeAttempt(attemptId);
        renderResult(result);
    } catch (err) {
        document.getElementById("assessment-body").innerHTML =
            '<p class="empty-state">Something went wrong submitting your answers. Please try again.</p>';
    }
}

async function loadAssessment() {
    const { assessmentId, topicId, title, difficulty } = getParams();

    document.getElementById("assessment-title").textContent = title;
    document.getElementById("assessment-difficulty").textContent = titleCase(difficulty);
    if (topicId) {
        document.getElementById("back-link").href = `learn.html?id=${topicId}`;
    }

    if (!assessmentId) {
        document.getElementById("assessment-body").innerHTML =
            '<p class="empty-state">No assessment selected. Go back and choose one from a topic.</p>';
        return;
    }

    try {
        const [attempt, questions] = await Promise.all([
            startAttempt(assessmentId),
            fetchQuestionsForAssessment(assessmentId),
        ]);

        renderQuestions(questions);

        const form = document.getElementById("assessment-form");
        if (form) {
            form.addEventListener("submit", (event) => handleSubmit(event, attempt.id, questions));
        }
    } catch (err) {
        document.getElementById("assessment-body").innerHTML =
            '<p class="empty-state">Could not start this assessment. Is the backend running?</p>';
    }
}

document.addEventListener("DOMContentLoaded", loadAssessment);// DUST-IT assessment flow - placeholder.
// Built once the Assessments feature (build-order step 6) is implemented.
