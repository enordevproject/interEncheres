package Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "laptop")
public class Laptop {
    public Laptop(){

    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(int lotNumber) {
        this.lotNumber = lotNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Column(name = "lot_number")
    private int lotNumber;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "lot_url", length = 600)
    private String lotUrl;

    @Column(name = "img_url", length = 255)
    private String imgUrl;

    @Temporal(TemporalType.DATE)
    @Column(name = "date")
    private Date date;

    @Column(name = "maison_enchere", length = 100)
    private String maisonEnchere;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "processor_brand", length = 50)
    private String processorBrand;

    @Column(name = "processor_model", length = 100)
    private String processorModel;

    @Column(name = "processor_cores")
    private int processorCores;

    @Column(name = "processor_clock_speed")
    private double processorClockSpeed;

    @Column(name = "ram_size")
    private int ramSize;

    @Column(name = "ram_type", length = 50)
    private String ramType;

    @Column(name = "storage_type", length = 50)
    private String storageType;

    @Column(name = "storage_capacity")
    private int storageCapacity;

    @Column(name = "storage_speed")
    private int storageSpeed;

    @Column(name = "gpu_type", length = 50)
    private String gpuType;

    @Column(name = "gpu_model", length = 100)
    private String gpuModel;

    @Column(name = "gpu_vram")
    private int gpuVram;

    @Column(name = "screen_size")
    private double screenSize;

    @Column(name = "screen_resolution", length = 50)
    private String screenResolution;

    @Column(name = "touch_screen")
    private boolean touchScreen;

    @Column(name = "fingerprint_sensor")
    private boolean fingerprintSensor;

    @Column(name = "face_recognition")
    private boolean faceRecognition;

    @Column(name = "chassis_material", length = 100)
    private String chassisMaterial;

    @Column(name = "keyboard_backlight")
    private boolean keyboardBacklight;

    @Column(name = "keyboard_type", length = 50)
    private String keyboardType;

    @Column(name = "connectivity", length = 255)
    private String connectivity;

    @Column(name = "battery_life", length = 50)
    private String batteryLife;

    @Column(name = "weight")
    private double weight;

    @Column(name = "operating_system", length = 50)
    private String operatingSystem;

    @Column(name = "product_condition", length = 50)
    private String condition;

    @Column(name = "warranty", length = 50)
    private String warranty;

    @Column(name = "release_year")
    private int releaseYear;

    @Column(name = "etat_produit_image", length = 50)
    private String etatProduitImage;

    @Column(name = "reason_for_condition", columnDefinition = "TEXT")
    private String reasonForCondition;

    @Column(name = "note_sur_10")
    private int noteSur10;

    @Column(name = "reason_for_score", columnDefinition = "TEXT")
    private String reasonForScore;

    @Column(name = "bon_coin_estimation", length = 50)
    private String bonCoinEstimation;

    @Column(name = "facebook_estimation", length = 50)
    private String facebookEstimation;

    @Column(name = "internet_estimation", length = 50)
    private String internetEstimation;

