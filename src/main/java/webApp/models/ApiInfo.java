package webApp.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "apiinfo")
public class ApiInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "api_name", length = 100, nullable = false)
    private String apiName;

    @Column(name = "api_key", nullable = false)
    private String apiKey;

    @Column(name = "api_url", length = 255, nullable = false)
    private String apiUrl;

    @Column(name = "model", length = 50, nullable = false)
    private String model;

    @Column(name = "max_tokens")
    private int maxTokens = 2048;

    @Column(name = "temperature")
    private double temperature = 1.00;

    @Column(name = "top_p")
    private double topP = 1.00;

    @Column(name = "frequency_penalty")
    private double frequencyPenalty = 0.00;

    @Column(name = "presence_penalty")
    private double presencePenalty = 0.00;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // Constructeurs
    public ApiInfo() {}

    public ApiInfo(String apiName, String apiKey, String apiUrl, String model) {
        this.apiName = apiName;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.model = model;
    }

    // Getters & Setters
    public int getId() { return id; }
    public String getApiName() { return apiName; }
    public void setApiName(String apiName) { this.apiName = apiName; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getApiUrl() { return apiUrl; }
    public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getMaxTokens() { return maxTokens; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public double getTopP() { return topP; }
    public void setTopP(double topP) { this.topP = topP; }

    public double getFrequencyPenalty() { return frequencyPenalty; }
    public void setFrequencyPenalty(double frequencyPenalty) { this.frequencyPenalty = frequencyPenalty; }

    public double getPresencePenalty() { return presencePenalty; }
    public void setPresencePenalty(double presencePenalty) { this.presencePenalty = presencePenalty; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
