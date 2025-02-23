package Models;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "laptop")
public class Laptop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lot_id", nullable = false)
    private Lot lot;

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

    // 🔥 AI-Generated Product Condition Based on Image
    @Column(name = "etat_produit_image", length = 50)
    private String etatProduitImage;

    @Column(name = "reason_for_condition", columnDefinition = "TEXT")
    private String reasonForCondition;

    // 🔥 GPT Decision Insights
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

    // 🔹 Default Constructor (Required for Hibernate)
    public Laptop() {}

    // 🔹 Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Lot getLot() { return lot; }
    public void setLot(Lot lot) { this.lot = lot; }

    public int getLotNumber() { return lotNumber; }
    public void setLotNumber(int lotNumber) { this.lotNumber = lotNumber; }

    public String getEtatProduitImage() { return etatProduitImage; }
    public void setEtatProduitImage(String etatProduitImage) { this.etatProduitImage = etatProduitImage; }

    public String getReasonForCondition() { return reasonForCondition; }
    public void setReasonForCondition(String reasonForCondition) { this.reasonForCondition = reasonForCondition; }

    public int getNoteSur10() { return noteSur10; }
    public void setNoteSur10(int noteSur10) { this.noteSur10 = noteSur10; }

    public String getReasonForScore() { return reasonForScore; }
    public void setReasonForScore(String reasonForScore) { this.reasonForScore = reasonForScore; }

    public String getBonCoinEstimation() { return bonCoinEstimation; }
    public void setBonCoinEstimation(String bonCoinEstimation) { this.bonCoinEstimation = bonCoinEstimation; }

    public String getFacebookEstimation() { return facebookEstimation; }
    public void setFacebookEstimation(String facebookEstimation) { this.facebookEstimation = facebookEstimation; }

    public String getInternetEstimation() { return internetEstimation; }
    public void setInternetEstimation(String internetEstimation) { this.internetEstimation = internetEstimation; }

    public boolean isRecommendedToBuy() { return recommendedToBuy; }
    public void setRecommendedToBuy(boolean recommendedToBuy) { this.recommendedToBuy = recommendedToBuy; }
}
