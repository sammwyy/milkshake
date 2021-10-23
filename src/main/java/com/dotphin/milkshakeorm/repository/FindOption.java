package com.dotphin.milkshakeorm.repository;

public class FindOption {

    private String sortKey = null;
    private int sortOrder = 0;

    public FindOption sort(final String key, final int order) {
        this.sortKey = key;
        this.sortOrder = order;
        return this;
    }

    public FindOption sort(final String key) {
        return this.sort(key, 1);
    }

    public String getSortKey() {
        return this.sortKey;
    }

    public int getSortOrder() {
        return this.sortOrder;
    }

    public static FindOption create() {
        return new FindOption();
    }
}
