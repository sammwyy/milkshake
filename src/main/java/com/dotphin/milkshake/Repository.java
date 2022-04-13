package com.dotphin.milkshake;

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
}
