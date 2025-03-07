function applyFilters() {
    let filters = {};

    // ✅ Select all visible & hidden inputs and selects
    document.querySelectorAll(".content input, .content select").forEach(el => {
        let value = el.value.trim();
        // ✅ Get selected EXCLUDED auction houses
        let excludedHouses = [...document.getElementById("excludeMaisonEnchere").selectedOptions].map(opt => opt.value);
        if (excludedHouses.length > 0) {
            filters.excludeMaisonEnchere = excludedHouses.join(",");
        }

        // ✅ Get included auction houses
        let includedHouses = [...document.getElementById("maisonEnchere").selectedOptions].map(opt => opt.value);
        if (includedHouses.length > 0) {
            filters.maisonEnchere = includedHouses.join(",");
        }
        if (el.type === "checkbox") {
            if (el.checked) filters[el.id.replace("Filter", "")] = "true";
        } else if (el.multiple) {
            // ✅ Convert multi-select values to a comma-separated string
            let selectedValues = Array.from(el.selectedOptions).map(option => option.value).join(",");
            if (selectedValues) filters[el.id.replace("Filter", "")] = selectedValues;
        } else if (value !== "") {
            let key = el.id.replace("Filter", "");
            filters[key] = value;
        }
    });

    // ✅ Handle "Today" and "This Week" filters
    let todayCheckbox = document.getElementById("filterToday")?.checked;
    let thisWeekCheckbox = document.getElementById("filterThisWeek")?.checked;
    // ✅ Convert multi-select values to a comma-separated string
    $(".multi-select").each(function () {
        let id = $(this).attr("id");
        let selectedValues = $(this).val(); // Get selected values as an array

        if (selectedValues && selectedValues.length > 0) {
            filters[id] = selectedValues.join(","); // ✅ Convert array to comma-separated string
        }
    });

    // ✅ Ensure numeric values are correctly formatted (remove € symbols)
    ["minBonCoinEstimation", "maxBonCoinEstimation"].forEach(id => {
        let value = document.getElementById(id)?.value.trim();
        if (value !== "" && !isNaN(value.replace(/[^0-9.]/g, ""))) {
            filters[id] = value.replace(/[^0-9.]/g, ""); // ✅ Remove € and non-numeric characters
        }
    });

    if (todayCheckbox) {
        let today = new Date().toISOString().split("T")[0]; // Get YYYY-MM-DD
        filters["minDate"] = today;
        filters["maxDate"] = today;
    }

    if (thisWeekCheckbox) {
        let today = new Date();
        let startOfWeek = new Date(today.setDate(today.getDate() - today.getDay() + 1)).toISOString().split("T")[0]; // Monday
        let endOfWeek = new Date(today.setDate(today.getDate() + 6)).toISOString().split("T")[0]; // Sunday

        filters["minDate"] = startOfWeek;
        filters["maxDate"] = endOfWeek;
    }

    console.log("🔍 Filters Before Sending to API:", filters);

    if (Object.keys(filters).length === 0) {
        console.warn("⚠️ No filters selected. API will fetch all laptops.");
    } else {
        fetchLaptops(filters);
    }

    toggleSidebar();
}


document.addEventListener("DOMContentLoaded", function () {
    document.querySelectorAll(".filter-section input, .filter-section select").forEach(el => {
        el.addEventListener("keypress", function (event) {
            if (event.key === "Enter") {
                event.preventDefault(); // ✅ Prevent unintended behavior
                console.log("⏎ Enter pressed - Clicking Apply Filters...");
                document.getElementById("applyFiltersButton").click(); // ✅ Click "Apply Filters" button
            }
        });
    });

    // ✅ Fix: Prevent "Enter" from toggling collapsibles or sidebar
    document.getElementById("filterPanel").addEventListener("keypress", function (event) {
        if (event.key === "Enter") {
            event.stopPropagation(); // ✅ Stops unwanted toggles
        }
    });
});






function resetFilters() {
    console.log("🔄 Resetting all filters...");

    // ✅ Reset all standard input fields (text, number, date, checkboxes)
    document.querySelectorAll(".filter-section input").forEach(el => {
        if (el.type === "checkbox") {
            el.checked = false; // ❌ Uncheck all checkboxes
        } else {
            el.value = ""; // ✅ Clear text, number, and date inputs
        }
    });

    // ✅ Reset Select2 multi-select dropdowns
    $(".multi-select").each(function () {
        $(this).val(null).trigger("change"); // ✅ Reset Select2 dropdowns properly
    });

    // ✅ Reset normal select dropdowns (non-multi)
    document.querySelectorAll(".filter-section select").forEach(el => {
        if (!el.multiple) {
            el.selectedIndex = 0; // ✅ Reset single select dropdowns
        }
    });

    // ✅ Reset autocomplete fields (if they are initialized)
    ["#brand", "#model", "#maisonEnchere", "#gpuModel"].forEach(selector => {
        let field = $(selector);
        if (field.hasClass("ui-autocomplete-input")) {
            field.val("").autocomplete("close"); // ✅ Clear and close autocomplete
        }
    });

    // ✅ Reset special filters
    document.getElementById("filterToday").checked = false;
    document.getElementById("filterThisWeek").checked = false;
    document.getElementById("minBonCoinEstimation").value = "";
    document.getElementById("maxBonCoinEstimation").value = "";

    // ✅ Reset **Performance Score** dropdowns
    document.getElementById("minNoteSur10").selectedIndex = 0;
    document.getElementById("maxNoteSur10").selectedIndex = 0;

    // ✅ Reset **Release Year** dropdowns
    $("#minReleaseYear").val("").trigger("change");
    $("#maxReleaseYear").val("").trigger("change");

    // ✅ Reset **GPU and Processor** dropdowns
    $("#gpuBrand").val(null).trigger("change");
    $("#gpuVram").val(null).trigger("change");
    $("#processorBrand").val(null).trigger("change");
    $("#processorModel").val(null).trigger("change");

    // ✅ Reset **Auction Date** inputs
    document.getElementById("minDate").value = "";
    document.getElementById("maxDate").value = "";

    // ✅ Keep collapsible filter sections **open**

    // ✅ Ensure API gets **reset filters** and UI stays in sync
    console.log("✅ Filters reset. Reloading laptops...");
    fetchLaptops({}); // Fetch all laptops with no filters
}












