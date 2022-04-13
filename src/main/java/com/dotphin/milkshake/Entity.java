package com.dotphin.milkshake;

import java.util.Map;

import com.dotphin.classserializer.ClassSerializer;

import org.bson.Document;

public class Entity {
    private Repository<?> repository;
    private String id;

    public Repository<?> getRepository() {
        if (this.repository == null) {
            this.repository = Milkshake.getRepository(this.getClass());
        }
        return this.repository;
    }

    public void delete() {
        this.getRepository().delete(this);
    }

    public String getID() {
        return this.id;
    }

    public Map<String, Object> getPropsAsMap() {
        return ClassSerializer.getDefaultSerializer().serialize(this);
    }

    public Document getPropsAsDocument() {
        return new Document(this.getPropsAsMap());
    }

    public Document getPropsAsUpdate() {
        return new Document("$set", this.getPropsAsDocument());
    }

    public void injectProps(Map<String, Object> props) {
        ClassSerializer.getDefaultSerializer().deserialize(this, props);
    }

    public void refresh() {
        this.getRepository().refresh(this);
    }

    public void save() {
        this.getRepository().save(this);
    }

    public void setID(String id) {
        this.id = id;
    }
}
