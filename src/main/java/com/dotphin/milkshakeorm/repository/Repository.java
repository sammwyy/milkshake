package com.dotphin.milkshakeorm.repository;

import java.lang.reflect.Array;
import java.util.Map;

import com.dotphin.milkshakeorm.errors.NotIDAnnotationException;
import com.dotphin.milkshakeorm.providers.IProvider;
import com.dotphin.milkshakeorm.utils.EntityUtils;

@SuppressWarnings("unchecked")
public class Repository<S> {
    private final String collection;
    private final Class<?> entity;
    private final IProvider provider;

    public Repository(final Class<?> entity, final IProvider provider, final String collection) {
        this.collection = collection;
        this.entity = entity;
        this.provider = provider;
    }

    public Repository(final Class<?> entity, final IProvider provider) {
        this(entity, provider, entity.getName());
    }

    /* Shorthand operations */
    public String save(final Object obj) throws NotIDAnnotationException {
        final Map<String, Object> props = EntityUtils.mapEntityToProps(obj);
        String id = EntityUtils.getEntityID(obj);

        if (id == null) {
            id = this.provider.create(this.collection, props);
        } else {
            this.provider.updateByID(this.collection, id, props);
        }

        EntityUtils.setEntityID(obj, id);
        return id;
    }

    public boolean delete(final Object obj) throws NotIDAnnotationException {
        final String id = EntityUtils.getEntityID(obj);
        if (id != null) {
            return this.provider.deleteByID(this.collection, id);
        }
        return false;
    }

    public boolean refresh(final Object obj) throws NotIDAnnotationException {
        final String id = EntityUtils.getEntityID(obj);
        final Map<String, Object> props = this.provider.findByID(this.collection, id);
        if (props != null) {
            try {
                EntityUtils.injectPropsToEntity(obj, props);
            } catch (final Exception e) {
                e.printStackTrace();
                return false;
            }
            return true;
        }

        return false;
    }

    /* Read operations */
    public S findByID(final String id) {
        Object obj = EntityUtils.mapPropsToEntity(entity, this.provider.findByID(this.collection, id));
        return (S) obj;
    }

    public S findOne(final Map<String, Object> filter) {
        Object obj = EntityUtils.mapPropsToEntity(entity, this.provider.findOne(this.collection, filter));
        return (S) obj;
    }

    public S[] findMany(final Map<String, Object> filter, final FindOption options) {
        Object[] objs = EntityUtils.mapPropsToEntity(entity, this.provider.findMany(this.collection, filter, options));
        S[] list = (S[]) Array.newInstance(entity, objs.length);

        for (int i = 0; i < objs.length; i++) {
            list[i] = (S) objs[i];
        }

        return list;
    }

    public S[] findMany(final Map<String, Object> filter) {
        return this.findMany(filter, new FindOption());
    }

    /* Update operations */
    public long updateMany(final Map<String, Object> filter, final Map<String, Object> update) {
        return this.provider.updateMany(this.collection, filter, update);
    }

    public boolean updateOne(final Map<String, Object> filter, final Map<String, Object> update) {
        return this.provider.updateOne(this.collection, filter, update);
    }

    public boolean updateByID(final String id, final Map<String, Object> update) {
        return this.provider.updateByID(this.collection, id, update);
    }

    /* Delete operations */
    public boolean deleteByID(final String id) {
        return this.provider.deleteByID(this.collection, id);
    }

    public boolean deleteOne(final String id) {
        return this.provider.deleteByID(this.collection, id);
    }

    public long deleteMany(final Map<String, Object> filter) {
        return this.provider.deleteMany(this.collection, filter);
    }
}
