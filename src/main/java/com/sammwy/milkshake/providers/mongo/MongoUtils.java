package com.sammwy.milkshake.providers.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.sammwy.milkshake.query.Filter.Find;
import com.sammwy.milkshake.query.Filter.Update;

/**
 * Utility class for converting Milkshake query filters to MongoDB BSON
 * operations.
 * Provides translation between the ORM's query abstraction and native MongoDB
 * operations.
 */
public class MongoUtils {

    /**
     * Converts a Milkshake Find filter to a MongoDB BSON filter.
     * Supports standard comparison operators: $eq, $ne, $gt, $lt, $in.
     *
     * @param filter The Find filter to convert
     * @return A Bson filter representing the query conditions
     * @throws UnsupportedOperationException if an unknown operator is encountered
     *
     * @see Filters
     * @see Find
     */
    public static Bson toBson(Find filter) {
        List<Bson> bsonList = new ArrayList<>();

        for (Map.Entry<String, Object> entry : filter.getCriteria().entrySet()) {
            String field = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> condition = (Map<String, Object>) value;

                for (Map.Entry<String, Object> cond : condition.entrySet()) {
                    String op = cond.getKey();
                    Object val = cond.getValue();

                    switch (op) {
                        case "$ne":
                            bsonList.add(Filters.ne(field, val));
                            break;
                        case "$gt":
                            bsonList.add(Filters.gt(field, val));
                            break;
                        case "$lt":
                            bsonList.add(Filters.lt(field, val));
                            break;
                        case "$in":
                            bsonList.add(Filters.in(field, (List<?>) val));
                            break;
                        default:
                            throw new UnsupportedOperationException("Unknown operator: " + op);
                    }
                }
            } else {
                bsonList.add(Filters.eq(field, value));
            }
        }

        return bsonList.isEmpty() ? Filters.empty() : Filters.and(bsonList);
    }

    /**
     * Converts a Milkshake Update filter to MongoDB BSON update operations.
     * Supports standard update operators: $set, $inc, $push.
     *
     * @param update The Update filter to convert
     * @return A Bson update operation, or null if no operations were specified
     * @throws UnsupportedOperationException if an unknown operator is encountered
     * @throws ClassCastException            if $inc value is not a Number
     *
     * @see Updates
     * @see Update
     */
    public static Bson toBson(Update update) {
        List<Bson> bsonUpdates = new ArrayList<>();

        for (Map.Entry<String, Object> operation : update.getUpdateOperations().entrySet()) {
            String op = operation.getKey();
            @SuppressWarnings("unchecked")
            Map<String, Object> fields = (Map<String, Object>) operation.getValue();

            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                String field = entry.getKey();
                Object value = entry.getValue();

                switch (op) {
                    case "$set":
                        bsonUpdates.add(Updates.set(field, value));
                        break;
                    case "$inc":
                        bsonUpdates.add(Updates.inc(field, (Number) value));
                        break;
                    case "$push":
                        bsonUpdates.add(Updates.push(field, value));
                        break;
                    default:
                        throw new UnsupportedOperationException("Unknown update operator: " + op);
                }
            }
        }

        return bsonUpdates.isEmpty() ? null : Updates.combine(bsonUpdates);
    }
}