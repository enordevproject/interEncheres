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

async function fetchLogs() {
    try {
        let response = await fetch("http://localhost:9090/api/search/logs");
        if (!response.ok) throw new Error("Failed to fetch logs.");
        let logs = await response.json();
        updateLogs(logs);
    } catch (error) {
        console.error("❌ Error fetching logs:", error);
    }
}


async function processLotsWithGPT() {
    const processEndpoint = 'http://localhost:9090/api/lots/process';
    let resultsPanel = document.getElementById("resultsPanel");

    if (resultsPanel) resultsPanel.innerHTML = "🔄 Processing Lots with GPT...";

    // ✅ Show progress bar
    //showProgress();

    try {
        let response = await fetch(processEndpoint, { method: 'POST' });
        let result = await response.json();

        if (!response.ok) throw new Error(result.message);

        console.log('✅ GPT processing completed:', result.message);

        if (resultsPanel) resultsPanel.innerHTML = "✅ Lots Processed Successfully!";
        await fetchLaptops(); // ✅ Refresh data

    } catch (error) {
        console.error('❌ GPT Processing failed:', error);
        if (resultsPanel) {
            resultsPanel.innerHTML = `<span style="color: red;">❌ Error: ${error.message}</span>`;
        }
        alert('Error processing lots: ' + error.message);
    } finally {
        // ✅ Hide progress bar when done
        //  hideProgress();
    }
}

async function addToInterencheresFavorites(lotUrl) {
    try {
        console.log(`🔄 Sending request to add ${lotUrl} to favorites...`);

        let response = await fetch("http://localhost:9090/api/interencheres/favorite", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ lotUrl: lotUrl }) // Ensure correct payload format
        });

        let result = await response.text(); // Get response text

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
    fetchLaptops(); // Load initial laptops
});



