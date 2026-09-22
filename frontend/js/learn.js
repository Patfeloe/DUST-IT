// DUST-IT - Learn a topic page.
//
// Reads ?id= from the URL and renders one topic: its description,
// concepts, resources, and the assessments a student can take on it.

const DIFFICULTY_ORDER = ["BEGINNER", "INTERMEDIATE", "ADVANCED", "EXPERT"];

function getTopicIdFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
}

function renderConcepts(concepts) {
    const el = document.getElementById("concepts-list");

    if (concepts.length === 0) {
        el.innerHTML = '<p class="empty-state">No concepts added for this topic yet.</p>';
        return;
    }

    el.innerHTML = "";
    concepts.forEach((concept) => {
        const row = document.createElement("div");
        row.className = "list-row";
        row.innerHTML = `
            <div class="marker"></div>
            <div class="content">
                <h4>${escapeHtml(concept.name)}</h4>
                <p>${escapeHtml(concept.description || "")}</p>
            </div>
        `;
        el.appendChild(row);
    });
}

function renderResources(resources) {
    const el = document.getElementById("resources-list");

    if (resources.length === 0) {
        el.innerHTML = '<p class="empty-state">No resources linked yet - check back soon.</p>';
        return;
    }

    el.innerHTML = "";
    resources.forEach((resource) => {
        const row = document.createElement("a");
        row.className = "list-row";
        row.href = resource.url;
        row.target = "_blank";
        row.rel = "noopener noreferrer";
        row.style.textDecoration = "none";
        row.style.color = "inherit";

        const initial = resource.title ? resource.title.trim().charAt(0).toUpperCase() : "?";
        const thumb = resource.thumbnailUrl
            ? `<img src="${resource.thumbnailUrl}" alt="" />`
            : initial;

        row.innerHTML = `
            <div class="thumb">${thumb}</div>
            <div class="content">
                <h4>${escapeHtml(resource.title)}</h4>
                <p class="source">${escapeHtml(resource.resourceType)}${resource.source ? " &middot; " + escapeHtml(resource.source) : ""}</p>
            </div>
        `;
        el.appendChild(row);
    });
}

function renderAssessments(assessments, topicId) {
    const el = document.getElementById("assessments-list");

    if (assessments.length === 0) {
        el.innerHTML = '<p class="empty-state">No assessments for this topic yet - nothing to test yourself on just yet.</p>';
        return;
    }

    const sorted = [...assessments].sort(
        (a, b) => DIFFICULTY_ORDER.indexOf(a.difficultyLevel) - DIFFICULTY_ORDER.indexOf(b.difficultyLevel)
    );

    el.innerHTML = "";
    sorted.forEach((assessment) => {
        const row = document.createElement("div");
        row.className = "assessment-row";
        row.innerHTML = `
            <div>
                <span class="badge">${escapeHtml(titleCase(assessment.difficultyLevel))}</span>
                <span>${escapeHtml(assessment.title)}</span>
            </div>
            <a class="btn btn-primary" href="Assessment.html?assessmentId=${assessment.id}&topicId=${topicId}&title=${encodeURIComponent(assessment.title)}&difficulty=${assessment.difficultyLevel}">Start</a>
        `;
        el.appendChild(row);
    });
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

async function loadTopic() {
    const topicId = getTopicIdFromUrl();

    if (!topicId) {
        document.getElementById("topic-title").textContent = "No topic selected";
        document.getElementById("topic-description").textContent =
            "Go back and search for a topic to open it here.";
        return;
    }

    try {
        const topic = await fetchTopic(topicId);
        document.getElementById("topic-subject").textContent = topic.subject;
        document.getElementById("topic-title").textContent = topic.title;
        document.getElementById("topic-description").textContent =
            topic.description || "No description yet for this topic.";
        document.title = `DUST-IT - ${topic.title}`;
    } catch (err) {
        document.getElementById("topic-title").textContent = "Topic not found";
        document.getElementById("topic-description").textContent =
            "This topic may have been removed. Go back and search for another one.";
        return;
    }

    const [concepts, resources, assessments] = await Promise.all([
        fetchConceptsForTopic(topicId).catch(() => []),
        fetchResourcesForTopic(topicId).catch(() => []),
        fetchAssessmentsForTopic(topicId).catch(() => []),
    ]);

    renderConcepts(concepts);
    renderResources(resources);
    renderAssessments(assessments, topicId);
}

document.addEventListener("DOMContentLoaded", loadTopic);