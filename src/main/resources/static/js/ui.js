function toggleSidebar() {
    let filterPanel = document.getElementById("filterPanel");
    let filterToggle = document.getElementById("filterToggle");

    if (filterPanel) {
        filterPanel.classList.toggle("active");
    }

    if (filterToggle) {
        // Instead of hiding the button, adjust its visibility carefully
        if (filterPanel.classList.contains("active")) {
            filterToggle.style.opacity = "0"; // Hide visually but keep the space
        } else {
            filterToggle.style.opacity = "1"; // Make it visible again
        }
    }
}

function toggleDetails(index, event) {
    // Prevent toggling if the click event originates from the favorite button
    if (event.target.classList.contains("favorite-btn")) {
        return;
    }

    let detailsRow = document.getElementById(`details-${index}`);
    detailsRow.style.display = detailsRow.style.display === "none" ? "table-row" : "none";
}