// DUST-IT topic search.
//
// Wires up the search box on pages/topic.html to the /api/topics?query=
// endpoint (see TopicController), and renders matching topics into the
// page.

document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.getElementById("search-input");
    const searchButton = document.getElementById("search-button");
    const resultsEl = document.getElementById("search-results");

    async function runSearch() {
        const query = searchInput.value.trim();

        resultsEl.textContent = "Searching...";

        try {
            const topics = await searchTopics(query);

            if (topics.length === 0) {
                resultsEl.textContent = "No topics found for that search.";
                return;
            }

            resultsEl.innerHTML = "";
            topics.forEach((topic) => {
                const item = document.createElement("div");
                item.className = "topic-result";

                const title = document.createElement("h3");
                title.textContent = `${topic.title} (${topic.subject})`;

                const description = document.createElement("p");
                description.textContent = topic.description || "No description yet.";

                item.appendChild(title);
                item.appendChild(description);
                resultsEl.appendChild(item);
            });
        } catch (err) {
            resultsEl.textContent = "Something went wrong while searching. Is the backend running?";
        }
    }

    searchButton.addEventListener("click", runSearch);

    searchInput.addEventListener("keydown", (event) => {
        if (event.key === "Enter") {
            runSearch();
        }
    });
});