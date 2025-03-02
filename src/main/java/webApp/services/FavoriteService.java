package webApp.services;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import selenium.pages.Produit;
import selenium.pages.Home;
import webApp.models.User;
import webApp.repositories.UserRepository;
import webApp.utils.PasswordUtils;

import java.util.List;
import java.util.Optional;

@Service
public class FavoriteService {

    @Autowired
    private UserRepository userRepository; // ✅ Injects UserRepository

    @Autowired
    private SeleniumConfigService seleniumConfigService; // ✅ Injects SeleniumConfigService

    /**
     * ✅ Fetches credentials from the database, decrypts the password, and logs in.
     */
    private String[] getCredentialsFromDB() throws Exception {
        Optional<User> userOptional = userRepository.findFirstByOrderByIdAsc();
        if (userOptional.isEmpty()) {
            throw new IllegalStateException("❌ No user found in the database.");
        }

        User user = userOptional.get();
        String decryptedPassword = PasswordUtils.decrypt(user.getEncryptedPassword(), user.getEmail());

        return new String[]{user.getEmail(), decryptedPassword};
    }

    /**
     * ✅ Adds a single lot to Interencheres favorites.
     */
    public String addLotToFavorites(String lotUrl) {
        WebDriver driver = seleniumConfigService.getDriver(); // ✅ Get Singleton WebDriver

        try {
            // ✅ Fetch credentials from DB
            String[] credentials = getCredentialsFromDB();
            String email = credentials[0];
            String password = credentials[1];

            // ✅ Log in once
            Home home = new Home(driver);
            home.performLogin(email, password);

            // ✅ Navigate to Lot Page
            Produit produit = new Produit(driver);
            produit.openLotPage(lotUrl);

            // ✅ Add to Favorites
            produit.addToFavorites();

            return "✅ Lot successfully added to favorites!";
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }

    /**
     * ✅ Adds multiple lots to Interencheres favorites.
     */
    public String addLotsToFavorites(List<String> lotUrls) {
        WebDriver driver = seleniumConfigService.getDriver(); // ✅ Use Singleton WebDriver

        try {
            // ✅ Fetch credentials from DB
            String[] credentials = getCredentialsFromDB();
            String email = credentials[0];
            String password = credentials[1];

            // ✅ Log in once
            Home home = new Home(driver);
            home.performLogin(email, password);

            int successCount = 0;
            int failCount = 0;

            for (String lotUrl : lotUrls) {
                try {
                    // ✅ Navigate to Lot Page
                    Produit produit = new Produit(driver);
                    produit.openLotPage(lotUrl);

                    // ✅ Add to Favorites
                    produit.addToFavorites();
                    successCount++;
                } catch (Exception e) {
                    System.err.println("❌ Failed to add lot: " + lotUrl + " | Error: " + e.getMessage());
                    failCount++;
                }
            }

            return "✅ Successfully added " + successCount + " lots to favorites. ❌ Failed: " + failCount;
        } catch (Exception e) {
            return "❌ Error: " + e.getMessage();
        }
    }
}
