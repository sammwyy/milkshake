package com.dotphin.milkshakeorm;

import java.util.HashMap;
import java.util.Map;

import com.dotphin.milkshakeorm.providers.IProvider;
import com.dotphin.milkshakeorm.providers.MongoProvider;
import com.dotphin.milkshakeorm.repository.Repository;

public class MilkshakeORM {
    private static final Map<Class<?>, Repository<?>> repositories = new HashMap<>();
    private static final Map<DatabaseType, IProvider> providers = new HashMap<>();

    public static IProvider connect(final DatabaseType type, final String connectionURI) {
        IProvider provider = null;

        switch (type) {
            case MONGODB:
                provider = new MongoProvider().connect(connectionURI);
                break;
            default:
                throw new Error("Unknown database type.");
        }

        providers.put(type, provider);
        return provider;
    }

    public static Repository<?> addRepository(Class<?> entity, IProvider provider) {
        Repository<?> repository = new Repository<>(entity, provider);
        repositories.put(entity, repository);
        return repository;
    }

    public static Repository<?> addRepository(Class<?> entity, DatabaseType type) {
        Repository<?> repository = new Repository<>(entity, providers.get(type));
        repositories.put(entity, repository);
        return repository;
    }

    public static Repository<?> addRepository(Class<?> entity) {
        Repository<?> repository = new Repository<>(entity, (IProvider) providers.values().toArray()[0]);
        repositories.put(entity, repository);
        return repository;
    }

    @SuppressWarnings("unchecked")
    public static <S> Repository<S> getRepository(Class<?> entity) {
        return (Repository<S>) repositories.get(entity);
    }
}
