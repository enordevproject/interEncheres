package Models;

import hibernate.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

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

    public static void insertLaptopIntoDatabase(Laptop laptop) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(laptop); // Use persist() instead of save()
            transaction.commit(); // ✅ Ensure commit after each laptop insertion
            System.out.println("💾 Laptop inserted successfully: ");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // ✅ Rollback on failure
            }
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
}