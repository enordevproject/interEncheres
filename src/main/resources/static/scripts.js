function toggleSidebar() {
    document.getElementById("filterPanel").classList.toggle("active");
    document.getElementById("filterToggle").style.display =
        document.getElementById("filterPanel").classList.contains("active") ? "none" : "block";
}

document.getElementById("filterToggle").addEventListener("click", toggleSidebar);

async function fetchLaptops(filters = {}) {
    let filteredParams = Object.fromEntries(
        Object.entries(filters).filter(([_, value]) => value !== "")
    );
    let query = new URLSearchParams(filteredParams).toString();
    let endpoint = query
        ? `http://localhost:9090/api/laptops/filter?${query}`
        : "http://localhost:9090/api/laptops";

    console.log("🔎 Fetching laptops from:", endpoint);

    try {
        let response = await fetch(endpoint);
        if (!response.ok) {
            console.error("❌ API Error:", response.statusText);
            document.getElementById("laptopTable").innerHTML =
                "<tr><td colspan='11' class='text-center text-danger'>❌ API Error</td></tr>";
            return;
        }

        let laptops = await response.json();
        document.getElementById("resultsCount").textContent = laptops.length;
        console.log("✅ Received laptops:", laptops.length, "items");

        let tableBody = document.getElementById("laptopTable");
        tableBody.innerHTML = laptops.length
            ? laptops.map((laptop, index) => {
                let isFavorite = checkIfFavorite(laptop.id);
                // **Filter out bad data**
                let brand = (laptop.brand && !["N/A", "Unknown", ""].includes(laptop.brand)) ? laptop.brand : "";
                let model = (laptop.model && !["N/A", "Unknown", ""].includes(laptop.model)) ? laptop.model : "";
                let processorBrand = (laptop.processor_brand && !["N/A", "Unknown", ""].includes(laptop.processor_brand)) ? laptop.processor_brand : "";
                let processorModel = (laptop.processor_model && !["N/A", "Unknown", ""].includes(laptop.processor_model)) ? laptop.processor_model : "";

                // If essential details are missing, don't show the row
                if (!brand && !model && !processorBrand && !processorModel) return "";

                return `

                    <tr class="laptop-row" onclick="toggleDetails(${index}, event)">

                        <td><img src="${laptop.img_url}" style="max-width: 80px;"></td>
                        <td><a href="${laptop.lot_url}" target="_blank">${laptop.lot_number}</a></td>
                         <td>${laptop.date}</td>
                        <td>${brand}</td>
                        <td>${model}</td>
                        <td>${processorBrand} ${processorModel}</td>
                        <td>${laptop.ram_size ? laptop.ram_size + 'GB' : ''}</td>
                        <td>${laptop.storage_type || ''} ${laptop.storage_capacity ? laptop.storage_capacity + 'GB' : ''}</td>
                        <td>${laptop.screen_size ? laptop.screen_size + ' inches' : ''}</td>
                        <td><b>${laptop.bon_coin_estimation || ''}</b></td>
                        <td>${laptop.maison_enchere || ''}</td>
                        <td>${laptop.recommended_to_buy ? '✅ Yes' : '❌ No'}</td>
                         <td>
                    <button class="favorite-btn" onclick="toggleFavorite(${laptop.id})">
                        ${isFavorite ? '❤️' : '🤍'}
                    </button>
                </td>
                    </tr>
                   <tr class="details-row" id="details-${index}">
    <td colspan="11" class="details-content">
        <b>📋 Description:</b> ${laptop.description || "No description available"} <br>
        <b>🏠 Auction House:</b> ${laptop.maison_enchere || "Unknown"} <br>
        <b>📅 Auction Date:</b> ${laptop.date || "Unknown"} <br>
        <b>🔢 Lot Number:</b> ${laptop.lot_number} <br>
        <b>🔗 Lot URL:</b> <a href="${laptop.lot_url}" target="_blank">${laptop.lot_url}</a> <br>

        <b>💻 Full Specifications:</b> <br>
        ${laptop.brand ? `🏷️ Brand: ${laptop.brand} <br>` : ""}
        ${laptop.model ? `🆔 Model: ${laptop.model} <br>` : ""}
        ${laptop.processor_brand ? `🔎 Processor: ${laptop.processor_brand} ${laptop.processor_model} (${laptop.processor_cores} Cores, ${laptop.processor_clock_speed} GHz) <br>` : ""}
        ${laptop.ram_size ? `💾 RAM: ${laptop.ram_size}GB ${laptop.ram_type} <br>` : ""}
        ${laptop.storage_type ? `💽 Storage: ${laptop.storage_type} ${laptop.storage_capacity}GB (${laptop.storage_speed} MB/s) <br>` : ""}
        ${laptop.gpu_type ? `🎮 GPU: ${laptop.gpu_type} ${laptop.gpu_model} (${laptop.gpu_vram}GB VRAM) <br>` : ""}
        ${laptop.screen_size ? `🖥️ Screen: ${laptop.screen_size}" (${laptop.screen_resolution})<br>` : ""}
        ${laptop.touch_screen ? `🖐️ Touchscreen: Yes ✅ <br>` : ""}
        ${laptop.keyboard_backlight ? `💡 Keyboard Backlight: Yes ✅ <br>` : ""}
        ${laptop.keyboard_type ? `⌨️ Keyboard Type: ${laptop.keyboard_type} <br>` : ""}
        ${laptop.battery_life ? `🔋 Battery Life: ${laptop.battery_life} <br>` : ""}
        ${laptop.weight ? `⚡ Weight: ${laptop.weight}kg <br>` : ""}
        ${laptop.operating_system ? `💻 OS: ${laptop.operating_system} <br>` : ""}
        ${laptop.chassis_material ? `🏗️ Chassis Material: ${laptop.chassis_material} <br>` : ""}
        ${laptop.fingerprint_sensor ? `🔒 Security: Fingerprint ✅<br>` : ""}
        ${laptop.face_recognition ? `🔒 Security: Face Recognition ✅<br>` : ""}
        ${laptop.connectivity ? `📡 Connectivity: ${laptop.connectivity} <br>` : ""}

        <b>🔍 Product Condition:</b> ${laptop.product_condition || "Unknown"} <br>
        ${laptop.etat_produit_image ? `📷 Condition Analysis: ${laptop.etat_produit_image} <br>` : ""}
        ${laptop.reason_for_condition ? `⚠️ Condition Details: ${laptop.reason_for_condition} <br>` : ""}

        <b>🛡️ Warranty:</b> ${laptop.warranty || "Not specified"} <br>
        <b>📅 Release Year:</b> ${laptop.release_year || "Unknown"} <br>

        <b>💰 Price Estimations:</b> <br>
        ${laptop.bon_coin_estimation ? `🔹 Le Bon Coin: ${laptop.bon_coin_estimation} <br>` : ""}
        ${laptop.facebook_estimation ? `🔹 Facebook Marketplace: ${laptop.facebook_estimation} <br>` : ""}
        ${laptop.internet_estimation ? `🔹 Online Estimate: ${laptop.internet_estimation} <br>` : ""}
        ${laptop.prix_neuf ? `🆕 New Price Estimate: ${laptop.prix_neuf} € <br>` : ""}

        <b>📍 Location:</b> ${laptop.ville || "Unknown"}, ${laptop.code_postal || "Unknown"} <br>

        <b>⭐ Score:</b> ${laptop.note_sur_10}/10 <br>
        <b>✅ Recommended to Buy:</b> ${laptop.recommended_to_buy ? "Yes ✅" : "No ❌"} <br>
    </td>
</tr>


                `;
            }).join("")
            : "<tr><td colspan='11' class='text-center text-danger'>⚠️ No laptops found.</td></tr>";
    } catch (error) {
        console.error("❌ Fetch error:", error);
        document.getElementById("laptopTable").innerHTML =
            "<tr><td colspan='11' class='text-center text-danger'>❌ Error loading data.</td></tr>";
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


$(document).ready(() => {
    fetchLaptops();
    loadAutocompleteData();
});
document.addEventListener("DOMContentLoaded", function () {
    document.querySelector("button[onclick='applyFilters()']").addEventListener("click", applyFilters);
    fetchLaptops(); // Load initial laptops
});

function checkIfFavorite(laptopId) {
    let favorites = JSON.parse(localStorage.getItem("favorites")) || [];
    return favorites.includes(laptopId);
}

async function toggleFavorite(laptopId) {
    try {
        // Get the button element
        let button = document.querySelector(`button[onclick="toggleFavorite(${laptopId})"]`);

        // Determine current state and toggle it
        let isFavorite = button.innerHTML.trim() === "❤️"; // Check if currently favorited

        // Send API request to toggle favorite
        let response = await fetch(`http://localhost:9090/api/laptops/favorite/${laptopId}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ favorite: !isFavorite }) // Toggle favorite status
        });

        if (!response.ok) {
            throw new Error(`❌ API request failed: ${response.status} ${response.statusText}`);
        }

        console.log("✅ Favorite updated successfully.");

        // ✅ Toggle icon instantly without reloading
        button.innerHTML = !isFavorite ? "❤️" : "🤍";

        updateFavoritePanel(); // Refresh the favorite list panel
    } catch (error) {
        console.error("❌ Error updating favorite:", error);
    }
}




