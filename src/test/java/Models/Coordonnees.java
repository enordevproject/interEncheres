package Models;

public class Coordonnees {

    private String address;
    private String telephone;
    private String email;

    // Constructor
    public Coordonnees(String address, String telephone, String email) {
        this.address = address;
        this.telephone = telephone;
        this.email = email;
    }

    // Getters and Setters
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Coordonnees{" +
                "address='" + address + '\'' +
                ", telephone='" + telephone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
