
// EXISTING: Modified search function to use dynamic BASE_URL
async function search() {
    console.log("🔎 Initiating search with keywords:", keywordList);

    if (keywordList.length === 0) {
        alert("Please enter at least one keyword.");
        return;
    }

    const searchEndpoint = `${BASE_URL}/api/search/execute`;
    let stopButton = document.getElementById("stopSearchButton");
    let logSection = document.getElementById("searchLogsContainer");

    if (searchActive) return;

    try {
        updateSearchStatus("🔄 Searching...");
        stopButton.style.display = "inline-block";
        logSection.style.display = "block"; // ✅ Keep logs visible when searching again

        let response = await fetch(searchEndpoint, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(keywordList),
        });

        if (!response.ok) throw new Error(await response.text());

        console.log("✅ Search started.");
        startFetchingLogs(); // ✅ Keep fetching logs instead of resetting
    } catch (error) {
        console.error("❌ Search execution failed:", error);
        updateSearchStatus(`<span style="color: red;">❌ Search Failed: ${error.message}</span>`);
        alert("Error starting search: " + error.message);
    }
}


/**
 * ✅ Show/Hide logs with smooth transition
 */
function toggleLogs() {
    let logContainer = document.getElementById("searchLogsContainer");
    let toggleButton = document.getElementById("toggleLogsButton");

    if (logContainer.style.maxHeight === "0px" || logContainer.style.maxHeight === "") {
        logContainer.style.maxHeight = "250px";
        toggleButton.textContent = "🔼 Hide Logs";
    } else {
        logContainer.style.maxHeight = "0px";
        toggleButton.textContent = "🔽 Show Logs";
    }
}



/**
 * ✅ Append logs to search status
 */
/**
 * ✅ Function to add log entries with timestamps
 */


function addLogEntry(message) {
    let logContainer = document.getElementById("searchLogs");
    let logSection = document.getElementById("searchLogsContainer");
    let toggleButton = document.getElementById("toggleLogsButton");

    if (!logContainer || !logSection) {
        console.error("❌ Log container not found.");
        return;
    }

    let timestamp = new Date().toLocaleTimeString(); // ✅ Format time
    let logEntry = document.createElement("li");
    logEntry.textContent = `[${timestamp}] ${message}`;
    logContainer.appendChild(logEntry);

    // ✅ Show logs when search starts
    if (logSection.style.display === "none") {
        logSection.style.display = "block";
        logSection.style.maxHeight = "250px";
        toggleButton.style.display = "inline-block"; // ✅ Show toggle button
    }

    // ✅ Auto-scroll only if there are more than 8 logs
    if (logContainer.children.length > 8) {
        logSection.scrollTop = logSection.scrollHeight;
    }
}

/**
 * ✅ Start search process with real-time logs
 */
/**
 * ✅ Clears logs on new search
 */
function clearLogs() {
    let logContainer = document.getElementById("searchLogs");
    if (logContainer.children.length > 0) {
        console.log("🔍 Keeping previous logs for reference.");
        return;
    }
    logContainer.innerHTML = ""; // ✅ Only clear if no previous logs exist
}


/**
 * ✅ Fetch logs from backend every 2 seconds
 */
/**
 * ✅ Fetch logs from backend every 2 seconds
 */


/**
 * ✅ Update logs in UI
 */
function updateLogs(logs) {
    let logContainer = document.getElementById("searchLogs");
    let logSection = document.getElementById("searchLogsContainer");

    if (!logContainer || !logSection) {
        console.error("❌ Missing UI elements for logs.");
        return;
    }

    logContainer.innerHTML = logs.map(log => `<li>${log}</li>`).join("");
    logContainer.scrollTop = logContainer.scrollHeight;

    // ✅ Update label if GPT processing starts
    if (logs.some(log => log.includes("🔄 [Start] Processing lots with GPT-4..."))) {
        updateSearchStatus("⚙️ Processing with GPT...");
    }

    // ✅ Hide logs when processing is done & refresh page
    if (logs.some(log => log.includes("✅ [Finish] GPT Processing complete"))) {
        updateSearchStatus("✅ Process Complete");

        setTimeout(() => {
            logSection.style.display = "none";
            logContainer.innerHTML = ""; // ✅ Clear logs
            location.reload(); // ✅ Refresh the page after completion
        }, 3000);
    }
}


