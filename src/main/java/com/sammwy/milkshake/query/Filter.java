package com.sammwy.milkshake.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for constructing query filters used in database operations.
 * Provides nested classes for building find queries and update operations.
 */
public abstract class Filter {
    protected Map<String, Object> criteria = new HashMap<>();

    /**
     * Filter builder for find/query operations.
     * Supports various comparison operators for building complex queries.
     */
    public static class Find extends Filter {

        /**
         * Adds an equality condition to the filter.
         * 
         * @param field The field name to compare
         * @param value The value to match exactly
         * @return The current Find instance for method chaining
         */
        public Find eq(String field, Object value) {
            criteria.put(field, value);
            return this;
        }

        /**
         * Adds a not-equal condition to the filter.
         * 
         * @param field The field name to compare
         * @param value The value that should not match
         * @return The current Find instance for method chaining
         */
        public Find ne(String field, Object value) {
            Map<String, Object> condition = new HashMap<>();
            condition.put("$ne", value);
            criteria.put(field, condition);
            return this;
        }

        /**
         * Adds a greater-than condition to the filter.
         * 
         * @param field The field name to compare
         * @param value The value that field should be greater than
         * @return The current Find instance for method chaining
         */
        public Find gt(String field, Object value) {
            Map<String, Object> condition = new HashMap<>();
            condition.put("$gt", value);
            criteria.put(field, condition);
            return this;
        }

        /**
         * Adds a less-than condition to the filter.
         * 
         * @param field The field name to compare
         * @param value The value that field should be less than
         * @return The current Find instance for method chaining
         */
        public Find lt(String field, Object value) {
            Map<String, Object> condition = new HashMap<>();
            condition.put("$lt", value);
            criteria.put(field, condition);
            return this;
        }

        /**
         * Adds an in-array condition to the filter.
         * 
         * @param field  The field name to compare
         * @param values List of values that the field should match against
         * @return The current Find instance for method chaining
         */
        public Find in(String field, List<?> values) {
            Map<String, Object> condition = new HashMap<>();
            condition.put("$in", values);
            criteria.put(field, condition);
            return this;
        }

        /**
         * Gets the built criteria map for the query.
         * 
         * @return A Map containing all the filter conditions
         */
        public Map<String, Object> getCriteria() {
            return criteria;
        }
    }

    /**
     * Filter builder for update operations.
     * Supports various update operators for modifying documents.
     */
    public static class Update extends Filter {
        private Map<String, Object> updateOperations = new HashMap<>();

        /**
         * Sets the value of a field in an update operation.
         * 
         * @param field The field name to update
         * @param value The new value to set
         * @return The current Update instance for method chaining
         */
        @SuppressWarnings("unchecked")
        public Update set(String field, Object value) {
            Map<String, Object> setOp = (Map<String, Object>) updateOperations.getOrDefault("$set", new HashMap<>());
            setOp.put(field, value);
            updateOperations.put("$set", setOp);
            return this;
        }

        /**
         * Increments a numeric field's value by the specified amount.
         * 
         * @param field The numeric field to increment
         * @param value The amount to increment by
         * @return The current Update instance for method chaining
         */
        @SuppressWarnings("unchecked")
        public Update inc(String field, Number value) {
            Map<String, Object> incOp = (Map<String, Object>) updateOperations.getOrDefault("$inc", new HashMap<>());
            incOp.put(field, value);
            updateOperations.put("$inc", incOp);
            return this;
        }

        /**
         * Appends a value to an array field.
         * 
         * @param field The array field to modify
         * @param value The value to append to the array
         * @return The current Update instance for method chaining
         */
        @SuppressWarnings("unchecked")
        public Update push(String field, Object value) {
            Map<String, Object> pushOp = (Map<String, Object>) updateOperations.getOrDefault("$push", new HashMap<>());
            pushOp.put(field, value);
            updateOperations.put("$push", pushOp);
            return this;
        }

        /**
         * Gets the criteria map for selecting documents to update.
         * 
         * @return A Map containing the selection criteria
         */
        public Map<String, Object> getCriteria() {
            return criteria;
        }

        /**
         * Gets the update operations map.
         * 
         * @return A Map containing all the update operations
         */
        public Map<String, Object> getUpdateOperations() {
            return updateOperations;
        }
    }
}