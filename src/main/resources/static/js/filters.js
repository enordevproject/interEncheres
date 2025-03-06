function applyFilters() {
    let filters = {};

    // ✅ Select all visible & hidden inputs and selects
    document.querySelectorAll(".content input, .content select").forEach(el => {
        let value = el.value.trim();

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
    document.querySelectorAll(".collapsible").forEach(button => {
        let content = button.nextElementSibling;
        content.style.display = "block"; // Ensure content stays visible
        content.style.maxHeight = content.scrollHeight + "px"; // Keep expanded
        button.classList.add("active"); // Keep button visually active
    });

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



// EXISTING: Modified loadAutocompleteData function to use dynamic BASE_URL
async function loadAutocompleteData() {
    try {
        let response = await fetch(`${BASE_URL}/api/laptops`);
        let laptops = await response.json();

        // ✅ Extract unique values from the API response
        let sellers = [...new Set(laptops.map(l => l.maison_enchere || l.maisonEnchere).filter(s => s))].sort();
        let gpuModels = [...new Set(laptops.map(l => l.gpu_model).filter(m => m))].sort();
        let gpuVramOptions = [...new Set(laptops.map(l => l.gpu_vram).filter(v => v))].sort((a, b) => a - b);
        let laptopModels = [...new Set(laptops.map(l => l.model).filter(m => m))].sort(); // ✅ Extract Laptop Models

        console.log("✅ Loaded Sellers:", sellers.length ? sellers : "⚠️ No sellers found!");
        console.log("✅ Loaded GPU Models:", gpuModels.length ? gpuModels : "⚠️ No GPU models found!");
        console.log("✅ Loaded Laptop Models:", laptopModels.length ? laptopModels : "⚠️ No laptop models found!");
        console.log("✅ Loaded GPU VRAM Options:", gpuVramOptions.length ? gpuVramOptions : "⚠️ No GPU VRAM options found!");

        // ✅ Enable jQuery UI Autocomplete for Sellers
        if (sellers.length > 0) {
            $("#maisonEnchere").empty().select2({
                placeholder: "Select trusted sellers...",
                allowClear: true,
                multiple: true,
                width: "100%",
                data: sellers.map(seller => ({ id: seller, text: seller }))
            });
        } else {
            console.warn("⚠️ Seller list is empty! Check API response.");
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

        // ✅ Enable Select2 Multi-Select for Model
        if (laptopModels.length > 0) {
            $("#model").empty().select2({
                placeholder: "Select or search models...",
                allowClear: true,
                multiple: true,
                width: "100%",
                tags: false, // ✅ Only allow selecting models from database
                data: laptopModels.map(model => ({ id: model, text: model })) // ✅ Convert to Select2 format
            });
        } else {
            console.warn("⚠️ Laptop model list is empty!");
        }

        // ✅ Populate GPU VRAM Dropdown
        let gpuVramSelect = document.getElementById("gpuVram");
        if (gpuVramOptions.length > 0) {
            gpuVramSelect.innerHTML = `<option value="">Any</option>` +
                gpuVramOptions.map(v => `<option value="${v}">${v}GB</option>`).join("");
        }

    } catch (error) {
        console.error("❌ Error loading autocomplete data:", error);
    }
}


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


function applyPreset(type) {
    resetFilters(); // Clear all previous filters

    if (type === "gaming") {
        document.getElementById("processorBrand").value = "Intel";
        document.getElementById("minNoteSur10").value = "8";
    } else if (type === "budget") {
        document.getElementById("maxBonCoinEstimation").value = "500";
    } else if (type === "premium") {
        document.getElementById("minNoteSur10").value = "9";
        document.getElementById("processorBrand").value = "Apple";
    }

    applyFilters(); // Apply filters after setting the values
}



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