/**
 * ✅ Start fetching logs on search
 */
function startFetchingLogs() {
    if (!document.getElementById("searchLogs").hasChildNodes()) {
        clearLogs(); // ✅ Only clear if no previous logs exist
    }
    fetchLogs(); // ✅ Initial fetch
    setInterval(fetchLogs, 2000); // ✅ Fetch logs every 2 seconds
}


/**
 * ✅ Start search process
 */

function updateSearchStatus(message) {
    let resultsPanel = document.getElementById("resultsPanel");
    if (!resultsPanel) {
        console.error("❌ resultsPanel not found.");
        return;
    }
    resultsPanel.innerHTML = message;
}




/**
 * ✅ Stop the running search with logging
 */
async function stopSearch() {
    if (!searchActive) return;

    console.log("⏹️ Attempting to stop search...");
    updateSearchStatus("⏹️ Stopping search...");
    searchActive = false; // Prevent new searches

    const stopEndpoint = `${BASE_URL}/api/search/stop`;
    let stopButton = document.getElementById("stopSearchButton");
    let logSection = document.getElementById("searchLogsContainer");

    try {
        let response = await fetch(stopEndpoint, {
            method: "POST",
            headers: { "Content-Type": "application/json" }
        });

        if (!response.ok) throw new Error("Failed to stop search.");

        console.log("✅ Search successfully stopped.");
        updateSearchStatus("✅ Search Stopped Successfully");

        // ✅ Keep logs visible for a longer time (10 seconds)
        setTimeout(() => {
            logSection.style.display = "none";
        }, 10000);

        // ✅ Only process lots if search stopped successfully
        processLots();
    } catch (error) {
        console.error("❌ Error stopping search:", error);
        updateSearchStatus("❌ Error Stopping Search");
        alert("❌ Unable to stop search: " + error.message);
    } finally {
        stopButton.style.display = "none";
    }
}

function updateSearchStatus(message) {
    let logSection = document.getElementById("searchLogsContainer");
    let logContainer = document.getElementById("searchLogs");

    let logEntry = document.createElement("p");
    logEntry.textContent = message;
    logEntry.style.fontWeight = "bold";
    logContainer.appendChild(logEntry);

    logSection.style.display = "block";
    logSection.scrollTop = logSection.scrollHeight; // ✅ Auto-scroll logs to latest entry
}


/**
 * ✅ Start processing lots after search ends
 */
async function processLots() {
    updateSearchStatus("⚙️ Processing lots with GPT...");

    const processEndpoint = `${BASE_URL}/api/lots/process`;

    try {
        let response = await fetch(processEndpoint, { method: "POST" });

        if (!response.ok) {
            throw new Error("Failed to process lots. Server returned an error.");
        }

        let result = await response.json();
        console.log("✅ GPT Processing Completed:", result.message);
        updateSearchStatus("✅ GPT Processing Completed Successfully");
    } catch (error) {
        console.error("❌ Error processing lots:", error);
        updateSearchStatus("❌ GPT Processing Failed: " + error.message);
        alert("❌ Failed to process lots: " + error.message);
    } finally {
        fetchLogs(); // ✅ Ensure logs are updated no matter what
    }
}



/**
 * ✅ Stop search when the user switches tabs or navigates away
 */
document.addEventListener("visibilitychange", async function () {
    if (document.hidden && searchActive) {
        console.log("🚨 Tab hidden: Stopping active search...");
        await stopSearch();
    }
});



/**
 * ✅ Stop search when the page is closed or refreshed
 */
window.addEventListener("beforeunload", async function () {
    if (searchActive) {
        console.log("🚨 Page closing: Stopping active search...");
        await stopSearch();
    }
});





document.addEventListener("DOMContentLoaded", function () {
    document.getElementById("searchLogsContainer").style.display = "none"; // ✅ Hide logs by default
    document.getElementById("searchLogs").innerHTML = ""; // ✅ Ensure logs are empty on load
    document.getElementById("toggleLogsButton").style.display = "none"; // ✅ Hide toggle button initially
});

let searchActive = false;





