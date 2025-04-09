package com.sammwy.milkshake;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.sammwy.milkshake.utils.ReflectionUtils;

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
    private String id;
    private Map<String, Object> cachedFields = new HashMap<>();

    /**
     * Constructs a new Schema instance with a randomly generated UUID.
     */
    public Schema() {
        this.id = UUID.randomUUID().toString();
    }

    /**
     * Gets the unique identifier of this document.
     * 
     * @return The document ID string
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier of this document.
     * 
     * @param id The new ID to assign to this document
     */
    public void setId(String id) {
        this.id = id;
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
        return this.getDefaultRepository().deleteByID(this.id);
    }

    /**
     * Converts this schema to a Map representation suitable for database
     * operations.
     * 
     * @return A Map containing all persistent fields and their values
     * @see ReflectionUtils#schemaToDocument(Schema)
     */
    public Map<String, Object> toMap() {
        return ReflectionUtils.schemaToDocument(this);
    }

    /**
     * Creates a Schema instance from a Map representation.
     * 
     * @param <T>         The specific Schema type
     * @param schemaClass The class of the Schema to create
     * @param data        The document data as key-value pairs
     * @return A new Schema instance populated with the provided data
     * @see ReflectionUtils#documentToSchema(Class, Map)
     */
    public static <T extends Schema> T fromMap(Class<T> schemaClass, Map<String, Object> data) {
        return ReflectionUtils.documentToSchema(schemaClass, data);
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
}