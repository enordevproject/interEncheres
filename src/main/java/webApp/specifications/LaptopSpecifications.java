package webApp.specifications;

import org.springframework.data.jpa.domain.Specification;
import webApp.models.Laptop;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class LaptopSpecifications {



        public static Specification<Laptop> filterByParams(Map<String, String> filters) {
            Specification<Laptop> spec = Specification.where(null);

            // ✅ Filter by Lot Number
            if (filters.containsKey("lot_number")) {
                int lotNumber = Integer.parseInt(filters.get("lot_number"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("lotNumber"), lotNumber));
            }

            // ✅ Filter by Brand
            if (filters.containsKey("brand")) {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("brand")), "%" + filters.get("brand").toLowerCase() + "%"));
            }

            // ✅ Filter by Model
            if (filters.containsKey("model")) {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("model")), "%" + filters.get("model").toLowerCase() + "%"));
            }
            // ✅ Fix: Allow filtering by Min and Max Release Year
            if (filters.containsKey("minReleaseYear")) {
                int minYear = Integer.parseInt(filters.get("minReleaseYear"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.greaterThanOrEqualTo(root.get("releaseYear"), minYear));
            }

            if (filters.containsKey("maxReleaseYear")) {
                int maxYear = Integer.parseInt(filters.get("maxReleaseYear"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.lessThanOrEqualTo(root.get("releaseYear"), maxYear));
            }

            // ✅ Filter by RAM Size (GB)
            if (filters.containsKey("ramSize")) {
                int ramSize = Integer.parseInt(filters.get("ramSize"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("ramSize"), ramSize));
            }

            // ✅ Filter by Storage Type (SSD / HDD)
            if (filters.containsKey("storageType")) {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("storageType"), filters.get("storageType")));
            }

            // ✅ Filter by Storage Capacity (GB)
            if (filters.containsKey("storageCapacity")) {
                int storageCapacity = Integer.parseInt(filters.get("storageCapacity"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("storageCapacity"), storageCapacity));
            }

            // ✅ Filter by Processor Brand
            if (filters.containsKey("processorBrand")) {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("processorBrand")), "%" + filters.get("processorBrand").toLowerCase() + "%"));
            }

            // ✅ Filter by Processor Model
            if (filters.containsKey("processorModel")) {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("processorModel")), "%" + filters.get("processorModel").toLowerCase() + "%"));
            }

            // ✅ Filter by GPU Type
            if (filters.containsKey("gpuType")) {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("gpuType")), "%" + filters.get("gpuType").toLowerCase() + "%"));
            }
            if (filters.containsKey("maisonEnchere")) {
                String seller = filters.get("maisonEnchere").toLowerCase();
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("maisonEnchere")), "%" + seller + "%")
                );
            }



            // ✅ Filter by GPU VRAM (GB)
            if (filters.containsKey("gpuVram")) {
                int gpuVram = Integer.parseInt(filters.get("gpuVram"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("gpuVram"), gpuVram));
            }

            // ✅ Filter by Screen Size (in inches)
            if (filters.containsKey("screenSize")) {
                double screenSize = Double.parseDouble(filters.get("screenSize"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("screenSize"), screenSize));
            }

            // ✅ Filter by Touchscreen (true / false)
            if (filters.containsKey("touchScreen")) {
                boolean touchScreen = Boolean.parseBoolean(filters.get("touchScreen"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("touchScreen"), touchScreen));
            }

            // ✅ Filter by Fingerprint Sensor (true / false)
            if (filters.containsKey("fingerprintSensor")) {
                boolean fingerprintSensor = Boolean.parseBoolean(filters.get("fingerprintSensor"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("fingerprintSensor"), fingerprintSensor));
            }

            // ✅ Filter by Face Recognition (true / false)
            if (filters.containsKey("faceRecognition")) {
                boolean faceRecognition = Boolean.parseBoolean(filters.get("faceRecognition"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("faceRecognition"), faceRecognition));
            }

            // ✅ Filter by Battery Life
            if (filters.containsKey("batteryLife")) {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(root.get("batteryLife"), "%" + filters.get("batteryLife") + "%"));
            }

            // ✅ Filter by Weight (kg)
            if (filters.containsKey("weight")) {
                double weight = Double.parseDouble(filters.get("weight"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("weight"), weight));
            }

            // ✅ Filter by Operating System
            if (filters.containsKey("operatingSystem")) {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("operatingSystem")), "%" + filters.get("operatingSystem").toLowerCase() + "%"));
            }

            // ✅ Filter by Release Year
            if (filters.containsKey("releaseYear")) {
                int releaseYear = Integer.parseInt(filters.get("releaseYear"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("releaseYear"), releaseYear));
            }

            // ✅ Fix: Allow filtering by Min and Max BonCoin Price
            if (filters.containsKey("minBonCoinEstimation")) {
                double minPrice = Double.parseDouble(filters.get("minBonCoinEstimation"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.greaterThanOrEqualTo(root.get("bonCoinEstimation"), minPrice));
            }

            if (filters.containsKey("maxBonCoinEstimation")) {
                double maxPrice = Double.parseDouble(filters.get("maxBonCoinEstimation"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.lessThanOrEqualTo(root.get("bonCoinEstimation"), maxPrice));
            }

// ✅ Fix: Allow filtering by Min and Max Facebook Price
            if (filters.containsKey("minFacebookEstimation")) {
                double minPrice = Double.parseDouble(filters.get("minFacebookEstimation"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.greaterThanOrEqualTo(root.get("facebookEstimation"), minPrice));
            }

            if (filters.containsKey("maxFacebookEstimation")) {
                double maxPrice = Double.parseDouble(filters.get("maxFacebookEstimation"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.lessThanOrEqualTo(root.get("facebookEstimation"), maxPrice));
            }

// ✅ Fix: Allow filtering by Min and Max Internet Price
            if (filters.containsKey("minInternetEstimation")) {
                double minPrice = Double.parseDouble(filters.get("minInternetEstimation"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.greaterThanOrEqualTo(root.get("internetEstimation"), minPrice));
            }

            if (filters.containsKey("maxInternetEstimation")) {
                double maxPrice = Double.parseDouble(filters.get("maxInternetEstimation"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.lessThanOrEqualTo(root.get("internetEstimation"), maxPrice));
            }
            // ✅ Fix: Allow filtering by Min and Max Performance Score
            if (filters.containsKey("minNoteSur10")) {
                int minScore = Integer.parseInt(filters.get("minNoteSur10"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.greaterThanOrEqualTo(root.get("noteSur10"), minScore));
            }

            if (filters.containsKey("maxNoteSur10")) {
                int maxScore = Integer.parseInt(filters.get("maxNoteSur10"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.lessThanOrEqualTo(root.get("noteSur10"), maxScore));
            }


            // ✅ Filter by Condition Rating (0-10)
            if (filters.containsKey("noteSur10")) {
                int noteSur10 = Integer.parseInt(filters.get("noteSur10"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("noteSur10"), noteSur10));
            }

            // ✅ Filter by Recommended to Buy (true / false)
            if (filters.containsKey("recommendedToBuy")) {
                boolean recommended = Boolean.parseBoolean(filters.get("recommendedToBuy"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("recommendedToBuy"), recommended));
            }

            // ✅ Filter by Auction Date
            if (filters.containsKey("date")) {
                Date auctionDate = parseDate(filters.get("date"));
                if (auctionDate != null) {
                    spec = spec.and((root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(root.get("date"), auctionDate));
                }
            }
            if (filters.containsKey("minDate")) {
                Date minAuctionDate = parseDate(filters.get("minDate"));
                if (minAuctionDate != null) {
                    spec = spec.and((root, query, criteriaBuilder) ->
                            criteriaBuilder.greaterThanOrEqualTo(root.get("date"), minAuctionDate));
                }
            }

            if (filters.containsKey("maxDate")) {
                Date maxAuctionDate = parseDate(filters.get("maxDate"));
                if (maxAuctionDate != null) {
                    spec = spec.and((root, query, criteriaBuilder) ->
                            criteriaBuilder.lessThanOrEqualTo(root.get("date"), maxAuctionDate));
                }
            }


            // ✅ Filter by Product Condition Image
            if (filters.containsKey("etatProduitImage")) {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("etatProduitImage")), "%" + filters.get("etatProduitImage").toLowerCase() + "%"));
            }
            // ✅ Fix: Ensure filtering for `product_condition`
            if (filters.containsKey("product_condition")) {
                String condition = filters.get("product_condition").trim();

                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(criteriaBuilder.lower(root.get("productCondition")), condition.toLowerCase()));

                System.out.println("✅ Filtering by product_condition: " + condition);
            }




            // ✅ Filter by Reason for Condition
            if (filters.containsKey("reasonForCondition")) {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("reasonForCondition")), "%" + filters.get("reasonForCondition").toLowerCase() + "%"));
            }

            // ✅ Filter by Reason for Score
            if (filters.containsKey("reasonForScore")) {
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("reasonForScore")), "%" + filters.get("reasonForScore").toLowerCase() + "%"));
            }


            return spec;
        }

        private static Date parseDate(String dateString) {
            try {
                return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
            } catch (ParseException e) {
                return null;
            }
        }
    }


