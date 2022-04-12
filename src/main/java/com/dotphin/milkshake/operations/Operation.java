package com.dotphin.milkshake.operations;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.model.Updates;

import org.bson.conversions.Bson;

public class Operation {
    private List<Bson> updates;
    
    public Operation() {
        this.updates = new ArrayList<>();
    }

    public Operation add(String key, Object value, boolean ignoreIfExist) {
        this.updates.add(Updates.push(key, value));
        return this;
    }

    public Operation addIfNotPresent(String key, Object value) {
        this.updates.add(Updates.addToSet(key, value));
        return this;
    }

    public Operation currentDate(String key) {
        this.updates.add(Updates.currentDate(key));
        return this;
    }

    public Operation currentTimestamp(String key) {
        this.updates.add(Updates.currentTimestamp(key));
        return this;
    }

    public Operation increment(String key, Number value) {
        this.updates.add(Updates.inc(key, value));
        return this;
    }

    public Operation max(String key, Number value) {
        this.updates.add(Updates.max(key, value));
        return this;
    }

    public Operation min(String key, Number value) {
        this.updates.add(Updates.min(key, value));
        return this;
    }

    public Operation multiply(String key, Number value) {
        this.updates.add(Updates.mul(key, value));
        return this;
    }

    public Operation remove(String key, Object value) {
        this.updates.add(Updates.pull(key, value));
        return this;
    }

    public Operation removeFirst(String key) {
        this.updates.add(Updates.popFirst(key));
        return this;
    }

    public Operation removeLast(String key) {
        this.updates.add(Updates.popLast(key));
        return this;
    }

    public Operation rename(String oldKey, String newKey) {
        this.updates.add(Updates.rename(oldKey, newKey));
        return this;
    }

    public Operation set(String key, Object value) {
        this.updates.add(Updates.set(key, value));
        return this;
    }

    public Operation unset(String key) {
        this.updates.add(Updates.unset(key));
        return this;
    }

    public Bson build() {
        return Updates.combine(this.updates);
    }
}
