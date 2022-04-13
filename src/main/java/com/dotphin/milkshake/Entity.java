package com.dotphin.milkshake;

import java.util.Map;

import com.dotphin.classserializer.ClassSerializer;

import org.bson.Document;

public class Entity {
    private String id;

    public String getID() {
        return this.id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public Map<String, Object> getPropsAsMap() {
        return ClassSerializer.getDefaultSerializer().serialize(this);
    }

    public Document getPropsAsDocument() {
        return new Document(this.getPropsAsMap());
    }

    public Repository<?> getRepository() {
        return Milkshake.getRepository(this.getClass());
    }

    public void injectProps(Map<String, Object> props) {
        ClassSerializer.getDefaultSerializer().deserialize(this, props);
    }

    public void delete() {
        this.getRepository().delete(this);
    }

    public void refresh() {
        this.getRepository().refresh(this);
    }

    public void save() {
        this.getRepository().save(this);
    }
}
