package webApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import webApp.models.Lot;
import webApp.models.Laptop;
import webApp.models.Results;

import java.util.List;

@Service
public class ResultsService {

    /**
     * Processes and stores lots using the methods defined in the Results class.
     * Each lot is processed and added to the database if it does not exist.
     *
     * @param lots the list of Lot objects to be processed and stored.
     */
    @Transactional
    public void processAndStoreLots(List<Lot> lots) {
        Results results = new Results();
        results.pushLotsToDatabase(lots); // Using the method from Results to handle database interactions
    }

    /**
     * Processes a lot to determine if it should be converted into a Laptop and stored.
     *
     * @param lot the Lot to be processed.
     */
    @Transactional
    public void processAndStoreLot(Lot lot) {
        // Assuming the Results class has a method to process lots and convert them to laptops
        try {
            Results.processLot(lot); // Call the static method to handle the lot
        } catch (Exception e) {
            // Log and handle any exceptions that occur during processing
            System.err.println("Error processing lot: " + e.getMessage());
        }
    }

    /**
     * Retrieves all laptops stored in the database.
     *
     * @return a list of all Laptop objects from the database.
     */
    public List<Laptop> retrieveAllLaptops() {
        return Results.getAllLaptopsFromDatabase(); // Delegate to the Results method
    }

    /**
     * Additional methods to interact with the database could be included here,
     * utilizing the static methods from Results for operations like updating or deleting laptops.
     */
}
