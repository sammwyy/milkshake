package com.sammwy.milkshake.sorting;

public class SortKey {
    private String key;
    private SortOrder order;

    public SortKey(String key, SortOrder order) {
        this.key = key;
        this.order = order;
    }

    public String getKey() {
        return this.key;
    }

    public SortOrder getOrder() {
        return this.order;
    }

    public int getOrderValue() {
        return this.getOrder().getValue();
    }
}
