package com.sammwy.milkshake;

import java.util.HashMap;
import java.util.Map;

/**
 * A caching mechanism for storing and retrieving Repository instances
 * associated with Schema classes.
 * This cache maintains a static mapping between Schema classes and their
 * corresponding Repositories,
 * ensuring efficient access and avoiding redundant repository instantiation.
 */
public class RepositoryCache {
    /**
     * The internal cache storage mapping Schema classes to their Repository
     * instances.
     * Uses a HashMap for O(1) average time complexity on get/put operations.
     */
    private static final Map<Class<? extends Schema>, Repository<? extends Schema>> CACHE = new HashMap<>();

    /**
     * Stores a Repository instance in the cache associated with its Schema class.
     * 
     * @param <T>         The specific Schema type
     * @param schemaClass The class object of the Schema implementation
     * @param repository  The Repository instance to cache for the given Schema
     *                    class
     * @throws IllegalArgumentException if either parameter is null
     */
    public static <T extends Schema> void cache(Class<T> schemaClass, Repository<T> repository) {
        CACHE.put(schemaClass, repository);
    }

    /**
     * Retrieves a cached Repository instance for the specified Schema class.
     * 
     * @param <T>         The specific Schema type
     * @param schemaClass The class object of the Schema implementation to look up
     * @return The cached Repository instance, or null if no repository is cached
     *         for the given class
     */
    @SuppressWarnings("unchecked")
    public static <T extends Schema> Repository<T> get(Class<T> schemaClass) {
        if (!CACHE.containsKey(schemaClass)) {
            return null;
        }

        Repository<T> repository = (Repository<T>) CACHE.get(schemaClass);
        return repository;
    }

    /**
     * Removes a cached Repository instance for the specified Schema class.
     * 
     * @param <T>         The specific Schema type
     * @param schemaClass The class object of the Schema implementation to remove
     */
    public static <T extends Schema> void remove(Class<T> schemaClass) {
        CACHE.remove(schemaClass);
    }

    /**
     * Remove all cached Repository instances from the cache.
     */
    public static void clear() {
        CACHE.clear();
    }
}