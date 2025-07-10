package org.utils.filter;

import org.enums.Operation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilterUtils {

    /**
     * Converts frontend filter parameters to SearchCriteria objects
     * 
     * @param filters Map of filter parameters from frontend
     * @return List of SearchCriteria objects
     */
    public static List<SearchCriteria> convertToSearchCriteria(Map<String, Object> filters) {
        List<SearchCriteria> criteriaList = new ArrayList<>();
        
        if (filters == null || filters.isEmpty()) {
            return criteriaList;
        }
        
        // Handle type filter (car type)
        if (filters.containsKey("type") && filters.get("type") instanceof List && !((List<?>) filters.get("type")).isEmpty()) {
            List<?> types = (List<?>) filters.get("type");
            criteriaList.add(new SearchCriteria("type", Operation.IN, types));
        }
        
        // Handle transmission filter
        if (filters.containsKey("transmission") && filters.get("transmission") instanceof List && !((List<?>) filters.get("transmission")).isEmpty()) {
            List<?> transmissions = (List<?>) filters.get("transmission");
            criteriaList.add(new SearchCriteria("transmission", Operation.IN, transmissions));
        }
        
        // Handle brand filter
        if (filters.containsKey("brand") && filters.get("brand") instanceof List && !((List<?>) filters.get("brand")).isEmpty()) {
            List<?> brands = (List<?>) filters.get("brand");
            criteriaList.add(new SearchCriteria("producer.name", Operation.IN, brands));
        }
        
        // Handle powertrain filter
        if (filters.containsKey("powertrain") && filters.get("powertrain") instanceof List && !((List<?>) filters.get("powertrain")).isEmpty()) {
            List<?> powertrains = (List<?>) filters.get("powertrain");
            criteriaList.add(new SearchCriteria("powertrain", Operation.IN, powertrains));
        }
        
        // Handle color filter
        if (filters.containsKey("color") && filters.get("color") instanceof List && !((List<?>) filters.get("color")).isEmpty()) {
            List<?> colors = (List<?>) filters.get("color");
            criteriaList.add(new SearchCriteria("color", Operation.IN, colors));
        }
        
        // Handle seating filter
        if (filters.containsKey("seating") && filters.get("seating") instanceof Number) {
            int seating = ((Number) filters.get("seating")).intValue();
            if (seating > 1) {
                criteriaList.add(new SearchCriteria("capacity", Operation.EQUALS, seating));
            }
        }
        
        // Handle region filter
        if (filters.containsKey("region") && filters.get("region") instanceof String) {
            String region = (String) filters.get("region");
            if (!region.isEmpty() && !"-".equals(region)) {
                criteriaList.add(new SearchCriteria("region", Operation.EQUALS, region.toUpperCase()));
            }
        }
        
        // Handle price range filter
        if (filters.containsKey("range") && filters.get("range") instanceof List && ((List<?>) filters.get("range")).size() == 2) {
            List<?> range = (List<?>) filters.get("range");
            if (range.get(0) instanceof Number && range.get(1) instanceof Number) {
                double min = ((Number) range.get(0)).doubleValue();
                double max = ((Number) range.get(1)).doubleValue();
                
                if (min > 0 || max > 0) {
                    Object[] rangeValues = new Object[] { min, max };
                    criteriaList.add(new SearchCriteria("rentPrice", Operation.BETWEEN, rangeValues));
                }
            }
        }
        
        // Handle year filter
        if (filters.containsKey("year") && filters.get("year") instanceof Number) {
            int year = ((Number) filters.get("year")).intValue();
            if (year > 0) {
                criteriaList.add(new SearchCriteria("carModelYear", Operation.EQUALS, year));
            }
        }
        
        return criteriaList;
    }
    
    /**
     * Builds a list of SearchCriteria from request parameters
     * 
     * @param type Car type
     * @param brand Car brand (producer name)
     * @param color Car color
     * @param minPrice Minimum rent price
     * @param maxPrice Maximum rent price
     * @param minYear Minimum model year
     * @param maxYear Maximum model year
     * @param capacity Car capacity (seating)
     * @param status Car status
     * @return List of SearchCriteria objects
     */
    public static List<SearchCriteria> buildSearchCriteria(
            String type, String brand, String color,
            Double minPrice, Double maxPrice,
            Integer minYear, Integer maxYear,
            Integer capacity, String status) {
        
        List<SearchCriteria> criteriaList = new ArrayList<>();
        
        // Type filter
        if (type != null && !type.isEmpty()) {
            criteriaList.add(new SearchCriteria("type", Operation.EQUALS, type));
        }
        
        // Brand filter
        if (brand != null && !brand.isEmpty()) {
            criteriaList.add(new SearchCriteria("producer.name", Operation.EQUALS, brand));
        }
        
        // Color filter
        if (color != null && !color.isEmpty()) {
            criteriaList.add(new SearchCriteria("color", Operation.EQUALS, color));
        }
        
        // Price range filter
        if (minPrice != null && maxPrice != null) {
            criteriaList.add(new SearchCriteria("rentPrice", Operation.BETWEEN, new Object[] { minPrice, maxPrice }));
        } else if (minPrice != null) {
            criteriaList.add(new SearchCriteria("rentPrice", Operation.GREATER_THAN_OR_EQUAL_TO, minPrice));
        } else if (maxPrice != null) {
            criteriaList.add(new SearchCriteria("rentPrice", Operation.LESS_THAN_OR_EQUAL_TO, maxPrice));
        }
        
        // Year range filter
        if (minYear != null && maxYear != null) {
            criteriaList.add(new SearchCriteria("carModelYear", Operation.BETWEEN, new Object[] { minYear, maxYear }));
        } else if (minYear != null) {
            criteriaList.add(new SearchCriteria("carModelYear", Operation.GREATER_THAN_OR_EQUAL_TO, minYear));
        } else if (maxYear != null) {
            criteriaList.add(new SearchCriteria("carModelYear", Operation.LESS_THAN_OR_EQUAL_TO, maxYear));
        }
        
        // Capacity filter
        if (capacity != null && capacity > 0) {
            criteriaList.add(new SearchCriteria("capacity", Operation.EQUALS, capacity));
        }
        
        // Status filter
        if (status != null && !status.isEmpty()) {
            criteriaList.add(new SearchCriteria("status", Operation.EQUALS, status));
        }
        
        return criteriaList;
    }
} 