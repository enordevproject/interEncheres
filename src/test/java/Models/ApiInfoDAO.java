package Models;

import hibernate.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sanitySuite.FirstTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiInfoDAO {
    private static final Map<String, ApiInfo> cache = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FirstTest.class);
    /**
     * Saves API info to the database.
     */
    public static void saveApiInfo(ApiInfo apiInfo) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(apiInfo); // Using persist() instead of save()
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    /**
     * Retrieves API info by name with caching.
     */
    public static ApiInfo getApiInfoByName(String apiName) {
        log.info("🔍 Recherche des informations API pour : {}", apiName);

        // Vérifier d'abord le cache
        if (cache.containsKey(apiName)) {
            log.info("✅ API trouvée dans le cache pour : {}", apiName);
            return cache.get(apiName);
        }

        // Recherche en base de données
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            log.info("📡 Requête Hibernate en cours pour récupérer l'API '{}'", apiName);

            ApiInfo apiInfo = session.createQuery(
                            "FROM ApiInfo a WHERE a.apiName = :apiName", ApiInfo.class)
                    .setParameter("apiName", apiName)
                    .setCacheable(true) // Enable Hibernate caching
                    .uniqueResult();

            if (apiInfo != null) {
                log.info("✅ API trouvée en base de données : {}", apiInfo.getApiName());
                cache.put(apiName, apiInfo);
            } else {
                log.warn("⚠️ Aucune API trouvée en base pour : {}", apiName);
            }

            return apiInfo;
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération de l'API '{}' : {}", apiName, e.getMessage());
            return null;
        }
    }


    /**
     * Retrieves all API info records.
     */
    public static List<ApiInfo> getAllApiInfos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM ApiInfo", ApiInfo.class)
                    .setCacheable(true) // Enable Hibernate caching
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
