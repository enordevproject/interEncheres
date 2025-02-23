package hibernate;

import Models.Lot;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class HibernateTest {

    @Test
    public void testHibernateConnection() {


        // Open session from HibernateUtil
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();

        // Create a new Lot object
        Lot lot = new Lot("1234", "Test Description", "http://image.url",
                "100", "22/05/2025", "Maison Enchere", "http://lot.url");

        // Save the Lot object to the database
        session.save(lot);

        // Commit the transaction
        tx.commit();

        // Close the session
        session.close();

        // Assert that the object has been saved (check the number as primary key)
        assertNotNull(lot.getNumber(), "Lot number should not be null after save");

        // Optionally, you can print the lot's number
        System.out.println("Lot saved with number: " + lot.getNumber());
    }
}