// ✅ Open All Favorite Laptop URLs in New Tabs
function openAllFavorites() {
    fetch("http://localhost:9090/api/laptops/favorites")
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to fetch favorites");
            }
            return response.json();
        })
        .then(favorites => {
            if (favorites.length === 0) {
                alert("No favorite laptops to open.");
                return;
            }

            // Open all URLs in separate tabs
            favorites.forEach(laptop => {
                if (laptop.lot_url && laptop.lot_url.trim()) {
                    window.open(laptop.lot_url, "_blank");
                }
            });
        })
        .catch(error => console.error("❌ Error opening favorite URLs:", error));
}


async function clearFavorites() {
    try {
        let response = await fetch("http://localhost:9090/api/laptops/favorites", {
            method: "DELETE",
            headers: { "Content-Type": "application/json" }
        });

        if (!response.ok) {
            console.error("❌ Failed to delete favorites:", response.status);
            alert("Failed to clear favorites. Please try again.");
            return;
        }

        let message = await response.text();
        console.log("✅ Favorites cleared successfully:", message);
        alert(message);

        // ✅ Clear UI: Reset favorite panel
        updateFavoritePanel();

        // ✅ Clear favorite icons in the table
        document.querySelectorAll(".favorite-btn").forEach(btn => {
            btn.innerHTML = "🤍"; // Reset all favorite icons to unselected
        });

    } catch (error) {
        console.error("❌ Error clearing favorites:", error);
    }
}




