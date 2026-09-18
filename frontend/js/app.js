// DUST-IT main app script - placeholder.
//
// Right now this just proves frontend -> backend actually works, by
// calling /api/health and showing the result on the page. Real app logic
// (topic search, dashboard, assessments) gets added here and in
// search.js / assessment.js / dashboard.js as those build-order steps
// are reached.

document.addEventListener("DOMContentLoaded", async () => {
    const statusEl = document.getElementById("backend-status");
    try {
        const health = await checkBackendHealth();
        statusEl.textContent = `Backend says: ${health.status}`;
    } catch (err) {
        statusEl.textContent = "Backend not reachable yet - is it running on localhost:8080?";
        return;
    }

    const topicsListEl = document.getElementById("topics-list");
    try {
        const topics = await fetchTopics();
        if (topics.length === 0) {
            topicsListEl.textContent = "No topics yet - add one with a POST to /api/topics (see database/seed.sql for examples).";
        } else {
            topicsListEl.innerHTML = "";
            topics.forEach((topic) => {
                const item = document.createElement("li");
                item.textContent = `[${topic.subject}] ${topic.title}`;
                topicsListEl.appendChild(item);
            });
        }
    } catch (err) {
        topicsListEl.textContent = "Could not load topics.";
    }
});