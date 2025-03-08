async function downloadFavoritesExcel() {
    try {
        let response = await fetch(`${BASE_URL}/api/laptops/favorites/excel`);
        if (!response.ok) {
            throw new Error(`❌ Failed to download: ${response.statusText}`);
        }

        let blob = await response.blob();
        let url = window.URL.createObjectURL(blob);
        let a = document.createElement("a");
        a.href = url;
        a.download = "favorite_laptops.xlsx";
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);

        console.log("✅ Excel report downloaded.");
    } catch (error) {
        console.error("❌ Error downloading report:", error);
    }
}
