package org.service;

import org.enums.Operation;
import org.utils.filter.SearchCriteria;

import java.util.List;
import java.util.Map;

/**
 * Service for handling filter operations
 */
public interface FilterService<T> {
    
    /**
     * Filter entities by criteria with AND operation
     * 
     * @param criteria List of search criteria
     * @return List of filtered entities
     */
    List<T> filter(List<SearchCriteria> criteria);
    
    /**
     * Filter entities by criteria with specified logical operation
     * 
     * @param criteria List of search criteria
     * @param operation Logical operation (AND, OR)
     * @return List of filtered entities
     */
    List<T> filter(List<SearchCriteria> criteria, Operation operation);
    
    /**
     * Filter entities by map of filter parameters
     * 
     * @param filters Map of filter parameters
     * @return List of filtered entities
     */
    List<T> filterByMap(Map<String, Object> filters);
    
    /**
     * Filter entities by map of filter parameters with specified logical operation
     * 
     * @param filters Map of filter parameters
     * @param operation Logical operation (AND, OR)
     * @return List of filtered entities
     */
    List<T> filterByMap(Map<String, Object> filters, Operation operation);
} 