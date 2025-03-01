package webApp.Models;

import webApp.Utils.ImageUtils;
import jakarta.persistence.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private String estimationPrice;

    @Column(name = "date", length = 50)
    private String date;

    @Column(name = "maisonEnchere", length = 100)
    private String maisonEnchere;

    @Column(name = "url", length = 255)
    private String url;

    @Column(name = "insertionDate")
    private LocalDateTime insertionDate;

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

    // Getter for insertionDate
    public LocalDateTime getInsertionDate() {
        return insertionDate;
    }

    // Setter for insertionDate
    public void setInsertionDate(LocalDateTime insertionDate) {
        this.insertionDate = insertionDate;
    }

    @PrePersist
    public void setDefaultInsertionDate() {
        this.insertionDate = LocalDateTime.now();
    }

    // Getters and Setters for other fields
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




    public Date getDate() {
        if (date == null || date.trim().isEmpty()) return null;

        date = date.trim().toLowerCase();
        Calendar calendar = Calendar.getInstance();

        // Handle standard relative terms
        if (date.contains("aujourd'hui")) {
            return calendar.getTime();
        } else if (date.contains("demain")) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            return calendar.getTime();
        } else if (date.contains("hier")) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            return calendar.getTime();
        }

        // Handle custom format '2j 20h'
        Pattern pattern = Pattern.compile("(\\d+)j (\\d+)h");
        Matcher matcher = pattern.matcher(date);
        if (matcher.find()) {
            int days = Integer.parseInt(matcher.group(1));
            int hours = Integer.parseInt(matcher.group(2));
            calendar.add(Calendar.DAY_OF_MONTH, days);
            calendar.add(Calendar.HOUR_OF_DAY, hours);
            return calendar.getTime();
        }

        // Handle standard date formats
        SimpleDateFormat[] formats = {
                new SimpleDateFormat("yyyy-MM-dd HH:mm"),
                new SimpleDateFormat("dd/MM/yyyy HH:mm"),
                new SimpleDateFormat("yyyy-MM-dd"),
                new SimpleDateFormat("dd/MM/yyyy")
        };

        for (SimpleDateFormat format : formats) {
            try {
                return format.parse(date);
            } catch (ParseException ignored) {}
        }

        System.out.println("❌ Format de date invalide : " + date);
        return null;
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
    public String getImgAsBase64() {
        return ImageUtils.downloadImageAsBase64(this.imgUrl);
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
                ", insertionDate=" + insertionDate +
                '}';
    }
}
