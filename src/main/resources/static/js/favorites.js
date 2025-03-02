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
                    
                    <!-- 🔗 Open Lot URL -->
                    <a href="${laptop.lot_url || '#'}" target="_blank" class="favorite-link">🔗 Open</a>

                    <!-- ⭐ Add to Interencheres Favorites -->
                    <a href="#" onclick="addToInterencheresFavorites('${laptop.lot_url}')" 
                        title="Add" class="favorite-link">
                        ⭐ Add to Favorites
                    </a>

                    <!-- 🗑️ Remove from Favorites -->
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