function sortTable(columnIndex) {
    let table = document.querySelector(".scrollable-table");
    let tbody = table.querySelector("tbody");
    let rows = Array.from(tbody.querySelectorAll("tr"))
        .filter(row => row.children.length > columnIndex && !row.classList.contains("details-row")); // ✅ Ignore hidden or details rows

    let isAscending = table.getAttribute(`data-sort-${columnIndex}`) !== "asc";
    table.setAttribute(`data-sort-${columnIndex}`, isAscending ? "asc" : "desc");

    rows.sort((rowA, rowB) => {
        let cellA = rowA.children[columnIndex]?.textContent?.trim() || "";
        let cellB = rowB.children[columnIndex]?.textContent?.trim() || "";

        // ✅ Convert numbers for proper sorting
        let numA = parseFloat(cellA.replace(/[^0-9.-]+/g, ""));
        let numB = parseFloat(cellB.replace(/[^0-9.-]+/g, ""));

        if (!isNaN(numA) && !isNaN(numB)) {
            return isAscending ? numA - numB : numB - numA;
        }

        return isAscending ? cellA.localeCompare(cellB) : cellB.localeCompare(cellA);
    });

    // ✅ Clear and append sorted rows
    tbody.innerHTML = "";
    rows.forEach(row => tbody.appendChild(row));
}



async function loadAutocompleteData() {
    try {
        let response = await fetch(`${BASE_URL}/api/laptops`);
        let laptops = await response.json();

        let sellers = [...new Set(laptops.map(l => l.maison_enchere || l.maisonEnchere).filter(s => s))].sort();
        let baseModels = new Set();
        let fullModelsMap = new Map(); // Stores base models as keys, full models as values
        let referencesMap = new Map(); // Stores base models as keys, reference numbers as values
        let gpuVramOptions = [...new Set(laptops.map(l => l.gpu_vram).filter(v => v))].sort((a, b) => a - b);
      //  let laptopModels = [...new Set(laptops.map(l => l.model).filter(m => m))].sort(); // ✅ Extract Laptop Models
        let gpuModels = [...new Set(laptops.map(l => l.gpu_model).filter(m => m))].sort();


        laptops.forEach(laptop => {
            let model = laptop.model;
            if (model) {
                let parts = model.split(" ");
                if (parts.length > 1) {
                    let baseModel = parts[0]; // First word as base model
                    let reference = parts.slice(1).join(" "); // Everything after first word as reference

                    baseModels.add(baseModel); // Store base model

                    // Store full models grouped by base model
                    if (!fullModelsMap.has(baseModel)) {
                        fullModelsMap.set(baseModel, new Set());
                    }
                    fullModelsMap.get(baseModel).add(model);

                    // Store references grouped by base model
                    if (!referencesMap.has(baseModel)) {
                        referencesMap.set(baseModel, new Set());
                    }
                    referencesMap.get(baseModel).add(reference);
                }
            }
        });

        let sortedBaseModels = [...baseModels].sort();
        let sortedLaptopModels = [...fullModelsMap.values()].flatMap(set => [...set]).sort();
        let sortedReferences = [...referencesMap.values()].flatMap(set => [...set]).sort();

        // ✅ Populate Base Models
        $("#baseModel").empty().select2({
            placeholder: "Select base models...",
            allowClear: true,
            multiple: true,
            width: "100%",
            data: sortedBaseModels.map(model => ({ id: model, text: model }))
        });

        // ✅ Populate Full Models Initially (all models)
        $("#model").empty().select2({
            placeholder: "Select full models...",
            allowClear: true,
            multiple: true,
            width: "100%",
            data: sortedLaptopModels.map(model => ({ id: model, text: model }))
        });

        // ✅ Populate Reference Numbers Initially (all references)
        $("#reference").empty().select2({
            placeholder: "Select reference...",
            allowClear: true,
            multiple: true,
            width: "100%",
            data: sortedReferences.map(ref => ({ id: ref, text: ref }))
        });

        // ✅ Filter Full Models and References Based on Base Model Selection
        $("#baseModel").on("change", function () {
            let selectedBaseModels = $(this).val() || [];
            let filteredModels = selectedBaseModels.length > 0
                ? selectedBaseModels.flatMap(base => [...(fullModelsMap.get(base) || [])])
                : sortedLaptopModels;

            let filteredReferences = selectedBaseModels.length > 0
                ? selectedBaseModels.flatMap(base => [...(referencesMap.get(base) || [])])
                : sortedReferences;

            $("#model").empty().select2({
                placeholder: "Select full models...",
                allowClear: true,
                multiple: true,
                width: "100%",
                data: filteredModels.map(model => ({ id: model, text: model }))
            });

            $("#reference").empty().select2({
                placeholder: "Select reference...",
                allowClear: true,
                multiple: true,
                width: "100%",
                data: filteredReferences.map(ref => ({ id: ref, text: ref }))
            });
        });
        // ✅ Populate "Trusted Sellers" (maisonEnchere)
        if (sellers.length > 0) {
            $("#maisonEnchere").empty().select2({
                placeholder: "Select trusted sellers...",
                allowClear: true,
                multiple: true,
                width: "100%",
                data: sellers.map(seller => ({ id: seller, text: seller }))
            });

            // ✅ Populate "Excluded Sellers" (excludeMaisonEnchere)
            $("#excludeMaisonEnchere").empty().select2({
                placeholder: "Exclude sellers...",
                allowClear: true,
                multiple: true,
                width: "100%",
                data: sellers.map(seller => ({ id: seller, text: seller }))
            });
        } else {
            console.warn("⚠️ Seller list is empty! Check API response.");
        }
        // ✅ Populate GPU VRAM Dropdown
        let gpuVramSelect = document.getElementById("gpuVram");
        if (gpuVramOptions.length > 0) {
            gpuVramSelect.innerHTML = `<option value="">Any</option>` +
                gpuVramOptions.map(v => `<option value="${v}">${v}GB</option>`).join("");
        }
        // ✅ Enable Select2 Multi-Select for GPU Models
        if (gpuModels.length > 0) {
            $("#gpuModel").empty().select2({
                placeholder: "Select GPU models...",
                allowClear: true,
                multiple: true,
                width: "100%",
                data: gpuModels.map(model => ({ id: model, text: model })) // ✅ Convert to Select2 format
            });
        } else {
            console.warn("⚠️ GPU model list is empty!");
        }


    } catch (error) {
        console.error("❌ Error loading autocomplete data:", error);
    }
}

