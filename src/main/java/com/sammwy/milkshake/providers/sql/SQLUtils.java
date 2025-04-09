package com.sammwy.milkshake.providers.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sammwy.milkshake.query.Filter;

/**
 * Utility class for converting Milkshake query filters to SQL query
 * components.
 * Provides translation between the ORM's query abstraction and native SQL
 * operations.
 */
public class SQLUtils {

    /**
     * Builds a WHERE clause from a Find filter.
     * 
     * @param filter The Find filter to convert
     * @return A SQLCriteriaResult containing the WHERE clause string and
     *         parameters
     */
    public static SQLCriteriaResult buildWhereCriteria(Filter.Find filter) {
        if (filter == null || filter.getCriteria().isEmpty()) {
            return new SQLCriteriaResult("", new ArrayList<>());
        }

        StringBuilder whereClause = new StringBuilder();
        List<Object> parameters = new ArrayList<>();
        boolean first = true;

        for (Map.Entry<String, Object> entry : filter.getCriteria().entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();

            if (!first) {
                whereClause.append(" AND ");
            }

            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> condition = (Map<String, Object>) value;

                for (Map.Entry<String, Object> cond : condition.entrySet()) {
                    String op = cond.getKey();
                    Object val = cond.getValue();

                    switch (op) {
                        case "$ne":
                            whereClause.append("`").append(field).append("` != ?");
                            parameters.add(val);
                            break;
                        case "$gt":
                            whereClause.append("`").append(field).append("` > ?");
                            parameters.add(val);
                            break;
                        case "$lt":
                            whereClause.append("`").append(field).append("` < ?");
                            parameters.add(val);
                            break;
                        case "$in":
                            List<?> inList = (List<?>) val;
                            if (inList.isEmpty()) {
                                whereClause.append("0"); // Always false
                            } else {
                                whereClause.append("`").append(field).append("` IN (");
                                for (int i = 0; i < inList.size(); i++) {
                                    if (i > 0) {
                                        whereClause.append(", ");
                                    }
                                    whereClause.append("?");
                                    parameters.add(inList.get(i));
                                }
                                whereClause.append(")");
                            }
                            break;
                        default:
                            throw new UnsupportedOperationException("Unknown operator: " + op);
                    }
                }
            } else {
                whereClause.append("`").append(field).append("` = ?");
                parameters.add(value);
            }

            first = false;
        }

        return new SQLCriteriaResult(whereClause.toString(), parameters);
    }

    /**
     * Builds a SET clause from an Update filter.
     * 
     * @param update The Update filter to convert
     * @return A SQLUpdateResult containing the SET clause string and parameters
     */
    public static SQLUpdateResult buildUpdateStatement(Filter.Update update) {
        if (update == null || update.getUpdateOperations().isEmpty()) {
            return new SQLUpdateResult("", new ArrayList<>());
        }

        StringBuilder setClause = new StringBuilder();
        List<Object> parameters = new ArrayList<>();
        boolean first = true;

        Map<String, Object> updateOps = update.getUpdateOperations();

        // Handle $set operations
        if (updateOps.containsKey("$set")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> setOps = (Map<String, Object>) updateOps.get("$set");

            for (Map.Entry<String, Object> entry : setOps.entrySet()) {
                if (!first) {
                    setClause.append(", ");
                }

                setClause.append("`").append(entry.getKey()).append("` = ?");
                parameters.add(entry.getValue());

                first = false;
            }
        }

        // Handle $inc operations
        if (updateOps.containsKey("$inc")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> incOps = (Map<String, Object>) updateOps.get("$inc");

            for (Map.Entry<String, Object> entry : incOps.entrySet()) {
                if (!first) {
                    setClause.append(", ");
                }

                setClause.append("`").append(entry.getKey()).append("` = `")
                        .append(entry.getKey()).append("` + ?");
                parameters.add(entry.getValue());

                first = false;
            }
        }

        // Handle $push operations (for JSON arrays in SQL)
        if (updateOps.containsKey("$push")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> pushOps = (Map<String, Object>) updateOps.get("$push");

            for (Map.Entry<String, Object> entry : pushOps.entrySet()) {
                if (!first) {
                    setClause.append(", ");
                }

                // Using JSON_ARRAY_APPEND for array operations in SQL
                setClause.append("`").append(entry.getKey()).append("` = JSON_ARRAY_APPEND(`")
                        .append(entry.getKey()).append("`, '$', ?)");
                parameters.add(entry.getValue());

                first = false;
            }
        }

        return new SQLUpdateResult(setClause.toString(), parameters);
    }

    /**
     * Maps Java types to SQL column types
     * 
     * @param javaType The Java class type
     * @return The corresponding SQL column type
     */
    public static String getSQLType(Class<?> javaType, boolean isSqlite) {
        if (javaType == String.class) {
            return "TEXT";
        } else if (javaType == Integer.class || javaType == int.class
                || javaType == Long.class || javaType == long.class
                || javaType == Short.class || javaType == short.class
                || javaType == Byte.class || javaType == byte.class) {
            return "INTEGER";
        } else if (javaType == Double.class || javaType == double.class
                || javaType == Float.class || javaType == float.class) {
            return "REAL";
        } else if (javaType == Boolean.class || javaType == boolean.class) {
            return (isSqlite ? "INTEGER" : "BOOLEAN");
        } else if (javaType == byte[].class) {
            return "BLOB";
        } else {
            // For complex types, we'll store them as TEXT
            // You might want to implement serialization/deserialization for these
            return "TEXT";
        }
    }
}

/**
 * Container class for SQL WHERE clause components.
 */
class SQLCriteriaResult {
    private final String whereClause;
    private final List<Object> parameters;

    public SQLCriteriaResult(String whereClause, List<Object> parameters) {
        this.whereClause = whereClause;
        this.parameters = parameters;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public List<Object> getParameters() {
        return parameters;
    }
}

/**
 * Container class for SQL UPDATE statement components.
 */
class SQLUpdateResult {
    private final String setClause;
    private final List<Object> parameters;

    public SQLUpdateResult(String setClause, List<Object> parameters) {
        this.setClause = setClause;
        this.parameters = parameters;
    }

    public String getSetClause() {
        return setClause;
    }

    public List<Object> getParameters() {
        return parameters;
    }
}