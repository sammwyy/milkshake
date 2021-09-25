package com.dotphin.milkshake.repository;

import java.util.Map;

import com.dotphin.milkshake.DataQuery;
import com.dotphin.milkshake.providers.IProvider;
import com.dotphin.milkshake.utils.EntityUtils;

@SuppressWarnings("unchecked")
public class Repository<S> {
    private final Class<?> entity;
    private final IProvider provider;

    public Repository(final Class<?> entity, final IProvider provider) {
        this.entity = entity;
        this.provider = provider;
    }

    public S findByID(final String id) {
        Object obj = EntityUtils.mapPropsToEntity(this, entity, this.provider.findByID(this.entity.getName(), id));
        return (S) obj;
    }

    public S findOne(final DataQuery query) {
        Object obj = EntityUtils.mapPropsToEntity(this, entity, this.provider.findOne(this.entity.getName(), query));
        return (S) obj;
    }

    public S[] findMany(final DataQuery query) {
        Object[] obj = EntityUtils.mapPropsToEntity(this, entity, this.provider.findMany(this.entity.getName(), query));
        return (S[]) obj;
    }

    public S put(final Object obj) {
        final Map<String, Object> props = EntityUtils.mapEntityToProps(obj);
        final String id = this.provider.create(this.entity.getName(), props);
        return this.findByID(id);
    }
}
