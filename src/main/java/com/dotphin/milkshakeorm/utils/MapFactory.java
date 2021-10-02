package com.dotphin.milkshakeorm.utils;

import java.util.HashMap;

public class MapFactory extends HashMap<String, Object> {
    public boolean contains(final String key) {
        return this.containsKey(key);
    }

    public MapFactory delete(final String key) {
        if (this.contains(key)) {
            this.remove(key);
        }
        return this;
    }

    public MapFactory add(final String key, final Object value) {
        this.put(key, value);
        return this;
    }

    public MapFactory set(final String key, final Object value) {
        this.delete(key);
        return this.add(key, value);
    }

    public static MapFactory create(final String key, final Object value) {
        final MapFactory factory = new MapFactory();
        factory.add(key, value);
        return factory;
    }
}
