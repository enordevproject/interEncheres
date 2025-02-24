package webApp.Models;

import com.google.gson.Gson;
import webApp.hibernate.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Results {

    private static final Logger log = LoggerFactory.getLogger(Results.class);
    private List<Lot> lots;

    // Default Constructor
    public Results() {
        this.lots = new ArrayList<>();
    }

    // Constructor to initialize with a list of Lot objects
    public Results(List<Lot> lots) {
        this.lots = lots;
    }

    // Getter for the list of lots
    public List<Lot> getLots() {
        return lots;
    }

    // Setter for the list of lots
    public void setLots(List<Lot> lots) {
        this.lots = lots;
    }

    // Method to add a lot to the results
    public void addLot(Lot lot) {
        this.lots.add(lot);
    }

    // Method to remove a lot from the results
    public void removeLot(Lot lot) {
        this.lots.remove(lot);
    }

    @Override
    public String toString() {
        return "Results{" +
                "lots=" + lots +
                '}';
    }

    public void pushLotsToDatabase(List<Lot> lots) {
        // Open session and begin transaction
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        // Iterate over the lots and insert them into the database
        long addedCount = lots.stream()
                .filter(lot -> !checkIfUrlExists(lot.getUrl())) // Filter out existing URLs
                .peek(lot -> {
                    session.merge(lot); // Save or update the lot in the database
                    System.out.println("Lot with URL: " + lot.getUrl() + " has been successfully added to the database.");
                    System.out.println("📸 Image URL: " + lot.getImgUrl()); // Debugging line
                })
                .count(); // Count the added lots

        // Log the total number of added lots
        log.info("Total lots added: " + addedCount);
        System.out.println("Total lots added: " + addedCount);

        // Commit the transaction and close the session
        session.flush();
        tx.commit();
        session.close();
    }

    // ✅ Use HibernateUtil to get session
    public static List<Lot> getAllLotsFromDatabase() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Lot", Lot.class).list();
        } catch (Exception e) {
            System.err.println("❌ Error fetching lots from DB: " + e.getMessage());
            return List.of();
        }
    }
    public static boolean isValidLaptop(Laptop laptop) {
        if (laptop == null) return false;

        // Check for invalid or missing critical fields
        List<String> invalidValues = Arrays.asList("N/A", "Unknown", "Non précisé", "Sans garantie", "Non fonctionnel");

        if (laptop.getBrand() == null || invalidValues.contains(laptop.getBrand())) return false;
        if (laptop.getModel() == null || invalidValues.contains(laptop.getModel())) return false;
        if (laptop.getProcessorModel() == null || invalidValues.contains(laptop.getProcessorModel())) return false;
        if (laptop.getRamSize() <= 0) return false;
        if (laptop.getStorageCapacity() <= 0) return false;
        if (laptop.getScreenSize() <= 0) return false;

        // Exclude non-laptop descriptions
        String description = laptop.getDescription().toLowerCase();
        List<String> nonLaptopKeywords = Arrays.asList("microphone", "ecran", "tablette", "imprimante", "pc fixe", "tour", "scanner");

        for (String keyword : nonLaptopKeywords) {
            if (description.contains(keyword)) return false;
        }

        return true;
    }

    public static void insertLaptopIntoDatabase(Laptop laptop) {
        if (!isValidLaptop(laptop)) {
            System.out.println("❌ Laptop validation failed. Skipping insertion.");
            return;
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(laptop);
            transaction.commit();
            System.out.println("💾 Laptop inserted successfully.");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.out.println("❌ Failed to insert laptop: " + e.getMessage());
        }
    }


    public static boolean checkIfLaptopExists(String lotUrl) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(*) FROM Laptop WHERE lotUrl = :lotUrl";
            Long count = (Long) session.createQuery(hql)
                    .setParameter("lotUrl", lotUrl)
                    .uniqueResult();
            return count > 0;
        } catch (Exception e) {
            System.err.println("❌ Error checking laptop existence: " + e.getMessage());
            return false;
        }
    }


    private boolean checkIfUrlExists(String url) {
        // Ensure session is available in the current context
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(*) FROM Lot l1_0 WHERE l1_0.url = :url"; // Assuming this is correct
            Long count = (Long) session.createQuery(hql)
                    .setParameter("url", url)
                    .uniqueResult();
            return count > 0; // Return true if the URL already exists
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Return false in case of error (optional logging can be added)
        }
    }

    /**
     * ✅ Fetches all laptops from the database.
     *
     * @return List of Laptop objects.
     */
    public static List<Laptop> getAllLaptopsFromDatabase() {
        List<Laptop> laptops = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            laptops = session.createQuery("FROM Laptop", Laptop.class).list();

            transaction.commit();
        } catch (Exception e) {
            System.err.println("❌ Error fetching laptops from DB: " + e.getMessage());
        }
        return laptops;
    }

    // ✅ This method is executed in parallel for each lot
    public static void processLot(Lot lot) throws IOException {
        log.info("🔍 Processing lot: {}", lot.getUrl());

        if (Results.checkIfLaptopExists(lot.getUrl())) {
            log.info("⏭️ Laptop already exists for lot {}. Skipping...", lot.getUrl());
            return;
        }

        log.info("🧠 Sending lot to GPT-4...");
        Laptop generatedLaptop = GPTService.generateLaptopFromLot(lot);

        if (generatedLaptop != null) {
            log.info("✅ Laptop generated successfully for lot: {}", lot.getUrl());

            // ✅ Insert into database
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();

                session.persist(generatedLaptop);
                transaction.commit();

                log.info("💾 Laptop inserted into the database for lot: {}", lot.getUrl());
            } catch (Exception e) {
                log.error("❌ Error inserting laptop for lot {}: {}", lot.getUrl(), e.getMessage(), e);
            }
        } else {
            log.warn("⚠️ Failed to generate Laptop for {}", lot.getUrl());
        }
    }










    // Method to generate <option> elements for dropdowns
    private static String getOptions(Set<?> items) {
        StringBuilder options = new StringBuilder();
        for (Object item : items) {
            options.append("<option value='").append(item).append("'>").append(item).append("</option>");
        }
        return options.toString();
    }


    public static void generateJsonReport() {
        List<Laptop> laptops = getAllLaptopsFromDatabase();
        String filePath = "src/test/java/Front/laptops_report.json"; // Use the correct folder

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            new Gson().toJson(laptops, fileWriter);
            System.out.println("✅ JSON report generated at: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}