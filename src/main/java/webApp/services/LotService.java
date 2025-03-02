package webApp.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import webApp.models.Lot;
import webApp.models.Results;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class LotService {

    private static final Logger log = LoggerFactory.getLogger(LotService.class);

    public String processLotsWithGPT() throws IOException {
        log.info("🔄 [Start] Processing lots with GPT-4...");

        List<Lot> lotsFromDatabase = Results.getAllLotsFromDatabase();
        if (lotsFromDatabase.isEmpty()) {
            log.warn("⚠️ No lots found in the database.");
            return "⚠️ No lots found in the database.";
        }

        log.info("✅ Retrieved {} lots.", lotsFromDatabase.size());

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<Void>> futures = new ArrayList<>();

        for (Lot lot : lotsFromDatabase) {
            futures.add(executorService.submit(() -> {
                Results.processLot(lot);
                return null;
            }));
        }

        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                log.error("❌ Error processing a lot: {}", e.getMessage(), e);
            }
        }

        executorService.shutdown();
        log.info("✅ [Finish] Processing complete.");
        return "✅ Processing complete.";
    }
}
