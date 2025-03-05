const PRESETS = {
    high_end_dell: {
        brand: ["Dell"],
        model: ["XPS", "Latitude 7", "Inspiron 16", "Inspiron 15 Plus"], // ✅ Best Dell Series
        processorBrand: ["Intel"],
        processorModel: ["i7", "i9"], // ✅ Only high-performance processors
        minRam: "16",
        storageType: ["SSD"],
        storageCapacity: "512", // ✅ At least 512GB SSD
        gpuBrand: ["NVIDIA"],
        gpuModel: ["RTX 3060", "RTX 3070", "RTX 3080", "RTX 4060", "RTX 4070"],
        gpuVram: ["8", "12"],
        screenSize: ["15.6", "16"],
        minNoteSur10: "9",
        minBonCoinEstimation: "1200",
        weight: ["< 2.0"], // ✅ Prioritize lightweight laptops
        batteryLife: ["7h", "8h", "9h", "10h"], // ✅ Long battery life
        keyboardBacklight: ["true"], // ✅ Backlit keyboard required
        fingerprintSensor: ["true"], // ✅ Security feature
        faceRecognition: ["true"], // ✅ Face unlock preferred
        releaseYear: ["2022", "2023", "2024"] // ✅ Recent models only
    },
};
