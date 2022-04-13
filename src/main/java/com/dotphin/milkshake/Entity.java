package com.dotphin.milkshake;

import java.util.Map;

import com.dotphin.classserializer.ClassSerializer;

import org.bson.Document;

public class Entity {
    private String id;

    public String getID() {
        return this.id;
    }

    public Map<String, Object> getPropsAsMap() {
        return ClassSerializer.getDefaultSerializer().serialize(this);
    }

    public Document getPropsAsDocument() {
        return new Document(this.getPropsAsMap());
    }

    public void setID(String id) {
        this.id = id;
    }
}
