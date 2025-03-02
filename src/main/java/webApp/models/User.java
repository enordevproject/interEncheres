package webApp.models;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String encryptedPassword; // ✅ Stored as encrypted

    public User() {}

    public User(String email, String encryptedPassword) {
        this.email = email;
        this.encryptedPassword = encryptedPassword;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getEncryptedPassword() { return encryptedPassword; }
}
