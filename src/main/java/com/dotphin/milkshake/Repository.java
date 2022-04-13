package com.dotphin.milkshake;

import java.util.ArrayList;
import java.util.List;

import com.dotphin.classserializer.ClassSerializer;
import com.dotphin.milkshake.find.FindFilter;
import com.dotphin.milkshake.find.FindOptions;
import com.dotphin.milkshake.operations.Operation;

import org.bson.Document;

@SuppressWarnings("unchecked")
public class Repository<S> {
    private Class<?> clazz;
    private Provider provider;
    private String collection;

    private ClassSerializer serializer;

    protected Repository(Class<?> clazz, Provider provider, String collection) {
        this.clazz = clazz;
        this.provider = provider;
        this.collection = collection;
        this.serializer = ClassSerializer.getDefaultSerializer();
    }

    public String save(Entity entity) {
        String id = entity.getID();

        if (id == null) {
            id = this.provider.create(this.collection, entity.getPropsAsDocument());
            entity.setID(id);
        } else {
            this.provider.updateByID(this.collection, id, entity.getPropsAsUpdate());
        }

        return id;
    }

    public boolean delete(Entity entity) {
        String id = entity.getID();

        if (id != null) {
            boolean result = this.provider.deleteByID(this.collection, id);
            entity.setID(null);
            return result;
        } else {
            return false;
        }
    }

    public boolean refresh(Entity entity) {
        String id = entity.getID();

        if (id != null) {
            Document props = this.provider.findByID(this.collection, id);
            
            if (props != null) {
                entity.injectProps(props);
                return true;
            }
        }

        return false;
    }

    public S propsToEntity(Document doc) {
        if (doc != null) {
            S entity = (S) this.serializer.deserialize(this.clazz, doc);
            ((Entity) entity).setID(doc.getObjectId("_id").toHexString());
            return entity;
        } else {
            return null;
        }
    }

    public List<S> propsToEntities(List<Document> props) {
        List<S> result = new ArrayList<>();

        for (Document doc : props) {
            S entity = this.serializer.deserialize(this.clazz, doc);
            ((Entity) entity).setID(doc.getObjectId("_id").toHexString());
            result.add(entity);
        }

        return result;
    }

    public S findOne(FindFilter filter) {
        return this.propsToEntity(
            this.provider.findOne(collection, filter)
        );
    }

    public S findByID(String id) {
        return this.propsToEntity(
            this.provider.findByID(collection, id)
        );
    }

    public List<S> findMany(FindFilter filter, FindOptions options) {
        return this.propsToEntities(
            this.provider.findMany(collection, filter, options)
        );
    }

    public List<S> findMany(FindFilter filter) {
        return this.propsToEntities(
            this.provider.findMany(collection, filter)
        );
    }

    public boolean updateOne(FindFilter filter, Operation update) {
        return this.provider.updateOne(collection, filter, update);
    }

    public boolean updateByID(String id, Operation update) {
        return this.provider.updateByID(collection, id, update);
    }

    public long updateMany(FindFilter filter, Operation update) {
        return this.provider.updateMany(collection, filter, update);
    }

    public boolean deleteOne(FindFilter filter) {
        return this.provider.deleteOne(collection, filter);
    }

    public boolean deleteByID(String id) {
        return this.provider.deleteByID(collection, id);
    }

    public long deleteMany(FindFilter filter) {
        return this.provider.deleteMany(collection, filter);
    }
}
