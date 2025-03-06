async function savePreset() {
    let presetName = prompt("Enter a name for this preset:");
    if (!presetName) return;

    let filters = {};

    // ✅ Collect ALL filters (inputs, selects, multi-selects, checkboxes, and dates)
    document.querySelectorAll("input, select").forEach(el => {
        if (el.type === "checkbox") {
            filters[el.id] = el.checked ? "true" : "false";
        } else if (el.multiple) {
            filters[el.id] = [...el.selectedOptions].map(opt => opt.value).join(",");
        } else {
            filters[el.id] = el.value;
        }
    });

    let payload = { presetName, filters };

    let response = await fetch(`${BASE_URL}/api/search-presets/save`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    });

    if (response.ok) {
        alert("✅ Preset saved successfully!");
        loadPresets();
    } else {
        alert("❌ Failed to save preset.");
    }
}

async function loadPresets() {
    try {
        let response = await fetch(`${BASE_URL}/api/search-presets/all`);
        if (!response.ok) throw new Error("Failed to load presets");

        let presets = await response.json();
        let presetContainer = document.getElementById("presetContainer");
        presetContainer.innerHTML = "";

        if (presets.length === 0) {
            presetContainer.innerHTML = "<p class='no-presets'>No saved presets</p>";
            return;
        }

        presets.forEach(preset => {
            let presetDiv = document.createElement("div");
            presetDiv.classList.add("preset-item");
            presetDiv.id = `preset-${preset.presetName}`;

            let presetButton = document.createElement("button");
            presetButton.classList.add("preset-btn");
            presetButton.textContent = preset.presetName;
            presetButton.onclick = () => applyPreset(preset.presetName);

            let deleteButton = document.createElement("span"); // ❌ Icon Only
            deleteButton.classList.add("delete-preset-btn");
            deleteButton.innerHTML = "❌";
            deleteButton.onclick = () => deletePreset(preset.presetName);

            presetDiv.appendChild(presetButton);
            presetDiv.appendChild(deleteButton);
            presetContainer.appendChild(presetDiv);
        });
    } catch (error) {
        console.error("❌ Error loading presets:", error);
        document.getElementById("presetContainer").innerHTML = "<p class='error-message'>❌ Error loading presets</p>";
    }
}


async function applyPreset(presetName) {
    let response = await fetch(`${BASE_URL}/api/search-presets/load/${presetName}`);
    if (!response.ok) {
        alert("❌ Preset not found.");
        return;
    }

    let { filters } = await response.json();

    console.log("🔄 Applying Preset:", presetName, filters);

    // ✅ Restore saved filters into UI (Inputs & Selects)
    document.querySelectorAll("input, select").forEach(el => {
        if (filters[el.id] !== undefined) {
            if (el.type === "checkbox") {
                el.checked = filters[el.id] === "true";
            } else if (el.multiple) {
                let values = filters[el.id].split(",");
                $(el).val(values).trigger("change"); // ✅ Works for Select2
            } else {
                el.value = filters[el.id];
            }
        }
    });

    // ✅ Restore Select2 dropdowns properly
    $(".multi-select").each(function () {
        let selectId = this.id;
        if (filters[selectId] !== undefined) {
            let values = filters[selectId].split(",");
            $(this).val(values).trigger("change"); // ✅ Update UI with Select2
        }
    });

    // ✅ Restore Autocomplete Fields
    ["#brand", "#model", "#maisonEnchere", "#gpuModel"].forEach(selector => {
        let field = $(selector);
        if (field.hasClass("ui-autocomplete-input") && filters[selector.substring(1)]) {
            field.val(filters[selector.substring(1)]).trigger("change");
        }
    });

    // ✅ Restore Auction Date Fields
    if (filters["minDate"]) document.getElementById("minDate").value = filters["minDate"];
    if (filters["maxDate"]) document.getElementById("maxDate").value = filters["maxDate"];

    // ✅ Restore Performance Score Filters
    if (filters["minNoteSur10"]) document.getElementById("minNoteSur10").value = filters["minNoteSur10"];
    if (filters["maxNoteSur10"]) document.getElementById("maxNoteSur10").value = filters["maxNoteSur10"];

    // ✅ Ensure API gets the new filter state & refresh the laptop list
    console.log("✅ Filters applied. Fetching laptops...");
    fetchLaptops(filters);
}


async function deletePreset(presetName) {
    let confirmDelete = confirm(`Are you sure you want to delete '${presetName}'?`);
    if (!confirmDelete) return;

    try {
        let response = await fetch(`${BASE_URL}/api/search-presets/delete/${encodeURIComponent(presetName)}`, {
            method: "DELETE"
        });

        let data = await response.json();
        if (!response.ok) {
            throw new Error(data.error || "Unknown error");
        }

        // ✅ Remove preset from UI
        let presetElement = document.getElementById(`preset-${presetName}`);
        if (presetElement) presetElement.remove();

        // ✅ Refresh UI: If no presets left, reset table
        if (document.querySelectorAll(".preset-item").length === 0) {
            document.getElementById("presetContainer").innerHTML = "<p class='no-presets'>No saved presets</p>";
            resetTable(); // ✅ Clear the table when no presets remain
        }

        alert("✅ Preset deleted successfully!");
        loadPresets(); // Refresh the presets list
    } catch (error) {
        alert(`❌ Failed to delete preset: ${error.message}`);
        console.error("Delete Error:", error);
    }
}

// ✅ Function to reset the laptop table
function resetTable() {
    let tableBody = document.getElementById("laptopTable");
    if (tableBody) {
        tableBody.innerHTML = `<tr><td colspan='11' class='text-center text-muted'>No results available.</td></tr>`;
    }
}



document.addEventListener("DOMContentLoaded", loadPresets);