// ✅ Initialize Data on Page Load
$(document).ready(loadAutocompleteData);



// ✅ Run on page load
document.addEventListener("DOMContentLoaded", loadAutocompleteData);



// EXISTING: Check if a laptop is in favorites from localStorage
function checkIfFavorite(laptopId) {
    let favorites = JSON.parse(localStorage.getItem("favorites")) || [];
    return favorites.includes(laptopId);
}




// ✅ Helper function to populate dropdowns dynamically
function populateDropdown(elementId, values) {
    let select = document.getElementById(elementId);
    if (select) {
        select.innerHTML = `<option value="">Any</option>` + values.map(value => `<option value="${value}">${value}</option>`).join("");
    }
}

// ✅ Load data on page load
document.addEventListener("DOMContentLoaded", loadAutocompleteData);



document.getElementById("filterToggle").addEventListener("click", toggleSidebar);



document.querySelectorAll(".collapsible").forEach(button => {
    button.addEventListener("click", function () {
        this.classList.toggle("active");
        let content = this.nextElementSibling;

        // ✅ Use computedStyle to check if already visible
        if (window.getComputedStyle(content).display === "none") {
            content.style.display = "block";
            content.style.maxHeight = content.scrollHeight + "px"; // Smooth expand
        } else {
            content.style.maxHeight = "0px"; // Collapse smoothly
            setTimeout(() => content.style.display = "none", 300); // Hide after animation
        }
    });
});





$(document).ready(function () {
    // ✅ Initialize Select2 for all multi-select fields
    $(".multi-select").each(function () {
        let $select = $(this);
        if (!$select.hasClass("select2-hidden-accessible")) {
            $select.select2({
                placeholder: "Select options...",
                allowClear: true,
                width: "100%"
            });
        }
    });

    // ✅ Dynamically fetch laptop models
    async function loadLaptopModels() {
        try {
            let response = await fetch(`${BASE_URL}/api/laptops`);
            let laptops = await response.json();

            let laptopModels = [...new Set(laptops.map(l => l.model).filter(m => m))].sort();
            console.log("✅ Loaded Laptop Models:", laptopModels.length ? laptopModels : "⚠️ No models found!");

            // ✅ Apply Select2 after data is loaded
            $("#model").empty().select2({
                placeholder: "Search and select models...",
                allowClear: true,
                multiple: true,
                width: "100%",
                data: laptopModels.map(model => ({ id: model, text: model }))
            });

        } catch (error) {
            console.error("❌ Error loading laptop models:", error);
        }
    }

    loadLaptopModels();
});



