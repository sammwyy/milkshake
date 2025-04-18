package com.sammwy.milkshake.schema;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.sammwy.milkshake.Repository;
import com.sammwy.milkshake.RepositoryCache;
import com.sammwy.milkshake.annotations.ID;

/**
 * Abstract base class for all database schema definitions.
 * Provides core functionality for document persistence, caching, and
 * conversion.
 * 
 * <p>
 * Example usage:
 * 
 * <pre>
 * {@code
 * @SchemaType("users")
 * public class User extends Schema {
 *     // Schema implementation
 * }
 * }
 * </pre>
 * 
 * @see Repository
 * @see ReflectionUtils
 */
public abstract class Schema {
    private Map<String, Object> cachedFields = new HashMap<>();
    private Repository<? extends Schema> repository;
    private Field idField;

    /**
     * Constructs a new Schema instance with a randomly generated UUID.
     */
    public Schema() {
        Field idField = Schema.getIdFieldOf(this.getClass());
        if (idField != null) {
            this.idField = idField;
            this.setId(UUID.randomUUID().toString());
        }
    }

    /**
     * Gets the unique identifier of this document.
     * 
     * @return The document ID string
     */
    public String getId() {
        try {
            return this.idField != null ? (String) this.idField.get(this) : null;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sets the unique identifier of this document.
     * 
     * @param id The new ID to assign to this document
     */
    public void setId(String id) {
        try {
            this.idField.set(this, id);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores a field value in the temporary cache.
     * 
     * @param fieldName The name of the field to cache
     * @param value     The value to store in cache
     */
    public void setCachedField(String fieldName, Object value) {
        cachedFields.put(fieldName, value);
    }

    /**
     * Retrieves a field value from the temporary cache.
     * 
     * @param fieldName The name of the field to retrieve
     * @return The cached value, or null if not found
     */
    public Object getCachedField(String fieldName) {
        return cachedFields.get(fieldName);
    }

    /**
     * Saves this document to the database (inserts or updates if exists).
     * 
     * @return true if the operation was successful, false otherwise
     */
    public boolean save() {
        return this.getDefaultRepository().upsert(this);
    }

    /**
     * Deletes this document from the database.
     * 
     * @return true if the document was found and deleted, false otherwise
     */
    public boolean delete() {
        return this.getDefaultRepository().deleteByID(this.getId());
    }

    /**
     * Converts this schema to a Map representation suitable for database
     * operations.
     * 
     * @return A Map containing all persistent fields and their values
     */
    public Map<String, Object> toMap() {
        return this.getRepository().getProvider().getSerializer().serialize(this);
    }

    /**
     * Creates a Schema instance from a Map representation.
     * 
     * @param <T>         The specific Schema type
     * @param schemaClass The class of the Schema to create
     * @param data        The document data as key-value pairs
     * @return A new Schema instance populated with the provided data
     */
    public static <T extends Schema> T fromMap(Class<T> schemaClass, Map<String, Object> data) {
        return (T) RepositoryCache.get(schemaClass).getProvider().getSerializer()
                .deserialize(schemaClass, data);
    }

    /**
     * Gets the Repository instance associated with this Schema.
     * 
     * @return
     */
    public Repository<? extends Schema> getRepository() {
        if (this.repository == null) {
            return this.getDefaultRepository();
        }
        return this.repository;
    }

    /**
     * Gets the default Repository instance for this Schema type.
     * 
     * @return The Repository associated with this Schema's class
     * @throws IllegalStateException if no repository is found for this Schema type
     */
    @SuppressWarnings("unchecked")
    public Repository<Schema> getDefaultRepository() {
        return (Repository<Schema>) RepositoryCache.get(this.getClass());
    }

    /**
     * Gets the ID field of the schema class
     * 
     * @param schemaClass The schema class
     * @return The ID field
     */
    public static Field getIdFieldOf(Class<? extends Schema> schemaClass) {
        for (Field field : schemaClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ID.class)) {
                return field;
            }
        }
        return null;
    }

    /**
     * Gets the name of the ID field
     * 
     * @param field The ID field
     * @return The name of the ID field
     */
    public static String getIdKeyNameOf(Field field) {
        ID id = field.getAnnotation(ID.class);
        return id.name().isEmpty() ? field.getName() : id.name();
    }
}