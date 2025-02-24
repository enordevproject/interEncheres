package Models;

import hibernate.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    /**
     * ✅ Fetches all laptops from the database.
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

    public static void generateHtmlReport() {
        List<Laptop> laptops = getAllLaptopsFromDatabase();

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='en'><head>");
        html.append("<meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>Laptop Inventory</title>");
        html.append("<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css'>");
        html.append("<script src='https://cdn.jsdelivr.net/npm/jquery@3.6.0/dist/jquery.min.js'></script>");
        html.append("<script src='https://cdn.jsdelivr.net/npm/datatables.net/js/jquery.dataTables.min.js'></script>");
        html.append("<script src='https://cdn.jsdelivr.net/npm/datatables.net-bs5/js/dataTables.bootstrap5.min.js'></script>");
        html.append("<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/datatables.net-bs5/css/dataTables.bootstrap5.min.css'>");
        html.append("<style>img {border-radius: 8px;}</style>");
        html.append("</head><body class='container'><h2 class='my-3'>Laptop Inventory</h2>");
        html.append("<table id='laptopTable' class='table table-striped table-bordered'><thead><tr>");

        // ✅ Define Table Headers
        html.append("<th>Lot Number</th><th>Brand</th><th>Model</th><th>Specifications</th>");
        html.append("<th>Chassis</th><th>Keyboard</th><th>Connectivity</th>");
        html.append("<th>Battery</th><th>Weight</th><th>OS</th><th>Condition</th><th>Estimation</th>");
        html.append("<th>Score</th><th>Recommendation</th><th>Image</th></tr></thead><tbody>");

        for (Laptop laptop : laptops) {
            html.append("<tr>");

            // ✅ Lot Number as a clickable link
            html.append("<td><a href='").append(laptop.getLotUrl()).append("' target='_blank'>")
                    .append(laptop.getLotNumber()).append("</a></td>");

            // ✅ Brand & Model
            html.append("<td>").append(laptop.getBrand()).append("</td>");
            html.append("<td>").append(laptop.getModel()).append("</td>");

            // ✅ Specifications
            html.append("<td>")
                    .append("<strong>Processor:</strong> ").append(laptop.getProcessorBrand()).append(" ")
                    .append(laptop.getProcessorModel()).append(" - ").append(laptop.getProcessorClockSpeed()).append("GHz - ")
                    .append(laptop.getProcessorCores()).append(" Cores<br>")
                    .append("<strong>RAM:</strong> ").append(laptop.getRamSize()).append("GB ").append(laptop.getRamType()).append("<br>")
                    .append("<strong>Storage:</strong> ").append(laptop.getStorageCapacity()).append("GB ")
                    .append(laptop.getStorageType()).append(" (Speed: ")
                    .append("<strong>GPU:</strong> ").append(laptop.getGpuType()).append(" ").append(laptop.getGpuModel()).append(" (").append(laptop.getGpuVram()).append("GB VRAM)<br>")
                    .append("<strong>Screen:</strong> ").append(laptop.getScreenSize()).append("” ")
                    .append(laptop.getScreenResolution()).append(" - Touch: ").append(laptop.isTouchScreen() ? "✅" : "❌")
                    .append("</td>");





            // ✅ Battery & Weight
            html.append("<td>").append(laptop.getBatteryLife()).append("</td>");
            html.append("<td>").append(laptop.getWeight()).append("kg</td>");

            // ✅ OS & Condition
            html.append("<td>").append(laptop.getOperatingSystem()).append("</td>");
            html.append("<td>").append(laptop.getCondition()).append("</td>");


            // ✅ Score & Recommendation
            html.append("<td>Score: ").append(laptop.getNoteSur10()).append("/10")
                    .append("<br>Reason: ").append(laptop.getReasonForScore()).append("</td>");
            html.append("<td>").append(laptop.isRecommendedToBuy() ? "✅ Recommended" : "❌ Not Recommended").append("</td>");

            // ✅ Image Handling
            html.append("<td>");
            if (laptop.getImgUrl() != null && !laptop.getImgUrl().isEmpty()) {
                html.append("<img src='").append(laptop.getImgUrl()).append("' width='100'>");
            } else {
                html.append("<img src='https://via.placeholder.com/100' width='100' alt='No Image Available'>");
            }
            html.append("</td>");

            html.append("</tr>");
        }

        html.append("</tbody></table>");
        html.append("<script>$(document).ready(function() { $('#laptopTable').DataTable(); });</script>");
        html.append("</body></html>");

        // ✅ Save File Inside `test/java/front/`
        String directoryPath = "src/test/java/front/";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs(); // ✅ Create the directory if it doesn't exist
        }

        // ✅ Save the HTML file
        String filePath = directoryPath + "laptops_report.html";
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(html.toString());
            System.out.println("✅ Laptops report generated: " + filePath);
        } catch (IOException e) {
            System.err.println("❌ Error writing HTML report: " + e.getMessage());
        }
    }

}

