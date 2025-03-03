function applyFilters() {
    let filters = {};

    // ✅ Ensure collapsible filters are expanded before fetching values
    document.querySelectorAll(".collapsible").forEach(button => {
        let content = button.nextElementSibling;

        // Open all collapsed sections temporarily to collect filter values
        if (content.style.display === "none" || content.style.display === "") {
            content.style.display = "block";
        }
    });

    // ✅ Collect filter values
    document.querySelectorAll(".content input, .content select").forEach(el => {
        let value = el.value.trim();
        console.log(`📌 Checking: ${el.id}, Type: ${el.type}, Value: "${value}"`);

        if (value !== "") {
            let key = el.id.replace("Filter", ""); // ✅ Ensure key matches backend field names
            filters[key] = value;
        }
    });

    console.log("🔍 Captured Filters (Before Sending to API):", filters);

    if (Object.keys(filters).length === 0) {
        console.warn("⚠️ No filters selected. API will fetch all laptops.");
    } else {
        fetchLaptops(filters);
    }

    // ✅ Close collapsible sections after fetching values
    setTimeout(() => {
        document.querySelectorAll(".collapsible").forEach(button => {
            let content = button.nextElementSibling;
            content.style.display = "none";
        });
    }, 300); // Close after a short delay

    toggleSidebar();
}





function resetFilters() {
    document.querySelectorAll(".filter-section input, .filter-section select").forEach(el => {
        el.value = "";
    });

    console.log("🔄 Reset all filters"); // ✅ Debugging Reset Action

    fetchLaptops(); // Reload with no filters
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
        let response = await fetch("http://localhost:9090/api/laptops");
        let laptops = await response.json();

        let sellers = [...new Set(laptops.map(l => l.maisonEnchere).filter(s => s))].sort();
        let conditions = [...new Set(laptops.map(l => l.etatProduitImage).filter(c => c))].sort();

        // ✅ Enable autocomplete for Trusted Seller (Maison Enchère)
        $("#maisonEnchere").autocomplete({ source: sellers });

        // ✅ Enable autocomplete for Condition AI Scanned
        $("#etatProduitImage").autocomplete({ source: conditions });

    } catch (error) {
        console.error("❌ Error loading autocomplete data:", error);
    }
}

document.addEventListener("DOMContentLoaded", loadAutocompleteData);


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
