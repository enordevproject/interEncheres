package Models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "Lot")
public class Lot {

    @Id
    @Column(name = "number", length = 50)
    private String number;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "imgUrl", length = 255)
    private String imgUrl;

    @Column(name = "estimationPrice", length = 50)
    private String estimationPrice;  // Changed to BigDecimal for price handling

    @Column(name = "date", length = 50)
    private String date;  // Changed to LocalDate for proper date handling

    @Column(name = "maisonEnchere", length = 100)
    private String maisonEnchere;

    @Column(name = "url", length = 255)
    private String url;

    // Constructor
    public Lot(String number, String description, String imgUrl,
               String estimationPrice, String date, String maisonEnchere, String url) {
        this.number = number;
        this.description = description;
        this.imgUrl = imgUrl;
        this.estimationPrice = estimationPrice;
        this.date = date;
        this.maisonEnchere = maisonEnchere;
        this.url = url;
    }

    // Default Constructor
    public Lot() {}

    // Getters and Setters
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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

    public String getEstimationPrice() {
        return estimationPrice;
    }

    public void setEstimationPrice(String estimationPrice) {
        this.estimationPrice = estimationPrice;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMaisonEnchere() {
        return maisonEnchere;
    }

    public void setMaisonEnchere(String maisonEnchere) {
        this.maisonEnchere = maisonEnchere;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Lot{" +
                "number='" + number + '\'' +
                ", description='" + description + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", estimationPrice=" + estimationPrice +
                ", date=" + date +
                ", maisonEnchere='" + maisonEnchere + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
