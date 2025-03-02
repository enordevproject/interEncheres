function applyFilters() {
    let filters = Object.fromEntries(
        [...document.querySelectorAll(".filter-section input, .filter-section select")]
            .map(el => [el.id.replace("Filter", ""), el.value.trim()])
            .filter(([_, value]) => value !== "") // Remove empty filters
    );

    fetchLaptops(filters);
    toggleSidebar();
}


function resetFilters() {
    document.querySelectorAll(".filter-section input, .filter-section select").forEach(el => el.value = "");
    fetchLaptops();
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
    let response = await fetch("http://localhost:9090/api/laptops");
    let laptops = await response.json();

    let brands = [...new Set(laptops.map(l => l.brand))];
    let models = [...new Set(laptops.map(l => l.model))];
    let processors = [...new Set(laptops.map(l => l.processorBrand + " " + l.processorModel))];
    let storages = [...new Set(laptops.map(l => l.storageType))];

    $("#brandFilter").autocomplete({ source: brands });
    $("#modelFilter").autocomplete({ source: models });
    $("#processorFilter").autocomplete({ source: processors });
    $("#storageFilter").autocomplete({ source: storages });
}


document.getElementById("filterToggle").addEventListener("click", toggleSidebar);
