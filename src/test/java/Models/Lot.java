package Models;

import java.util.Date;

public class Lot {
    private String number;

    private String description;
    private String imgUrl;
    private String estimationPrice;
    private String date;
    private String maisonEnchere;
    private String url;

    // Constructor
    public Lot(String number, String description, String imgUrl,
               String estimationPrice, String date, String maisonEnchere,String url) {
        this.number = number;

        this.description = description;
        this.imgUrl = imgUrl;
        this.estimationPrice = estimationPrice;
        this.date = date;
        this.maisonEnchere = maisonEnchere;
        this.url = url;
    }
    public Lot() {

    }
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
                "number=" + number +
                ", description='" + description + '\'' +
                ", imgUrl=" + imgUrl +
                ", estimationPrice=" + estimationPrice +
                ", date=" + date +
                ", maisonEnchere='" + maisonEnchere + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
