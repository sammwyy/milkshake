package com.dotphin.milkshake;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class Milkshake {
    protected static final Map<String, Provider> providers = new HashMap<>();
    private static final Map<Class<?>, Repository<?>> repositories = new HashMap<>();

    public static <S> Repository<S> addRepository(Class<?> entity, Provider provider, String collection) {
        Repository<?> repository = new Repository<>(provider, collection);
        repositories.put(entity, repository);
        return (Repository<S>) repository;
    }

    public static <S> Repository<S> addRepository(Class<?> entity, Provider provider) {
        return addRepository(entity, provider, entity.getSimpleName());
    }
    
    public static Provider connect(String uri) {
        Provider provider = providers.get(uri);
        if (provider == null) {
            provider = new Provider(uri);
            providers.put(uri, provider);
        }

        provider.addConnection();
        return provider;
    }

    public static <S> Repository<S> getRepository(Class<?> entity) {
        return (Repository<S>) repositories.get(entity);
    }
}
