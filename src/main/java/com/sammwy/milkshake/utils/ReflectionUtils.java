package com.sammwy.milkshake.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sammwy.milkshake.annotations.ID;
import com.sammwy.milkshake.annotations.Prop;
import com.sammwy.milkshake.annotations.SchemaType;
import com.sammwy.milkshake.schema.Schema;

/**
 * Utility class for working with reflection and annotations in the Milkshake
 * ORM.
 * Provides methods for schema inspection, document conversion, and field
 * manipulation.
 */
public class ReflectionUtils {

    /**
     * Gets the collection name for a schema class.
     * Uses the @SchemaType annotation value if present, otherwise defaults to the
     * class simple name.
     *
     * @param schemaClass The schema class to inspect
     * @return The collection name to use for this schema
     */
    public static String getCollectionName(Class<? extends Schema> schemaClass) {
        if (schemaClass.isAnnotationPresent(SchemaType.class)) {
            SchemaType schemaType = schemaClass.getAnnotation(SchemaType.class);
            String value = schemaType.value();
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        return schemaClass.getSimpleName();
    }

    /**
     * Gets all fields annotated with @Prop in a schema class.
     *
     * @param schemaClass The schema class to inspect
     * @return List of fields marked with @Prop annotation
     */
    public static List<Field> getPropFields(Class<? extends Schema> schemaClass) {
        List<Field> propFields = new ArrayList<>();

        for (Field field : schemaClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Prop.class)) {
                field.setAccessible(true);
                propFields.add(field);
            }
        }

        return propFields;
    }

    /**
     * Gets the ID field of a schema class.
     * First looks for @ID annotated field, then falls back to field named "id",
     * and finally checks superclass for "id" field if not found.
     *
     * @param schemaClass The schema class to inspect
     * @return The ID field, or null if no suitable field is found
     */
    public static Field getIdField(Class<? extends Schema> schemaClass) {
        // Look for @ID annotated field
        for (Field field : schemaClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ID.class)) {
                field.setAccessible(true);
                return field;
            }
        }

        // Look for field named "id"
        try {
            Field idField = schemaClass.getDeclaredField("id");
            idField.setAccessible(true);
            return idField;
        } catch (NoSuchFieldException e) {
            // Check superclass
            if (schemaClass.getSuperclass() != null && Schema.class.isAssignableFrom(schemaClass.getSuperclass())) {
                try {
                    Field idField = schemaClass.getSuperclass().getDeclaredField("id");
                    idField.setAccessible(true);
                    return idField;
                } catch (NoSuchFieldException ex) {
                    // Ignore and return null
                }
            }
        }

        return null;
    }

    /**
     * Converts a Schema object to a document (Map) representation.
     * Includes the ID and all @Prop annotated fields with their values.
     *
     * @param schema The schema instance to convert
     * @return Map representing the document
     * @throws RuntimeException if field access fails or required field is null with
     *                          no default
     */
    public static Map<String, Object> schemaToDocument(Schema schema) {
        Map<String, Object> document = new HashMap<>();
        Class<? extends Schema> schemaClass = schema.getClass();

        // Add ID
        document.put("_id", schema.getId());

        // Add @Prop annotated fields
        for (Field field : getPropFields(schemaClass)) {
            try {
                Prop prop = field.getAnnotation(Prop.class);
                String fieldName = prop.name().isEmpty() ? field.getName() : prop.name();
                Object value = field.get(schema);

                // Handle null values for required fields
                if (value == null && prop.required()) {
                    if (!prop.defaultValue().isEmpty()) {
                        value = convertToType(prop.defaultValue(), field.getType());
                    } else {
                        throw new RuntimeException(
                                "Field " + fieldName + " is required but no value or default is provided");
                    }
                }

                document.put(fieldName, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error accessing field " + field.getName(), e);
            }
        }

        return document;
    }

    /**
     * Converts a document (Map) to a Schema object.
     * Populates the ID and all @Prop annotated fields from the document.
     *
     * @param schemaClass The schema class to instantiate
     * @param document    The document data to populate from
     * @return A populated schema instance
     * @throws RuntimeException if instantiation fails or field access fails
     */
    public static <T extends Schema> T documentToSchema(Class<T> schemaClass, Map<String, Object> document) {
        try {
            T schema = schemaClass.getDeclaredConstructor().newInstance();

            // Set ID
            if (document.containsKey("_id")) {
                schema.setId((String) document.get("_id"));
            }

            // Set @Prop annotated fields
            for (Field field : getPropFields(schemaClass)) {
                Prop prop = field.getAnnotation(Prop.class);
                String fieldName = prop.name().isEmpty() ? field.getName() : prop.name();

                if (document.containsKey(fieldName)) {
                    Object value = document.get(fieldName);
                    // Convert type if necessary
                    if (value != null && !field.getType().isAssignableFrom(value.getClass())) {
                        value = convertToType(value.toString(), field.getType());
                    }
                    field.set(schema, value);
                } else if (prop.required() && !prop.defaultValue().isEmpty()) {
                    // Use default value
                    field.set(schema, convertToType(prop.defaultValue(), field.getType()));
                }
            }

            return schema;
        } catch (Exception e) {
            throw new RuntimeException("Error creating instance of " + schemaClass.getName(), e);
        }
    }

    /**
     * Converts a string value to the specified type.
     * Supports common primitive types and their wrapper classes.
     *
     * @param value The string value to convert
     * @param type  The target type class
     * @return The converted value
     * @throws RuntimeException if the type is not supported
     */
    @SuppressWarnings("unchecked")
    private static <T> T convertToType(String value, Class<T> type) {
        if (type.equals(String.class)) {
            return (T) value;
        } else if (type.equals(Integer.class) || type.equals(int.class)) {
            return (T) Integer.valueOf(value);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return (T) Long.valueOf(value);
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return (T) Double.valueOf(value);
        } else if (type.equals(Float.class) || type.equals(float.class)) {
            return (T) Float.valueOf(value);
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return (T) Boolean.valueOf(value);
        }
        throw new RuntimeException("Cannot convert to type " + type.getName());
    }
}