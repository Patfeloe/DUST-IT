// DUST-IT - Student dashboard.
//
// Shows concept-level competency (Section 10), grouped by topic. This
// reads from GET /api/students/{id}/competency, which only has data once
// the competency-function Lambda (build-order step 8) is consuming
// ASSESSMENT_COMPLETED events and writing competency_results rows - the
// endpoint works today, but the Lambda that feeds it isn't built yet, so
// expect this to be empty until it is.

function escapeHtml(value) {
    const div = document.createElement("div");
    div.textContent = value == null ? "" : value;
    return div.innerHTML;
}

function groupByTopic(results) {
    const groups = new Map();
    results.forEach((result) => {
        if (!groups.has(result.topicId)) {
            groups.set(result.topicId, { topicTitle: result.topicTitle, concepts: [] });
        }
        groups.get(result.topicId).concepts.push(result);
    });
    return groups;
}

function renderDashboard(results) {
    const bodyEl = document.getElementById("dashboard-body");

    if (results.length === 0) {
        bodyEl.innerHTML = `
            <p class="empty-state">
                No competency results yet. Take an assessment on a topic and
                check back here once it's been scored.
            </p>
        `;
        return;
    }

    const groups = groupByTopic(results);
    bodyEl.innerHTML = "";

    groups.forEach((group, topicId) => {
        const section = document.createElement("section");
        section.className = "section";

        const heading = document.createElement("h2");
        heading.className = "section-label";
        heading.innerHTML = `<a href="learn.html?id=${topicId}">${escapeHtml(group.topicTitle)}</a>`;
        section.appendChild(heading);

        group.concepts.forEach((concept) => {
            const row = document.createElement("div");
            row.className = "list-row";
            row.innerHTML = `
                <div class="marker"></div>
                <div class="content">
                    <h4>${escapeHtml(concept.conceptName)}</h4>
                    <p>${escapeHtml(concept.status)} &middot; ${Math.round(concept.score)}%</p>
                </div>
            `;
            section.appendChild(row);
        });

        bodyEl.appendChild(section);
    });
}

async function loadDashboard() {
    const studentId = getOrCreateGuestStudentId();

    try {
        const results = await fetchCompetencyForStudent(studentId);
        renderDashboard(results);
    } catch (err) {
        document.getElementById("dashboard-body").innerHTML =
            '<p class="empty-state">Could not load your progress. Is the backend running?</p>';
    }
}

document.addEventListener("DOMContentLoaded", loadDashboard);