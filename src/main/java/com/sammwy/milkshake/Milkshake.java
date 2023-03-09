package com.sammwy.milkshake;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class Milkshake {
    protected static final Map<String, Provider> providers = new HashMap<>();
    private static final Map<Class<?>, Repository<?>> repositories = new HashMap<>();
    private static Provider lastRegisteredProvider = null;

    public static <S> Repository<S> addRepository(Class<?> entity, Provider provider, String collection) {
        Repository<?> repository = new Repository<>(entity, provider, collection);
        repositories.put(entity, repository);
        return (Repository<S>) repository;
    }

    public static <S> Repository<S> addRepository(Class<?> entity, Provider provider) {
        return addRepository(entity, provider, entity.getSimpleName());
    }

    public static <S> Repository<S> addRepository(Class<?> entity) {
        return addRepository(entity, Milkshake.getProvider(), entity.getSimpleName());
    }

    public static Provider getProvider() {
        return lastRegisteredProvider;
    }

    public static Provider getProvider(String uri) {
        return providers.get(uri);
    }

    public static Provider connect(String uri) {
        Provider provider = getProvider(uri);

        if (provider == null) {
            provider = new Provider(uri);
            providers.put(uri, provider);
        }

        provider.addConnection();
        lastRegisteredProvider = provider;
        return provider;
    }

    public static <S> Repository<S> getRepository(Class<?> entity) {
        return (Repository<S>) repositories.get(entity);
    }
}
