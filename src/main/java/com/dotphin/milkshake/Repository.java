package com.dotphin.milkshake;

import java.util.Map;

import org.bson.Document;

public class Repository<S> {
    private String collection;
    private Provider provider;

    protected Repository(Provider provider, String collection) {
        this.collection = collection;
        this.provider = provider;
    }

    public String save(Entity entity) {
        String id = entity.getID();
        Document props = entity.getPropsAsDocument();

        if (id == null) {
            id = this.provider.create(this.collection, props);
            entity.setID(id);
        } else {
            this.provider.updateByID(this.collection, id, props);
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
            Map<String, Object> props = this.provider.findByID(this.collection, id);
            
            if (props != null) {
                entity.injectProps(props);
                return true;
            }
        }

        return false;
    }
}
