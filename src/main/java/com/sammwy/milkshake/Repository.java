package com.sammwy.milkshake;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sammwy.milkshake.annotations.SchemaType;
import com.sammwy.milkshake.query.Filter;
import com.sammwy.milkshake.schema.Schema;

/**
 * A generic repository implementation for performing CRUD operations on a
 * specific Schema type.
 * This class serves as the main interface between the application and the
 * database,
 * providing methods to create, find, update, and delete entities.
 *
 * @param <T> The Schema type this repository manages
 */
public class Repository<T extends Schema> {
    private Class<T> schemaClass;
    private Provider provider;
    private String primaryKey;

    /**
     * Constructs a new Repository instance for the specified Schema class.
     *
     * @param provider    The database provider used to establish connections
     * @param schemaClass The Class object representing the Schema type this
     *                    repository manages
     * @throws IllegalArgumentException if either parameter is null
     */
    public Repository(Provider provider, Class<T> schemaClass) {
        this.provider = provider;
        this.schemaClass = schemaClass;

        Field idField = Schema.getIdFieldOf(schemaClass);
        if (idField != null) {
            primaryKey = Schema.getIdKeyNameOf(idField);
        }
    }

    /**
     * Gets the name of the unique identifier for the primary key.
     * 
     * @return The name of the ID field
     */
    public String getPrimaryKey() {
        return primaryKey;
    }

    /**
     * Get the collection or table name associated with this repository
     * 
     * @return The collection or table name
     */
    public String getCollectionName() {
        return getCollectionName(this.schemaClass);
    }

    /**
     * Inserts a single entity into the database.
     *
     * @param entity The entity to insert
     * @return true if the insertion was successful, false otherwise
     * @throws IllegalArgumentException if the entity is null
     */
    public boolean insert(T entity) {
        validateSchema(entity);
        return provider.insert(getCollectionName(), entity.toMap());
    }

    /**
     * Inserts multiple entities into the database in a single operation.
     *
     * @param entities The list of entities to insert
     * @return The number of successfully inserted entities
     * @throws IllegalArgumentException if the entities list is null
     */
    public int insertMany(List<T> entities) {
        List<Map<String, Object>> mapped = new ArrayList<>();
        for (T entity : entities) {
            try {
                validateSchema(entity);
                mapped.add(entity.toMap());
            } catch (IllegalArgumentException e) {
                // Ignore
            }
        }
        return provider.insertMany(getCollectionName(), mapped);
    }

    /**
     * Insert or update a single entity in the database.
     * 
     * @param entity The entity to insert or update
     * @return true if the operation was successful, false otherwise
     */
    public boolean upsert(T entity) {
        validateSchema(entity);
        return provider.upsert(getCollectionName(), entity.toMap(), this.getPrimaryKey());
    }

    /**
     * Finds all entities matching the specified filter criteria.
     *
     * @param filter The filter conditions to apply to the query
     * @return A List of matching entities, or an empty list if no matches found
     */
    public List<T> find(Filter.Find filter) {
        List<Map<String, Object>> results = provider.find(getCollectionName(), filter);
        List<T> converted = new ArrayList<>();
        for (Map<String, Object> result : results) {
            converted.add(Schema.fromMap(this.schemaClass, result));
        }
        return converted;
    }

    /**
     * Finds a single entity by its unique identifier.
     *
     * @param id The unique identifier of the entity to find
     * @return The matching entity, or null if not found
     */
    public T findById(String id) {
        Map<String, Object> result = provider.findById(getCollectionName(), primaryKey, id);
        return result != null ? Schema.fromMap(this.schemaClass, result) : null;
    }

    /**
     * Finds a single entity matching the specified filter criteria.
     * If multiple entities match, only the first one is returned.
     *
     * @param filter The filter conditions to apply to the query
     * @return The first matching entity, or null if no matches found
     */
    public T findOne(Filter.Find filter) {
        Map<String, Object> result = provider.findOne(getCollectionName(), filter);
        return result != null ? Schema.fromMap(this.schemaClass, result) : null;
    }

    /**
     * Updates all entities matching the specified filter criteria.
     *
     * @param filter The filter conditions to select which entities to update
     * @param update The update operations to apply to matching entities
     * @return The number of entities that were successfully updated
     */
    public int update(Filter.Find filter, Filter.Update update) {
        return provider.update(getCollectionName(), filter, update);
    }

    /**
     * Updates a single entity by its unique identifier.
     *
     * @param id     The unique identifier of the entity to update
     * @param update The update operations to apply to the entity
     * @return true if the entity was found and updated, false otherwise
     */
    public boolean updateByID(String id, Filter.Update update) {
        return provider.updateByID(getCollectionName(), primaryKey, id, update);
    }

    /**
     * Updates a single entity matching the specified filter criteria.
     * If multiple entities match, only the first one is updated.
     *
     * @param filter The filter conditions to select which entity to update
     * @param update The update operations to apply to the entity
     * @return true if a matching entity was found and updated, false otherwise
     */
    public boolean updateOne(Filter.Find filter, Filter.Update update) {
        return provider.updateOne(getCollectionName(), filter, update);
    }

    /**
     * Deletes all entities matching the specified filter criteria.
     *
     * @param filter The filter conditions to select which entities to delete
     * @return The number of entities that were successfully deleted
     */
    public int delete(Filter.Find filter) {
        return provider.delete(getCollectionName(), filter);
    }

    /**
     * Deletes a single entity by its unique identifier.
     *
     * @param id The unique identifier of the entity to delete
     * @return true if the entity was found and deleted, false otherwise
     */
    public boolean deleteByID(String id) {
        return provider.deleteByID(getCollectionName(), primaryKey, id);
    }

    /**
     * Deletes a single entity matching the specified filter criteria.
     * If multiple entities match, only the first one is deleted.
     *
     * @param filter The filter conditions to select which entity to delete
     * @return true if a matching entity was found and deleted, false otherwise
     */
    public boolean deleteOne(Filter.Find filter) {
        return provider.deleteOne(getCollectionName(), filter);
    }

    /**
     * Gets the database provider for this repository.
     *
     * @return The database provider
     */
    public Provider getProvider() {
        return provider;
    }

    private static boolean validateSchema(Schema entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity cannot be null");
        return true;
    }

    /**
     * Gets the collection name for a schema class.
     * Uses the @SchemaType annotation value if present, otherwise defaults to the
     * class simple name.
     *
     * @param schemaClass The schema class to inspect
     * @return The collection name to use for this schema
     */
    private static String getCollectionName(Class<? extends Schema> schemaClass) {
        if (schemaClass.isAnnotationPresent(SchemaType.class)) {
            SchemaType schemaType = schemaClass.getAnnotation(SchemaType.class);
            String value = schemaType.value();
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        return schemaClass.getSimpleName();
    }
}