package com.dotphin.milkshakeorm;

import java.util.HashMap;
import java.util.Map;

import com.dotphin.milkshakeorm.providers.Provider;
import com.dotphin.milkshakeorm.providers.impl.MongoProvider;
import com.dotphin.milkshakeorm.repository.Repository;
import com.dotphin.milkshakeorm.utils.URI;

@SuppressWarnings("unchecked")
public class MilkshakeORM {
    private static final Map<Class<?>, Repository<?>> repositories = new HashMap<>();
    private static final Map<URI, Provider> cachedProviders = new HashMap<>();

    public static Provider connect(final URI uri) {
        final String protocol = uri.getProtocol().toLowerCase();

        Provider provider = cachedProviders.get(uri);
        if (provider != null) {
            return provider;
        }

        if (protocol.equalsIgnoreCase("mongodb")) {
            provider = new MongoProvider().connect(uri);
        } else {
            throw new Error("Unknown database protocol " + protocol);
        }

        cachedProviders.put(uri, provider);
        return provider;
    }

    public static Provider connect(final String connectionURI) {
        return connect(new URI(connectionURI));
    }

    public static <S> Repository<S> addRepository(Class<?> entity, Provider provider, String collection) {
        Repository<?> repository = new Repository<>(entity, provider, collection);
        repositories.put(entity, repository);
        return (Repository<S>) repository;
    }

    public static <S> Repository<S> addRepository(Class<?> entity, Provider provider) {
        Repository<?> repository = new Repository<>(entity, provider);
        repositories.put(entity, repository);
        return (Repository<S>) repository;
    }

    public static <S> Repository<S> getRepository(Class<?> entity) {
        return (Repository<S>) repositories.get(entity);
    }
}
