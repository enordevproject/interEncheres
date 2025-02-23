package Models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "laptop")
public class Laptop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @Column(name = "recommended_to_buy")
    private boolean recommendedToBuy;

    // ✅ No-Arg Constructor (Required by Jackson)
    public Laptop() {}

    // ✅ JSON Constructor for Proper Deserialization
    @JsonCreator
    public Laptop(
            @JsonProperty("lot_number") int lotNumber,
            @JsonProperty("description") String description,
            @JsonProperty("lot_url") String lotUrl,
            @JsonProperty("img_url") String imgUrl,
            @JsonProperty("date") String date, // ✅ Fix: String instead of Date for JSON parsing
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
            @JsonProperty("gpu_type") String gpuType,
            @JsonProperty("gpu_model") String gpuModel,
            @JsonProperty("gpu_vram") int gpuVram,
            @JsonProperty("screen_size") double screenSize,
            @JsonProperty("screen_resolution") String screenResolution,
            @JsonProperty("touch_screen") boolean touchScreen,
            @JsonProperty("fingerprint_sensor") boolean fingerprintSensor,
            @JsonProperty("face_recognition") boolean faceRecognition,
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
            @JsonProperty("recommended_to_buy") boolean recommendedToBuy
    ) {
        this.lotNumber = lotNumber;
        this.description = description;
        this.lotUrl = lotUrl;
        this.imgUrl = imgUrl;

        // ✅ Fix Date Parsing from String
        try {
            this.date = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (Exception e) {
            this.date = null; // Default to null if parsing fails
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
        this.gpuType = gpuType;
        this.gpuModel = gpuModel;
        this.gpuVram = gpuVram;
        this.screenSize = screenSize;
        this.screenResolution = screenResolution;
        this.touchScreen = touchScreen;
        this.fingerprintSensor = fingerprintSensor;
        this.faceRecognition = faceRecognition;
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
        this.recommendedToBuy = recommendedToBuy;
    }
}