    @Column(name = "recommended_to_buy")
    private boolean recommendedToBuy;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    // ✅ JSON Constructor
    @JsonCreator
    public Laptop(
            @JsonProperty("lot_number") int lotNumber,
            @JsonProperty("description") String description,
            @JsonProperty("lot_url") String lotUrl,
            @JsonProperty("img_url") String imgUrl,
            @JsonProperty("date") String date,
            @JsonProperty("maison_enchere") String maisonEnchere,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("brand") String brand,
            @JsonProperty("model") String model,
            @JsonProperty("processor_brand") String processorBrand,
            @JsonProperty("processor_model") String processorModel,
            @JsonProperty("processor_cores") int processorCores,
            @JsonProperty("processor_clock_speed") double processorClockSpeed,
            @JsonProperty("ram_size") int ramSize,
            @JsonProperty("ram_type") String ramType,
            @JsonProperty("storage_type") String storageType,
            @JsonProperty("storage_capacity") int storageCapacity,
            @JsonProperty("storage_speed") int storageSpeed,
            @JsonProperty("gpu_type") String gpuType,
            @JsonProperty("gpu_model") String gpuModel,
            @JsonProperty("gpu_vram") int gpuVram,
            @JsonProperty("screen_size") double screenSize,
            @JsonProperty("screen_resolution") String screenResolution,
            @JsonProperty("touch_screen") boolean touchScreen,
            @JsonProperty("fingerprint_sensor") boolean fingerprintSensor,
            @JsonProperty("face_recognition") boolean faceRecognition,
            @JsonProperty("chassis_material") String chassisMaterial,
            @JsonProperty("keyboard_backlight") boolean keyboardBacklight,
            @JsonProperty("keyboard_type") String keyboardType,
            @JsonProperty("connectivity") String connectivity,
            @JsonProperty("battery_life") String batteryLife,
            @JsonProperty("weight") double weight,
            @JsonProperty("operating_system") String operatingSystem,
            @JsonProperty("product_condition") String condition,
            @JsonProperty("warranty") String warranty,
            @JsonProperty("release_year") int releaseYear,
            @JsonProperty("etat_produit_image") String etatProduitImage,
            @JsonProperty("reason_for_condition") String reasonForCondition,
            @JsonProperty("note_sur_10") int noteSur10,
            @JsonProperty("reason_for_score") String reasonForScore,
            @JsonProperty("bon_coin_estimation") String bonCoinEstimation,
            @JsonProperty("facebook_estimation") String facebookEstimation,
            @JsonProperty("internet_estimation") String internetEstimation,
            @JsonProperty("recommended_to_buy") boolean recommendedToBuy,
            @JsonProperty("image") String image
    ) {
        this.lotNumber = lotNumber;
        this.description = description;
        this.lotUrl = lotUrl;
        this.imgUrl = imgUrl;

        try {
            this.date = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (Exception e) {
            this.date = null;
        }

        this.maisonEnchere = maisonEnchere;
        this.quantity = quantity;
        this.brand = brand;
        this.model = model;
        this.processorBrand = processorBrand;
        this.processorModel = processorModel;
        this.processorCores = processorCores;
        this.processorClockSpeed = processorClockSpeed;
        this.ramSize = ramSize;
        this.ramType = ramType;
        this.storageType = storageType;
        this.storageCapacity = storageCapacity;
        this.storageSpeed = storageSpeed;
        this.gpuType = gpuType;
        this.gpuModel = gpuModel;
        this.gpuVram = gpuVram;
        this.screenSize = screenSize;
        this.screenResolution = screenResolution;
        this.touchScreen = touchScreen;
        this.fingerprintSensor = fingerprintSensor;
        this.faceRecognition = faceRecognition;
        this.chassisMaterial = chassisMaterial;
        this.keyboardBacklight = keyboardBacklight;
        this.keyboardType = keyboardType;
        this.connectivity = connectivity;
        this.batteryLife = batteryLife;
        this.weight = weight;
        this.operatingSystem = operatingSystem;
        this.condition = condition;
        this.warranty = warranty;
        this.releaseYear = releaseYear;
        this.etatProduitImage = etatProduitImage;
        this.reasonForCondition = reasonForCondition;
        this.noteSur10 = noteSur10;
        this.reasonForScore = reasonForScore;
        this.bonCoinEstimation = bonCoinEstimation;
        this.facebookEstimation = facebookEstimation;
        this.internetEstimation = internetEstimation;
        this.recommendedToBuy = recommendedToBuy;
        this.image = image;
    }

    public String getLotUrl() {
        return lotUrl;
    }

    public void setLotUrl(String lotUrl) {
        this.lotUrl = lotUrl;
    }
    public String getMaisonEnchere() {
        return maisonEnchere;
    }

    public void setMaisonEnchere(String maisonEnchere) {
        this.maisonEnchere = maisonEnchere;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getProcessorBrand() {
        return processorBrand;
    }

    public void setProcessorBrand(String processorBrand) {
        this.processorBrand = processorBrand;
    }

    public String getProcessorModel() {
        return processorModel;
    }

    public void setProcessorModel(String processorModel) {
        this.processorModel = processorModel;
    }

    public int getProcessorCores() {
        return processorCores;
    }

    public void setProcessorCores(int processorCores) {
        this.processorCores = processorCores;
    }

    public double getProcessorClockSpeed() {
        return processorClockSpeed;
    }

    public void setProcessorClockSpeed(double processorClockSpeed) {
        this.processorClockSpeed = processorClockSpeed;
    }

    public int getRamSize() {
        return ramSize;
    }

    public void setRamSize(int ramSize) {
        this.ramSize = ramSize;
    }

    public String getRamType() {
        return ramType;
    }

    public void setRamType(String ramType) {
        this.ramType = ramType;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public int getStorageCapacity() {
        return storageCapacity;
    }

    public void setStorageCapacity(int storageCapacity) {
        this.storageCapacity = storageCapacity;
    }

    public int getStorageSpeed() {
        return storageSpeed;
    }

    public void setStorageSpeed(int storageSpeed) {
        this.storageSpeed = storageSpeed;
    }

    public String getGpuType() {
        return gpuType;
    }

    public void setGpuType(String gpuType) {
        this.gpuType = gpuType;
    }

    public String getGpuModel() {
        return gpuModel;
    }

    public void setGpuModel(String gpuModel) {
        this.gpuModel = gpuModel;
    }

    public int getGpuVram() {
        return gpuVram;
    }

    public void setGpuVram(int gpuVram) {
        this.gpuVram = gpuVram;
    }

    public double getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(double screenSize) {
        this.screenSize = screenSize;
    }

    public String getScreenResolution() {
        return screenResolution;
    }

    public void setScreenResolution(String screenResolution) {
        this.screenResolution = screenResolution;
    }

    public boolean isTouchScreen() {
        return touchScreen;
    }

    public void setTouchScreen(boolean touchScreen) {
        this.touchScreen = touchScreen;
    }

    public boolean isFingerprintSensor() {
        return fingerprintSensor;
    }

    public void setFingerprintSensor(boolean fingerprintSensor) {
        this.fingerprintSensor = fingerprintSensor;
    }

    public boolean isFaceRecognition() {
        return faceRecognition;
    }

    public void setFaceRecognition(boolean faceRecognition) {
        this.faceRecognition = faceRecognition;
    }

    public String getChassisMaterial() {
        return chassisMaterial;
    }

    public void setChassisMaterial(String chassisMaterial) {
        this.chassisMaterial = chassisMaterial;
    }

    public boolean isKeyboardBacklight() {
        return keyboardBacklight;
    }

    public void setKeyboardBacklight(boolean keyboardBacklight) {
        this.keyboardBacklight = keyboardBacklight;
    }

    public String getKeyboardType() {
        return keyboardType;
    }

    public void setKeyboardType(String keyboardType) {
        this.keyboardType = keyboardType;
    }

    public String getConnectivity() {
        return connectivity;
    }

    public void setConnectivity(String connectivity) {
        this.connectivity = connectivity;
    }

    public String getBatteryLife() {
        return batteryLife;
    }

    public void setBatteryLife(String batteryLife) {
        this.batteryLife = batteryLife;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getEtatProduitImage() {
        return etatProduitImage;
    }

    public void setEtatProduitImage(String etatProduitImage) {
        this.etatProduitImage = etatProduitImage;
    }

    public String getReasonForCondition() {
        return reasonForCondition;
    }

    public void setReasonForCondition(String reasonForCondition) {
        this.reasonForCondition = reasonForCondition;
    }

    public int getNoteSur10() {
        return noteSur10;
    }

    public void setNoteSur10(int noteSur10) {
        this.noteSur10 = noteSur10;
    }

    public String getReasonForScore() {
        return reasonForScore;
    }

    public void setReasonForScore(String reasonForScore) {
        this.reasonForScore = reasonForScore;
    }

    public String getBonCoinEstimation() {
        return bonCoinEstimation;
    }

    public void setBonCoinEstimation(String bonCoinEstimation) {
        this.bonCoinEstimation = bonCoinEstimation;
    }

    public String getFacebookEstimation() {
        return facebookEstimation;
    }

    public void setFacebookEstimation(String facebookEstimation) {
        this.facebookEstimation = facebookEstimation;
    }

    public String getInternetEstimation() {
        return internetEstimation;
    }

    public void setInternetEstimation(String internetEstimation) {
        this.internetEstimation = internetEstimation;
    }

    public boolean isRecommendedToBuy() {
        return recommendedToBuy;
    }

    public void setRecommendedToBuy(boolean recommendedToBuy) {
        this.recommendedToBuy = recommendedToBuy;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
    public String getSpecs() {
        return "<b>🖥 Processor:</b> " + getProcessorBrand() + " " + getProcessorModel() + " (" + getProcessorCores() + " Cores, " + getProcessorClockSpeed() + "GHz)" +
                "<br><b>💾 RAM:</b> " + getRamSize() + "GB " + getRamType() +
                "<br><b>💽 Storage:</b> " + getStorageType() + " " + getStorageCapacity() + "GB, Speed: " + getStorageSpeed() + "MB/s" +
                "<br><b>🎮 GPU:</b> " + getGpuType() + " " + getGpuModel() + " (" + getGpuVram() + "GB VRAM)" +
                "<br><b>🖥 Screen:</b> " + getScreenSize() + " inches, Resolution: " + getScreenResolution() +
                (isTouchScreen() ? " (Touch ✅)" : " (No Touch ❌)") +
                "<br><b>🔋 Battery:</b> " + getBatteryLife() +
                "<br><b>⚡ Weight:</b> " + getWeight() + "kg" +
                "<br><b>💻 OS:</b> " + getOperatingSystem() +
                "<br><b>⌨️ Keyboard:</b> " + getKeyboardType() + (isKeyboardBacklight() ? " (Backlit ✅)" : " (No Backlight ❌)") +
                "<br><b>🔒 Security:</b> " + (isFingerprintSensor() ? "Fingerprint ✅" : "No Fingerprint ❌") + ", " +
                (isFaceRecognition() ? "Face Recognition ✅" : "No Face Recognition ❌") +
                "<br><b>📡 Connectivity:</b> " + getConnectivity() +
                "<br><b>🏗️ Chassis:</b> " + getChassisMaterial() +
                "<br><b>📅 Release Year:</b> " + getReleaseYear() +
                "<br><b>🛡️ Warranty:</b> " + (getWarranty() != null ? getWarranty() : "N/A");
    }


}
