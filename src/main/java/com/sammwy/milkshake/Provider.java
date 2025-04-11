package com.sammwy.milkshake;

import java.util.List;
import java.util.Map;

import com.sammwy.classserializer.ClassSerializer;
import com.sammwy.milkshake.query.Filter;
import com.sammwy.milkshake.schema.Schema;

/**
 * Defines the core database operations for the Milkshake ORM.
 * This interface serves as an abstraction layer for different database
 * backends,
 * providing CRUD (Create, Read, Update, Delete) operations and repository
 * management.
 */
public interface Provider {
    /**
     * Return the ClassSerializer used by the provider
     * 
     * @return The ClassSerializer
     */
    public ClassSerializer getSerializer();

    /**
     * Return whether the provider supports embedded databases
     * 
     * @return true if the provider supports embedded databases
     */
    public boolean supportsEmbedded();

    /**
     * Establishes a connection to the database using the provided connection
     * information.
     * 
     * @param info The connection configuration containing credentials and server
     *             details
     */
    public void connect(ProviderInfo info);

    /**
     * Inserts a single document into the specified collection.
     * 
     * @param collection The name of the collection to insert into
     * @param data       The document data as key-value pairs
     * @return true if the insertion was successful, false otherwise
     */
    boolean insert(String collection, Map<String, Object> data);

    /**
     * Initialize the table for a Schema class (Used by SQL like providers)
     * 
     * @param schemaClass The Schema class
     * @param primaryKey  The primary key
     * @return true if initialization was successful
     */
    <T extends Schema> boolean initialize(Class<T> schemaClass, String primaryKey);

    /**
     * Inserts multiple documents into the specified collection in a single
     * operation.
     * 
     * @param collection The name of the collection to insert into
     * @param dataList   The list of documents to insert
     * @return The number of successfully inserted documents
     */
    int insertMany(String collection, List<Map<String, Object>> dataList);

    /**
     * Performs an upsert (insert or update if exists) operation for a single
     * document.
     * 
     * @param collection The name of the collection
     * @param data       The document data including identifier fields
     * @param primaryKey The name of the identifier field
     * @return true if the operation was successful, false otherwise
     */
    boolean upsert(String collection, Map<String, Object> data, String primaryKey);

    /**
     * Finds all documents matching the specified criteria in the collection.
     * 
     * @param collection The name of the collection to query
     * @param criteria   The search criteria as key-value pairs
     * @return A list of matching documents, or empty list if none found
     */
    List<Map<String, Object>> find(String collection, Filter.Find criteria);

    /**
     * Finds a single document matching the specified criteria in the collection.
     * 
     * @param collection The name of the collection to query
     * @param criteria   The search criteria as key-value pairs
     * @return The first matching document, or null if none found
     */
    Map<String, Object> findOne(String collection, Filter.Find criteria);

    /**
     * Finds a document by its unique identifier in the collection.
     * 
     * @param collection The name of the collection to query
     * @param id         The unique identifier of the document
     * @return The matching document, or null if not found
     */
    Map<String, Object> findById(String collection, String primaryKey, String id);

    /**
     * Updates all documents matching the specified criteria in the collection.
     * 
     * @param collection The name of the collection to update
     * @param criteria   The selection criteria as key-value pairs
     * @param update     The update operations as key-value pairs
     * @return The number of documents modified
     */
    int update(String collection, Filter.Find criteria, Filter.Update update);

    /**
     * Updates a single document by its unique identifier in the collection.
     * 
     * @param collection The name of the collection to update
     * @param id         The unique identifier of the document to update
     * @param update     The update operations as key-value pairs
     * @return true if the document was found and updated, false otherwise
     */
    boolean updateByID(String collection, String primaryKey, String id, Filter.Update update);

    /**
     * Updates the first document matching the specified criteria in the collection.
     * 
     * @param collection The name of the collection to update
     * @param criteria   The selection criteria as key-value pairs
     * @param update     The update operations as key-value pairs
     * @return true if a document was found and updated, false otherwise
     */
    boolean updateOne(String collection, Filter.Find criteria, Filter.Update update);

    /**
     * Deletes all documents matching the specified criteria from the collection.
     * 
     * @param collection The name of the collection to delete from
     * @param criteria   The selection criteria as key-value pairs
     * @return The number of documents deleted
     */
    int delete(String collection, Filter.Find criteria);

    /**
     * Deletes a single document by its unique identifier from the collection.
     * 
     * @param collection The name of the collection to delete from
     * @param id         The unique identifier of the document to delete
     * @return true if the document was found and deleted, false otherwise
     */
    boolean deleteByID(String collection, String primaryKey, String id);

    /**
     * Deletes the first document matching the specified criteria from the
     * collection.
     * 
     * @param collection The name of the collection to delete from
     * @param criteria   The selection criteria as key-value pairs
     * @return true if a document was found and deleted, false otherwise
     */
    boolean deleteOne(String collection, Filter.Find criteria);

    /**
     * Creates and registers a repository for the specified schema class.
     * 
     * @param <T>         The Schema type
     * @param schemaClass The class object representing the schema
     * @return A new Repository instance for the specified schema
     */
    <T extends Schema> Repository<T> addRepository(Class<T> schemaClass);
}