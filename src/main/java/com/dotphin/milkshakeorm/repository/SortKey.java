package com.dotphin.milkshakeorm.repository;

public class SortKey {
    private String key;
    private SortOrder type;
    
    public SortKey(String key, SortOrder type) {
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return this.key;
    }

    public SortOrder getType() {
        return this.type;
    }
}
