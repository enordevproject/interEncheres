package webApp.models;

import com.google.gson.Gson;
import webApp.hibernate.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
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
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        long addedCount = lots.stream()
                .filter(lot -> !checkIfUrlExists(lot.getUrl()))
                .peek(lot -> {
                    synchronized (lot) { // ✅ Ensures each entry is unique
                        session.merge(lot);
                        log.info("📌 Lot Added - URL: {} | Date: {} | Maison Enchere: {}",
                                lot.getUrl(), lot.getDate(), lot.getMaisonEnchere());
                    }
                })
                .count();

        log.info("✅ Total lots added: " + addedCount);
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

        // Ensure it's classified as a laptop
        if (!laptop.isLaptop()) return false;

        // Check essential fields to confirm it's a valid laptop
        return laptop.getBrand() != null && !laptop.getBrand().trim().isEmpty() &&
                laptop.getModel() != null && !laptop.getModel().trim().isEmpty() &&
                laptop.getProcessorBrand() != null && !laptop.getProcessorBrand().trim().isEmpty() &&
                laptop.getProcessorModel() != null && !laptop.getProcessorModel().trim().isEmpty() &&
                laptop.getRamSize() > 0 &&
                laptop.getStorageCapacity() > 0 &&
                laptop.getScreenSize() > 10; // Ensures it's not a small device like a tablet
    }

    public static void removeExpiredLaptops() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            LocalDate today = LocalDate.now(); // Get today's date
            String hql = "DELETE FROM Laptop WHERE date < :today";
            int deletedCount = session.createQuery(hql)
                    .setParameter("today", today)
                    .executeUpdate();

            transaction.commit();
            System.out.println("🗑️ Removed " + deletedCount + " expired laptops from the database.");
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("❌ Error removing expired laptops: " + e.getMessage());
        }
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
        synchronized (lot) { // ✅ Ensures each lot is handled separately
            log.info("🔍 Processing lot: {}", lot.getUrl());

            if (checkIfLaptopExists(lot.getUrl())) {
                log.info("⏭️ Laptop already exists for lot {}. Skipping...", lot.getUrl());
                return;
            }

            log.info("🧠 Sending lot to GPT-4...");
            Laptop generatedLaptop = GPTService.generateLaptopFromLot(lot);

            if (generatedLaptop != null) {
                log.info("✅ Laptop generated successfully for lot: {}", lot.getUrl());

                if (!isValidLaptop(generatedLaptop)) {
                    log.warn("❌ Laptop validation failed for lot {}. Skipping insertion.", lot.getUrl());
                    return;
                }

                // 🚀 ✅ **Store the laptop in DB immediately** to prevent image and info mixing
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    Transaction transaction = session.beginTransaction();
                    session.merge(generatedLaptop);
                    transaction.commit();
                    log.info("💾 Laptop inserted into the database for lot: {} | Date: {} | Maison Enchere: {}",
                            lot.getUrl(), lot.getDate(), lot.getMaisonEnchere());
                } catch (Exception e) {
                    log.error("❌ Error inserting laptop for lot {}: {}", lot.getUrl(), e.getMessage(), e);
                }
            } else {
                log.warn("⚠️ Failed to generate Laptop for {}", lot.getUrl());
            }
        }
    }








// Exemples de méthodes supplémentaires utiles :

    // 1) Récupérer un seul Laptop via son ID
    public static Laptop getLaptopById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Laptop.class, id);
        } catch (Exception e) {
            log.error("❌ Error fetching laptop by ID {}: {}", id, e.getMessage());
            return null;
        }
    }

    // 2) Mettre à jour un Laptop existant (merge)
    public static void updateLaptop(Laptop laptop) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(laptop);
            transaction.commit();
            log.info("✅ Laptop (ID: {}) updated successfully.", laptop.getId());
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.error("❌ Failed to update laptop: {}", e.getMessage());
        }
    }

    // 3) Supprimer un Laptop via son ID
    public static void deleteLaptopById(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Laptop laptop = session.get(Laptop.class, id);
            if (laptop != null) {
                session.remove(laptop);
                log.info("🗑️ Laptop (ID: {}) deleted.", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            log.error("❌ Failed to delete laptop: {}", e.getMessage());
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