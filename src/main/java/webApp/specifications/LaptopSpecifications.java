package webApp.specifications;

import org.springframework.data.jpa.domain.Specification;
import webApp.models.Laptop;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import jakarta.persistence.criteria.Predicate; // ✅ Import This!

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LaptopSpecifications {



        public static Specification<Laptop> filterByParams(Map<String, String> filters) {
            Specification<Laptop> spec = Specification.where(null);

            // ✅ Filter by Lot Number
            if (filters.containsKey("lot_number")) {
                int lotNumber = Integer.parseInt(filters.get("lot_number"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("lotNumber"), lotNumber));
            }

            // ✅ Allow Multi-Value Search for Brand
            if (filters.containsKey("brand")) {
                String[] brands = filters.get("brand").toLowerCase().split(",");

                spec = spec.and((root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    for (String brand : brands) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("brand")), "%" + brand.trim() + "%"));
                    }
                    return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
                });
            }




            // ✅ Allow Multi-Value Search for Model using OR conditions
                        if (filters.containsKey("model")) {
                            String[] models = filters.get("model").toLowerCase().split(",");

                            spec = spec.and((root, query, criteriaBuilder) -> {
                                   List<Predicate> predicates = new ArrayList<>(); // ✅ Use List<Predicate>
                                for (String model : models) {
                                    predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("model")), "%" + model.trim() + "%"));
                                }
                                return criteriaBuilder.or(predicates.toArray(new Predicate[0])); // ✅ Correct Predicate Usage
                            });
            }
            // ✅ Allow Multi-Value Search for Base Model using OR conditions
            if (filters.containsKey("baseModel")) {
                String[] baseModels = filters.get("baseModel").toLowerCase().split(",");

                spec = spec.and((root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    for (String base : baseModels) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("model")), base.trim() + "%")); // ✅ Base Model Starts With
                    }
                    return criteriaBuilder.or(predicates.toArray(new Predicate[0])); // ✅ Correct Predicate Usage
                });
            }
            // ✅ Allow Multi-Value Search for Reference (Second Part of Model)
            if (filters.containsKey("reference")) {
                String[] references = filters.get("reference").toLowerCase().split(",");

                spec = spec.and((root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    for (String reference : references) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("model")), "% " + reference.trim() + "%"));
                        // ✅ Matches second part (e.g., "Latitude 5480" -> finds "5480")
                    }
                    return criteriaBuilder.or(predicates.toArray(new Predicate[0])); // ✅ Correct Predicate Usage
                });
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

            // ✅ Allow Multi-Value Search for RAM Size (GB) using OR conditions
            if (filters.containsKey("ramSize")) {
                String[] ramSizes = filters.get("ramSize").split(",");

                spec = spec.and((root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    for (String ramSize : ramSizes) {
                        predicates.add(criteriaBuilder.equal(root.get("ramSize"), Integer.parseInt(ramSize.trim())));
                    }
                    return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
                });
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

            // ✅ Allow Multi-Value Search for Processor Brand using OR conditions
            if (filters.containsKey("processorBrand")) {
                String[] processorBrands = filters.get("processorBrand").toLowerCase().split(",");

                spec = spec.and((root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    for (String brand : processorBrands) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("processorBrand")), "%" + brand.trim() + "%"));
                    }
                    return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
                });
            }


            // ✅ Filter by Processor Model
            if (filters.containsKey("processorModel")) {
                String[] processorModels = filters.get("processorModel").toLowerCase().split(",");

                spec = spec.and((root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    for (String processor : processorModels) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("processorModel")), "%" + processor.trim() + "%"));
                    }
                    return criteriaBuilder.or(predicates.toArray(new Predicate[0])); // ✅ OR condition for multiple processors
                });
            }


            // ✅ Filter by trusted auction houses (maisonEnchere)
            if (filters.containsKey("maisonEnchere")) {
                String[] sellers = filters.get("maisonEnchere").toLowerCase().split(",");

                spec = spec.and((root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    for (String seller : sellers) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("maisonEnchere")), "%" + seller.trim() + "%"));
                    }
                    return criteriaBuilder.or(predicates.toArray(new Predicate[0])); // ✅ OR condition for multiple sellers
                });
            }

            // ✅ Exclude specific auction houses (excludeMaisonEnchere)
            if (filters.containsKey("excludeMaisonEnchere")) {
                String[] excludedHouses = filters.get("excludeMaisonEnchere").toLowerCase().split(",");

                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.not(root.get("maisonEnchere").in((Object[]) excludedHouses))
                );
            }



            if (filters.containsKey("gpuBrand")) {
                String[] gpuBrands = filters.get("gpuBrand").split(",");

                spec = spec.and((root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    for (String brand : gpuBrands) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("gpuType")), "%" + brand.trim().toLowerCase() + "%"));
                    }
                    return criteriaBuilder.or(predicates.toArray(new Predicate[0])); // ✅ OR for multiple selections
                });
            }

            if (filters.containsKey("gpuModel")) {
                String[] gpuModels = filters.get("gpuModel").split(",");

                spec = spec.and((root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    for (String model : gpuModels) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("gpuModel")), "%" + model.trim().toLowerCase() + "%"));
                    }
                    return criteriaBuilder.or(predicates.toArray(new Predicate[0])); // ✅ Match any selected GPU models
                });
            }


            // ✅ Filter by GPU VRAM (GB)
            if (filters.containsKey("gpuVram")) {
                int gpuVram = Integer.parseInt(filters.get("gpuVram"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("gpuVram"), gpuVram));
            }

            if (filters.containsKey("screenSize")) {
                String[] screenSizes = filters.get("screenSize").split(",");

                spec = spec.and((root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    for (String size : screenSizes) {
                        predicates.add(criteriaBuilder.equal(root.get("screenSize"), Double.parseDouble(size.trim())));
                    }
                    return criteriaBuilder.or(predicates.toArray(new Predicate[0])); // ✅ Combine all conditions using OR
                });
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

            // ✅ Filter by Operating System (Allow multiple values using OR)
            if (filters.containsKey("operatingSystem")) {
                String[] operatingSystems = filters.get("operatingSystem").toLowerCase().split(",");

                spec = spec.and((root, query, criteriaBuilder) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    for (String os : operatingSystems) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("operatingSystem")), "%" + os.trim() + "%"));
                    }
                    return criteriaBuilder.or(predicates.toArray(new Predicate[0])); // ✅ OR condition for multiple OS values
                });
            }


            // ✅ Filter by Release Year
            if (filters.containsKey("releaseYear")) {
                int releaseYear = Integer.parseInt(filters.get("releaseYear"));
                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("releaseYear"), releaseYear));
            }

            if (filters.containsKey("minBonCoinEstimation") || filters.containsKey("maxBonCoinEstimation")) {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    List<Predicate> pricePredicates = new ArrayList<>();

                    if (filters.containsKey("minBonCoinEstimation")) {
                        try {
                            double minPrice = parsePrice(filters.get("minBonCoinEstimation"));
                            pricePredicates.add(
                                    criteriaBuilder.greaterThanOrEqualTo(
                                            criteriaBuilder.toDouble(root.get("bonCoinEstimation")),
                                            minPrice
                                    )
                            );
                        } catch (NumberFormatException e) {
                            System.err.println("❌ Invalid minBonCoinEstimation value: " + filters.get("minBonCoinEstimation"));
                        }
                    }

                    if (filters.containsKey("maxBonCoinEstimation")) {
                        try {
                            double maxPrice = parsePrice(filters.get("maxBonCoinEstimation"));
                            pricePredicates.add(
                                    criteriaBuilder.lessThanOrEqualTo(
                                            criteriaBuilder.toDouble(root.get("bonCoinEstimation")),
                                            maxPrice
                                    )
                            );
                        } catch (NumberFormatException e) {
                            System.err.println("❌ Invalid maxBonCoinEstimation value: " + filters.get("maxBonCoinEstimation"));
                        }
                    }

                    return criteriaBuilder.and(pricePredicates.toArray(new Predicate[0]));
                });
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

            // ✅ Filter by Exact Auction Date (as String)
            if (filters.containsKey("date")) {
                String auctionDate = filters.get("date").trim();

                spec = spec.and((root, query, criteriaBuilder) ->
                        criteriaBuilder.equal(root.get("date"), auctionDate) // ✅ Compare as String
                );
            }



            // ✅ DATE FILTERING (Fixes minDate/maxDate issues)
            if (filters.containsKey("minDate") || filters.containsKey("maxDate")) {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    List<Predicate> datePredicates = new ArrayList<>();
                    SimpleDateFormat dbFormat = new SimpleDateFormat("dd/MM/yyyy"); // ✅ Match Database Format
                    SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd"); // API Uses YYYY-MM-DD

                    // ✅ Function to Convert API Date (YYYY-MM-DD) to Database Format (DD/MM/YYYY)
                    Function<String, String> convertToDatabaseDate = dateStr -> {
                        dateStr = dateStr.trim();

                        // ✅ If format is already `DD/MM/YYYY`, return as is
                        if (dateStr.matches("\\d{2}/\\d{2}/\\d{4}")) {
                            return dateStr;
                        }

                        // ✅ Convert `YYYY-MM-DD` to `DD/MM/YYYY`
                        if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                            try {
                                Date parsedDate = apiFormat.parse(dateStr);
                                return dbFormat.format(parsedDate);
                            } catch (Exception e) {
                                System.err.println("❌ Invalid date format: " + dateStr);
                            }
                        }

                        // ✅ Handle relative time (e.g., "2j 10h")
                        Pattern pattern = Pattern.compile("(\\d+)j (\\d+)h");
                        Matcher matcher = pattern.matcher(dateStr);
                        if (matcher.find()) {
                            int days = Integer.parseInt(matcher.group(1));
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DAY_OF_YEAR, days);
                            return dbFormat.format(calendar.getTime()); // Convert to `DD/MM/YYYY`
                        }

                        return null; // Return null if format is not recognized
                    };

                    if (filters.containsKey("minDate")) {
                        String minDate = convertToDatabaseDate.apply(filters.get("minDate"));
                        if (minDate != null) {
                            System.out.println("✅ Applying minDate filter: " + minDate);
                            datePredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), minDate));
                        }
                    }

                    if (filters.containsKey("maxDate")) {
                        String maxDate = convertToDatabaseDate.apply(filters.get("maxDate"));
                        if (maxDate != null) {
                            System.out.println("✅ Applying maxDate filter: " + maxDate);
                            datePredicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date"), maxDate));
                        }
                    }

                    if (datePredicates.isEmpty()) {
                        System.out.println("⚠️ No valid date filters applied!");
                        return null; // No date filter applied
                    } else {
                        return criteriaBuilder.and(datePredicates.toArray(new Predicate[0]));
                    }
                });
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
    private static double parsePrice(String price) throws NumberFormatException {
        return Double.parseDouble(price.replaceAll("[^0-9.]", "")); // ✅ Remove € and non-numeric characters
    }


}

