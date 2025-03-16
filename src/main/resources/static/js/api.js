// NEW: Set dynamic base URL and print it
const BASE_URL = window.location.origin;
console.log("🔍 Using BASE_URL:", BASE_URL);

// EXISTING: Modified fetchLaptops to use dynamic BASE_URL
async function fetchLaptops(filters = {}) {
    let filteredParams = Object.fromEntries(
        Object.entries(filters).filter(([_, value]) => value !== "")
    );
    console.log("🔍 Filters Before API Call:", filteredParams);
    let query = new URLSearchParams(filteredParams).toString();
    let endpoint = query ? `${BASE_URL}/api/laptops/filter?${query}` : `${BASE_URL}/api/laptops`;
    console.log("🌍 FULL FILTERED URL:", endpoint);

    try {
    //  await deleteExpiredLaptops();
        let response = await fetch(endpoint);
        if (!response.ok) {
            console.error("❌ API Error:", response.statusText);
            document.getElementById("laptopTable").innerHTML =
                "<tr><td colspan='11' class='text-center text-danger'>❌ API Error</td></tr>";
            return;
        }
        let laptops = await response.json();
        console.log("✅ Received laptops:", laptops.length, "items");



        // ✅ Sort by closest auction date (ascending order)
        laptops.sort((a, b) => new Date(a.date) - new Date(b.date));

        document.getElementById("resultsCount").textContent = laptops.length;
        updateLaptopTable(laptops);
    } catch (error) {
        console.error("❌ Fetch error:", error);
    }
}

async function deleteExpiredLaptops() {
    try {
        let response = await fetch(`${BASE_URL}/api/laptops/deleteExpired`, { method: "DELETE" });
        if (!response.ok) {
            throw new Error(`❌ Failed to delete expired laptops: ${response.statusText}`);
        }
        console.log("✅ Expired laptops deleted successfully.");
    } catch (error) {
        console.error("❌ Error deleting expired laptops:", error);
    }
}


// EXISTING: Modified fetchLogs to use dynamic BASE_URL
async function fetchLogs() {
    try {
        let response = await fetch(`${BASE_URL}/api/search/logs`);
        if (!response.ok) throw new Error("Failed to fetch logs.");
        let logs = await response.json();
        updateLogs(logs);
    } catch (error) {
        console.error("❌ Error fetching logs:", error);
    }
}

// EXISTING: Modified processLotsWithGPT to use dynamic BASE_URL
async function processLotsWithGPT() {
    const processEndpoint = `${BASE_URL}/api/lots/process`;
    let resultsPanel = document.getElementById("resultsPanel");
    if (resultsPanel) resultsPanel.innerHTML = "🔄 Processing Lots with GPT...";
    try {
        let response = await fetch(processEndpoint, { method: 'POST' });
        let result = await response.json();
        if (!response.ok) throw new Error(result.message);
        console.log('✅ GPT processing completed:', result.message);
        if (resultsPanel) resultsPanel.innerHTML = "✅ Lots Processed Successfully!";
        await fetchLaptops();
    } catch (error) {
        console.error('❌ GPT Processing failed:', error);
        if (resultsPanel) {
            resultsPanel.innerHTML = `<span style="color: red;">❌ Error: ${error.message}</span>`;
        }
        alert('Error processing lots: ' + error.message);
    }
}

// EXISTING: Modified addToInterencheresFavorites to use dynamic BASE_URL
async function addToInterencheresFavorites(lotUrl) {
    try {
        console.log(`🔄 Sending request to add ${lotUrl} to favorites...`);
        let response = await fetch(`${BASE_URL}/api/interencheres/favorite`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ lotUrl: lotUrl })
        });
        let result = await response.text();
        if (!response.ok) throw new Error(result);
        console.log("✅ Lot added successfully:", result);
        alert("✅ Lot added to Interencheres favorites!");
    } catch (error) {
        console.error("❌ Error adding to favorites:", error);
        alert("❌ Could not add lot to favorites. " + error.message);
    }
}

$(document).ready(() => {
    fetchLaptops();
    loadAutocompleteData();
});
document.addEventListener("DOMContentLoaded", function () {
    document.querySelector("button[onclick='applyFilters()']").addEventListener("click", applyFilters);
    fetchLaptops();
});

