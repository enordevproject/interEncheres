package Models;

import hibernate.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import java.util.ArrayList;
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