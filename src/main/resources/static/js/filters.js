function applyFilters() {
    let filters = {};

    // ✅ Select all visible & hidden inputs and selects
    document.querySelectorAll(".content input, .content select").forEach(el => {
        let value = el.value.trim();
        if (el.type === "checkbox") {
            if (el.checked) filters[el.id.replace("Filter", "")] = "true";
        } else if (value !== "") {
            let key = el.id.replace("Filter", "");
            filters[key] = value;
        }
    });

    // ✅ Handle "Today" and "This Week" filters
    let todayCheckbox = document.getElementById("filterToday")?.checked;
    let thisWeekCheckbox = document.getElementById("filterThisWeek")?.checked;

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

    // ✅ Reset all input fields (text, date, number)
    document.querySelectorAll(".filter-section input").forEach(el => {
        if (el.type === "checkbox") {
            el.checked = false; // ❌ Uncheck all checkboxes
        } else {
            el.value = ""; // Clear text inputs, including date fields
        }
    });

    // ✅ Reset all select dropdowns properly
    document.querySelectorAll(".filter-section select").forEach(el => {
        el.selectedIndex = 0; // Set to first option (usually "Any")
    });

    // ✅ Reset autocomplete fields safely (check if initialized first)
    ["#brand", "#model", "#maisonEnchere", "#gpuModel"].forEach(selector => {
        let field = $(selector);
        if (field.hasClass("ui-autocomplete-input")) {
            field.val("").autocomplete("close");
        }
    });

    // ✅ Collapse all open filter sections
    document.querySelectorAll(".collapsible").forEach(button => {
        let content = button.nextElementSibling;
        content.style.display = "none";
        content.style.maxHeight = "0px";
        button.classList.remove("active"); // Ensure button does not appear active
    });

    // ✅ Log reset action and re-fetch laptops without filters
    console.log("✅ Filters reset. Reloading laptops...");
    fetchLaptops({}); // Fetch all laptops with no filters

    // ✅ Close the filters panel
  //  toggleSidebar();
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

        // ✅ Ensure correct property key (Fixing "maison_enchere")
        let sellers = [...new Set(laptops.map(l => l.maison_enchere || l.maisonEnchere).filter(s => s))].sort();
        let gpuModels = [...new Set(laptops.map(l => l.gpu_model).filter(m => m))].sort();
        let gpuVramOptions = [...new Set(laptops.map(l => l.gpu_vram).filter(v => v))].sort((a, b) => a - b);

        console.log("✅ Loaded Sellers:", sellers.length ? sellers : "⚠️ No sellers found!");
        console.log("✅ Loaded GPU Models:", gpuModels.length ? gpuModels : "⚠️ No GPU models found!");
        console.log("✅ Loaded GPU VRAM Options:", gpuVramOptions.length ? gpuVramOptions : "⚠️ No GPU VRAM options found!");

        // ✅ Enable jQuery UI Autocomplete for Sellers
        if (sellers.length > 0) {
            $("#maisonEnchere").autocomplete({ source: sellers });
        } else {
            console.warn("⚠️ Seller list is empty! Check API response.");
        }

        // ✅ Enable jQuery UI Autocomplete for GPU Model
        if (gpuModels.length > 0) {
            $("#gpuModel").autocomplete({ source: gpuModels });
        } else {
            console.warn("⚠️ GPU model list is empty!");
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