function updateLaptopTable(laptops) {
    let tableBody = document.getElementById("laptopTable");
    tableBody.innerHTML = laptops.length
        ? laptops.map((laptop, index) => {
            let isFavorite = checkIfFavorite(laptop.id);
            let brand = laptop.brand && !["N/A", "Unknown", ""].includes(laptop.brand) ? laptop.brand : "";
            let model = laptop.model && !["N/A", "Unknown", ""].includes(laptop.model) ? laptop.model : "";
            let processorBrand = laptop.processor_brand && !["N/A", "Unknown", ""].includes(laptop.processor_brand) ? laptop.processor_brand : "";
            let processorModel = laptop.processor_model && !["N/A", "Unknown", ""].includes(laptop.processor_model) ? laptop.processor_model : "";
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
        <b>📋 Description:</b> ${laptop.description || "❌ No description available"} <br>
        <b>🏠 Auction House:</b> ${laptop.maison_enchere || "❌ Unknown"} <br>
        <b>📅 Auction Date:</b> ${laptop.date || "❌ Unknown"} <br>
        <b>🔢 Lot Number:</b> ${laptop.lot_number} <br>
        <b>🔗 Lot URL:</b> <a href="${laptop.lot_url}" target="_blank" style="color: blue; text-decoration: underline;">🌍 View Auction</a> <br>


        <b>💻 Full Specifications:</b> <br>
        ${laptop.brand ? `🏷️ <b>Brand:</b> ${laptop.brand} <br>` : ""}
        ${laptop.model ? `🆔 <b>Model:</b> ${laptop.model} <br>` : ""}
        ${laptop.processor_brand ? `🔎 <b>Processor:</b> ${laptop.processor_brand} ${laptop.processor_model} <br>` : ""}
        ${laptop.ram_size ? `💾 <b>RAM:</b> ${laptop.ram_size}GB (${laptop.ram_type}) <br>` : ""}
        ${laptop.storage_type ? `💽 <b>Storage:</b> ${laptop.storage_type} ${laptop.storage_capacity}GB <br>` : ""}
        ${laptop.gpu_type ? `🎮 <b>Graphics:</b> ${laptop.gpu_type} ${laptop.gpu_model} (${laptop.gpu_vram}GB VRAM) <br>` : ""}
        ${laptop.screen_size ? `🖥️ <b>Screen:</b> ${laptop.screen_size}" (${laptop.screen_resolution}) <br>` : ""}
        ${laptop.operating_system ? `🖥️ <b>OS:</b> ${laptop.operating_system} <br>` : ""}

        <b>📍 Location:</b> ${laptop.ville || "❌ Unknown"}, ${laptop.code_postal || "❌ Unknown"} <br>
        <b>⭐ Score:</b> <b>${laptop.note_sur_10}/10</b> 🏆 (${laptop.reason_for_score || "No reason provided"}) <br>
        <b>🛠️ Condition:</b> <b>${laptop.product_condition}</b> 🏷️ (${laptop.reason_for_condition || "No details"}) <br>
        <b>🛍️ Recommended to Buy:</b> ${laptop.recommended_to_buy ? "✅ Yes" : "❌ No"} <br>
        <b>🖼️ IA Image Analysis:</b> ${laptop.etat_produit_image || "❌ No analysis"} <br>

        <b>💰 Estimated Prices:</b> <br>
        <b>📌 Le Bon Coin:</b> ${laptop.bon_coin_estimation ? laptop.bon_coin_estimation + "€" : "❌ No estimate"} <br>
        <b>📌 Facebook Marketplace:</b> ${laptop.facebook_estimation ? laptop.facebook_estimation + "€" : "❌ No estimate"} <br>
        <b>📌 Internet Estimate:</b> ${laptop.internet_estimation ? laptop.internet_estimation + "€" : "❌ No estimate"} <br>
    </td>
</tr>

            `;
        }).join("")
        : "<tr><td colspan='11' class='text-center text-danger'>⚠️ No laptops found.</td></tr>";
}