async function updateFavoritePanel() {
    try {
        let response = await fetch("http://localhost:9090/api/laptops/favorites");
        if (!response.ok) throw new Error("Failed to fetch favorites");

        let favorites = await response.json();
        console.log("✅ Loaded favorites:", favorites);

        let favoriteList = document.getElementById("favoriteList");
        let favoriteCount = document.getElementById("favoriteCount");

        favoriteCount.textContent = favorites.length;

        if (favorites.length === 0) {
            favoriteList.innerHTML = "<p class='no-favorites'>No favorites yet.</p>";
        } else {
            favoriteList.innerHTML = favorites.map(laptop => `
                <div class="favorite-item">
                    <img src="${laptop.img_url || 'default-image.png'}" alt="Laptop" class="favorite-img">
                    <p>${laptop.brand || 'Unknown'} ${laptop.model || 'Unknown'} (${laptop.maison_enchere || 'Unknown Seller'})</p>
                    <a href="${laptop.lot_url || '#'}" target="_blank">🔗 Open</a>

                    <!-- 🗑️ Remove Button (Small and inside the favorite item) -->
                    <button class="remove-favorite-btn" onclick="removeFavorite(${laptop.id})">🗑️</button>
                </div>
            `).join("");
        }
    } catch (error) {
        console.error("❌ Error loading favorites:", error);
    }
}
async function removeFavorite(laptopId) {
    try {
        let response = await fetch(`http://localhost:9090/api/laptops/favorite/${laptopId}`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ favorite: false }) // Remove the favorite
        });

        if (!response.ok) {
            throw new Error("❌ Failed to remove favorite");
        }

        console.log(`✅ Laptop ID ${laptopId} removed from favorites.`);
        updateFavoritePanel(); // Refresh UI to remove from the list
    } catch (error) {
        console.error("❌ Error removing favorite:", error);
    }
}




// ✅ Call this function when page loads
document.addEventListener("DOMContentLoaded", updateFavoritePanel);



// Initialize favorite panel on load
updateFavoritePanel();
