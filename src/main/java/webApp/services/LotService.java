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
import java.util.concurrent.TimeUnit;

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
     * ✅ Logs messages with category & exposes them to the UI
     */
    private void logMessage(String type, String message) {
        String formattedMessage = "[" + getFormattedTimestamp() + "] [" + type + "] " + message;
        log.info(formattedMessage);
        logMessages.add(formattedMessage);

        // ✅ Keep only last 200 logs to prevent memory overflow
        if (logMessages.size() > 200) {
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
        long startTime = System.currentTimeMillis();
        logMessage("INFO", "🔄 [Start] Processing lots with GPT-4...");

        // ✅ Fetch lots from the database
        List<Lot> lotsFromDatabase = Results.getAllLotsFromDatabase();
        int totalLots = lotsFromDatabase.size();
        logMessage("DEBUG", "📊 Lots retrieved: " + totalLots);

        if (totalLots == 0) {
            logMessage("WARN", "⚠️ No lots found in the database. Shutting down.");

            // ✅ Ensure proper shutdown before exiting
            return "⚠️ No lots found in the database.";
        }

        logMessage("INFO", "✅ Retrieved " + totalLots + " lots.");
        int threadPoolSize = Math.min(10, totalLots);
        logMessage("INFO", "⚙️ Using " + threadPoolSize + " worker threads for processing.");

        // ✅ Initialize Thread Pool
        ExecutorService executorService = Executors.newFixedThreadPool(threadPoolSize);
        List<Future<Void>> futures = new ArrayList<>();

        long lotStartTime = System.currentTimeMillis();

        try {
            for (int i = 0; i < totalLots; i++) {
                Lot lot = lotsFromDatabase.get(i);
                int currentIndex = i + 1;

                futures.add(executorService.submit(() -> {
                    try {
                        long singleStartTime = System.currentTimeMillis();
                        Results.processLot(lot);
                        long singleEndTime = System.currentTimeMillis();
                        long processingTime = singleEndTime - singleStartTime;

                        double progress = ((double) currentIndex / totalLots) * 100;
                        long elapsedTime = System.currentTimeMillis() - lotStartTime;
                        long estimatedRemainingTime = (long) ((elapsedTime / (double) currentIndex) * (totalLots - currentIndex));

                        logMessage("INFO", String.format(
                                "📊 [%d/%d] Processed lot %s | Date: %s | Maison Enchere: %s | ⏳ %dms | Progress: %.2f%% | ETA: %ds",
                                currentIndex, totalLots, lot.getNumber(), lot.getDate(), lot.getMaisonEnchere(),
                                processingTime, progress, estimatedRemainingTime / 1000
                        ));
                    } catch (Exception e) {
                        logMessage("ERROR", "❌ Error processing lot " + lot.getNumber() + ": " + e.getMessage());
                    }
                    return null;
                }));
            }

            // ✅ Wait for all threads to finish
            for (Future<Void> future : futures) {
                try {
                    future.get(30, TimeUnit.SECONDS); // ⏳ Timeout to prevent blocking indefinitely
                } catch (Exception e) {
                    logMessage("ERROR", "❌ Error waiting for task completion: " + e.getMessage());
                }
            }
        } finally {
            // ✅ Ensure proper shutdown of ExecutorService
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    logMessage("WARN", "⚠️ Forced executor shutdown due to timeout.");
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                logMessage("ERROR", "❌ Thread interrupted, forcing shutdown.");
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        long endTime = System.currentTimeMillis();
        long totalProcessingTime = endTime - startTime;
        logMessage("INFO", "✅ [Finish] GPT Processing complete in " + (totalProcessingTime / 1000) + "s. Deleting all lots...");

        deleteAllLots();

        return "✅ Processing complete and lots deleted.";
    }



    /**
     * ✅ Deletes all lots from the database after processing
     */
    public void deleteAllLots() {
        try {
            lotRepository.deleteAll();
            logMessage("INFO", "🗑️ All lots deleted successfully from the database.");
        } catch (Exception e) {
            logMessage("ERROR", "❌ Error deleting lots: " + e.getMessage());
        }
    }
}
