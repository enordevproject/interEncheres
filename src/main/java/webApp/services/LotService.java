package webApp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import webApp.models.Lot;
import webApp.models.Results;
import webApp.repositories.LotRepository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class LotService {
    private static final Logger log = LoggerFactory.getLogger(LotService.class);
    private final List<String> logMessages = new CopyOnWriteArrayList<>();
    private final LotRepository lotRepository;

    public LotService(LotRepository lotRepository) {
        this.lotRepository = lotRepository;
    }

    /**
     * ✅ Get formatted timestamp for logs
     */
    private String getFormattedTimestamp() {
        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    /**
     * ✅ Logs messages & exposes them to the UI
     */
    private void logMessage(String message) {
        String formattedMessage = "[" + getFormattedTimestamp() + "] " + message;
        log.info(formattedMessage);
        logMessages.add(formattedMessage);

        // ✅ Keep only last 100 logs to prevent memory overflow
        if (logMessages.size() > 100) {
            logMessages.remove(0);
        }
    }

    /**
     * ✅ Fetch logs for frontend
     */
    public List<String> getLogs() {
        return logMessages;
    }

    /**
     * ✅ Clears logs before each new search
     */
    public void clearLogs() {
        logMessages.clear();
        log.info("🧹 Logs cleared before new search.");
    }

    /**
     * ✅ Processes lots using GPT & deletes them after processing
     */
    public String processLotsWithGPT() throws IOException {
        clearLogs();
        logMessage("🔄 [Start] Processing lots with GPT-4...");

        List<Lot> lotsFromDatabase = Results.getAllLotsFromDatabase();
        int totalLots = lotsFromDatabase.size();

        if (totalLots == 0) {
            logMessage("⚠️ No lots found in the database.");
            return "⚠️ No lots found in the database.";
        }

        logMessage("✅ Retrieved " + totalLots + " lots.");

        int threadPoolSize = Math.min(10, totalLots);
        logMessage("⚙️ Using " + threadPoolSize + " worker threads for processing.");

        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
        List<Future<Void>> futures = new ArrayList<>();

        for (Lot lot : lotsFromDatabase) {
            futures.add(executorService.submit(() -> {
                synchronized (lot) { // ✅ Ensures thread safety for each lot
                    try {
                        Results.processLot(lot);
                        logMessage("📊 Processed lot " + lot.getNumber() + " | Date: " + lot.getDate() + " | Maison Enchere: " + lot.getMaisonEnchere());
                    } catch (Exception e) {
                        logMessage("❌ Error processing lot " + lot.getNumber() + ": " + e.getMessage());
                    }
                }
                return null;
            }));
        }

        for (Future<Void> future : futures) {
            try {
                future.get(); // ✅ Ensure all tasks finish
            } catch (Exception e) {
                logMessage("❌ Error waiting for task completion: " + e.getMessage());
            }
        }

        executorService.shutdown();
        logMessage("✅ [Finish] GPT Processing complete. Deleting all lots...");

        deleteAllLots();

        return "✅ Processing complete and lots deleted.";
    }


    /**
     * ✅ Deletes all lots from the database after processing
     */
    public void deleteAllLots() {
        try {
            lotRepository.deleteAll();
            logMessage("🗑️ All lots deleted successfully from the database.");
        } catch (Exception e) {
            logMessage("❌ Error deleting lots: " + e.getMessage());
        }
    }
}
