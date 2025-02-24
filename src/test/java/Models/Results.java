package Models;

import com.google.gson.Gson;
import hibernate.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
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






    public static void generateHtmlReport() {
        List<Laptop> laptops = getAllLaptopsFromDatabase();

        // Collect unique values for auto-complete & dropdown filters
        Set<String> brands = new HashSet<>();
        Set<String> models = new HashSet<>();
        Set<String> processors = new HashSet<>();
        Set<String> sellers = new HashSet<>();
        Set<String> conditions = new HashSet<>(Arrays.asList("New", "Used"));
        Set<Integer> releaseYears = new HashSet<>();

        for (Laptop laptop : laptops) {
            brands.add(laptop.getBrand());
            models.add(laptop.getModel());
            processors.add(laptop.getProcessorBrand() + " " + laptop.getProcessorModel());
            sellers.add(laptop.getMaisonEnchere());
            releaseYears.add(laptop.getReleaseYear());
        }

        String filePath = "src/test/java/front/laptops_report.html";

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='en'><head>")
                .append("<meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>")
                .append("<title>Laptop Inventory Analysis</title>")
                .append("<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css'>")
                .append("<script src='https://cdn.jsdelivr.net/npm/jquery@3.6.0/dist/jquery.min.js'></script>")
                .append("<script src='https://cdn.jsdelivr.net/npm/jquery-ui-dist/jquery-ui.min.js'></script>")
                .append("<link rel='stylesheet' href='https://cdn.jsdelivr.net/npm/jquery-ui-dist/jquery-ui.min.css'>")
                .append("<style>.table-container {overflow-x: auto; width: 100%;} th, td { white-space: nowrap; text-align: center; vertical-align: middle; }</style>")
                .append("</head><body class='container-fluid'><h2 class='my-3 text-center'>💻 Laptop Inventory Analysis</h2>");

        // Results Counter
        html.append("<h5 class='text-center'>Results: <span id='resultsCount'>" + laptops.size() + "</span></h5>");

        // Filters Section
        html.append("<div class='container'><div class='row filter-section'>")
                .append("<div class='col-md-2'><input type='text' id='brandFilter' class='form-control' placeholder='🔍 Brand'></div>")
                .append("<div class='col-md-2'><input type='text' id='modelFilter' class='form-control' placeholder='🔍 Model'></div>")
                .append("<div class='col-md-2'><input type='text' id='processorFilter' class='form-control' placeholder='🔍 Processor'></div>")
                .append("<div class='col-md-2'><select id='conditionFilter' class='form-control'><option value=''>🔍 Condition</option>")
                .append(getOptions(conditions))
                .append("</select></div>")
                .append("<div class='col-md-2'><input type='text' id='sellerFilter' class='form-control' placeholder='🏢 Seller'></div>")
                .append("<div class='col-md-2'><select id='yearFilter' class='form-control'><option value=''>📅 Release Year</option>")
                .append(getOptions(releaseYears))
                .append("</select></div>")
                .append("<div class='col-md-12 text-center mt-2'><button class='btn btn-primary' onclick='applyFilters()'>Apply Filters</button></div>")
                .append("</div></div>");

        // Table
        html.append("<div class='table-container'><table id='laptopTable' class='table table-striped'><thead><tr>")
                .append("<th>📌 Lot</th><th>📅 Date</th><th>🏢 Brand</th><th>🔖 Model</th><th>🔧 Technical Specifications</th>")
                .append("<th>📊 Score & Condition</th><th>🛠 Condition Justification</th><th>💰 Price Estimations</th><th>🏢 Seller</th><th>✅ Recommendation</th><th>🖼 Image</th></tr></thead><tbody>");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Laptop laptop : laptops) {
            String bonCoinLink = "https://www.leboncoin.fr/recherche/?category=17&text=" + laptop.getBrand() + "+" + laptop.getModel();

            html.append("<tr>")
                    .append("<td><a href='" + laptop.getLotUrl() + "' target='_blank'>" + laptop.getLotNumber() + "</a></td>")
                    .append("<td>" + (laptop.getDate() != null ? sdf.format(laptop.getDate()) : "N/A") + "</td>")
                    .append("<td>" + laptop.getBrand() + "</td>")
                    .append("<td>" + laptop.getModel() + "</td>")
                    .append("<td>" + laptop.getProcessorBrand() + " " + laptop.getProcessorModel() + "</td>")
                    .append("<td>⭐ " + laptop.getNoteSur10() + "/10 (" + laptop.getCondition() + ")</td>")
                    .append("<td>" + laptop.getReasonForCondition() + "</td>")
                    .append("<td><b>💰 BonCoin:</b> <a href='" + bonCoinLink + "' target='_blank'>" + laptop.getBonCoinEstimation() + "€</a></td>")
                    .append("<td>" + laptop.getMaisonEnchere() + "</td>")
                    .append("<td>" + (laptop.isRecommendedToBuy() ? "✅ Yes" : "❌ No") + "</td>")
                    .append("<td><img src='" + laptop.getImgUrl() + "' width='100px'></td>")
                    .append("</tr>");
        }

        html.append("</tbody></table></div>");

        // JavaScript for Filtering & Auto-Complete
        html.append("<script>")
                .append("$(function() {")
                .append("$('#brandFilter').autocomplete({ source: " + new Gson().toJson(brands) + " });")
                .append("$('#modelFilter').autocomplete({ source: " + new Gson().toJson(models) + " });")
                .append("$('#processorFilter').autocomplete({ source: " + new Gson().toJson(processors) + " });")
                .append("$('#sellerFilter').autocomplete({ source: " + new Gson().toJson(sellers) + " });")
                .append("});")

                .append("function applyFilters() {")
                .append("var brand = $('#brandFilter').val().toLowerCase();")
                .append("var model = $('#modelFilter').val().toLowerCase();")
                .append("var processor = $('#processorFilter').val().toLowerCase();")
                .append("var condition = $('#conditionFilter').val();")
                .append("var seller = $('#sellerFilter').val().toLowerCase();")
                .append("var visibleCount = 0;")
                .append("$('#laptopTable tbody tr').each(function() {")
                .append("var row = $(this);")
                .append("var rowBrand = row.find('td:eq(2)').text().toLowerCase();")
                .append("var rowModel = row.find('td:eq(3)').text().toLowerCase();")
                .append("var rowProcessor = row.find('td:eq(4)').text().toLowerCase();")
                .append("var rowCondition = row.find('td:eq(5)').text();")
                .append("var rowSeller = row.find('td:eq(8)').text().toLowerCase();")
                .append("var match = (!brand || rowBrand.includes(brand)) && (!model || rowModel.includes(model)) && (!processor || rowProcessor.includes(processor)) && (!condition || rowCondition.includes(condition)) && (!seller || rowSeller.includes(seller));")
                .append("if (match) { visibleCount++; row.show(); } else { row.hide(); }")
                .append("}); $('#resultsCount').text(visibleCount);")
                .append("}")
                .append("</script>");

        html.append("</body></html>");

        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(html.toString());
            Desktop.getDesktop().browse(new File(filePath).toURI());
        } catch (IOException e) {
            e.printStackTrace();
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



}