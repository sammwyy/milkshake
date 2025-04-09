package com.sammwy.milkshake.schema;

import java.lang.reflect.Field;
import java.util.UUID;

import com.sammwy.milkshake.annotations.Embedded;
import com.sammwy.milkshake.annotations.ID;
import com.sammwy.milkshake.annotations.Prop;

/**
 * Represents a property of a Schema class that should be persisted to the
 * database.
 * Handles both regular properties (@Prop) and embedded objects (@Embedded).
 */
public class SchemaProp {
    private final Field field;
    private final String name;
    private final String storageName;
    private final Class<?> type;
    private final boolean embedded;
    private final boolean required;
    private final String defaultValue;
    private final String prefix;
    private final boolean isID;

    /**
     * Creates a new SchemaProp instance for the specified field.
     *
     * @param field        Field from the Schema class
     * @param parentPrefix Prefix to be applied from parent objects (for nested
     *                     embedded objects)
     */
    public SchemaProp(Field field, String parentPrefix) {
        this.field = field;
        this.name = field.getName();
        this.type = field.getType();
        this.embedded = field.isAnnotationPresent(Embedded.class);
        this.isID = field.isAnnotationPresent(ID.class);

        if (embedded) {
            Embedded embedded = field.getAnnotation(Embedded.class);
            this.prefix = parentPrefix + (embedded.prefix().isEmpty() ? (field.getName() + "__") : embedded.prefix());
            this.storageName = this.name;
            this.required = false;
            this.defaultValue = "";
        } else if (field.isAnnotationPresent(Prop.class)) {
            Prop prop = field.getAnnotation(Prop.class);
            this.prefix = parentPrefix;
            this.storageName = prop.name().isEmpty() ? this.name : prop.name();
            this.required = prop.required();
            this.defaultValue = prop.defaultValue();
        } else {
            this.prefix = parentPrefix;
            this.storageName = "_id";
            this.required = true;
            this.defaultValue = UUID.randomUUID().toString();
        }
    }

    /**
     * Returns true if this property is an ID field.
     * 
     * @return true if this is an ID field, false otherwise
     */
    public boolean isID() {
        return isID;
    }

    /**
     * Gets the Java field name.
     * 
     * @return The field name in the Java class
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the storage name used in the database.
     * 
     * @return The column or field name in the database
     */
    public String getStorageName() {
        return storageName;
    }

    /**
     * Gets the full column name including any prefixes.
     * 
     * @return The full column name with prefixes
     */
    public String getFullColumnName() {
        return prefix + storageName;
    }

    /**
     * Gets the Java type of this property.
     * 
     * @return The Class representing the property's type
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * Checks if this property represents an embedded object.
     * 
     * @return true if this is an embedded object, false otherwise
     */
    public boolean isEmbedded() {
        return embedded;
    }

    /**
     * Checks if this property is required.
     * 
     * @return true if this property is required, false otherwise
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Gets the default value for this property.
     * 
     * @return The default value as a string
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Gets the prefix used for embedded fields.
     * 
     * @return The prefix string
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Gets the value of this property from a Schema instance.
     * 
     * @param schema The Schema instance
     * @return The property value
     * @throws IllegalAccessException if field access fails
     */
    public Object getValue(Schema schema) throws IllegalAccessException {
        field.setAccessible(true);
        return field.get(schema);
    }

    /**
     * Sets the value of this property on a Schema instance.
     * 
     * @param schema The Schema instance
     * @param value  The value to set
     * @throws IllegalAccessException if field access fails
     */
    public void setValue(Schema schema, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        if (value instanceof String && !type.equals(String.class)) {
            // Convert string to appropriate type if needed
            value = convertToType((String) value, type);
        } else if (value != null && !type.isAssignableFrom(value.getClass())) {
            // Try to convert other primitive types
            if (type.equals(Integer.class) || type.equals(int.class)) {
                if (value instanceof Number) {
                    value = ((Number) value).intValue();
                }
            } else if (type.equals(Long.class) || type.equals(long.class)) {
                if (value instanceof Number) {
                    value = ((Number) value).longValue();
                }
            } else if (type.equals(Double.class) || type.equals(double.class)) {
                if (value instanceof Number) {
                    value = ((Number) value).doubleValue();
                }
            } else if (type.equals(Float.class) || type.equals(float.class)) {
                if (value instanceof Number) {
                    value = ((Number) value).floatValue();
                }
            } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                if (value instanceof Number) {
                    value = ((Number) value).intValue() != 0;
                }
            }
        }

        field.set(schema, value);
    }

    /**
     * Converts a string value to the specified type.
     * 
     * @param value      The string value
     * @param targetType The target type
     * @return The converted value
     */
    @SuppressWarnings("unchecked")
    private <T> T convertToType(String value, Class<T> targetType) {
        if (targetType.equals(String.class)) {
            return (T) value;
        } else if (targetType.equals(Integer.class) || targetType.equals(int.class)) {
            return (T) Integer.valueOf(value);
        } else if (targetType.equals(Long.class) || targetType.equals(long.class)) {
            return (T) Long.valueOf(value);
        } else if (targetType.equals(Double.class) || targetType.equals(double.class)) {
            return (T) Double.valueOf(value);
        } else if (targetType.equals(Float.class) || targetType.equals(float.class)) {
            return (T) Float.valueOf(value);
        } else if (targetType.equals(Boolean.class) || targetType.equals(boolean.class)) {
            return (T) Boolean.valueOf(value);
        }
        throw new RuntimeException("Cannot convert to type " + targetType.getName());
    }
}