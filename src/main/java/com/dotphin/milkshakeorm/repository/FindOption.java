package com.dotphin.milkshakeorm.repository;

public class FindOption {

    private String sortKey = null;
    private int sortOrder = 0;
    private int limit = 0;
    private int skip = 0;

    public FindOption sort(final String key, final int order) {
        this.sortKey = key;
        this.sortOrder = order;
        return this;
    }

    public FindOption sort(final String key) {
        return this.sort(key, 1);
    }

    public FindOption limit(final int limit) {
        this.limit = limit;
        return this;
    }

    public FindOption skip(final int skip) {
        this.skip = skip;
        return this;
    }

    public String getSortKey() {
        return this.sortKey;
    }

    public int getSortOrder() {
        return this.sortOrder;
    }

    public int getLimit() {
        return limit;
    }

    public int getSkip() {
        return skip;
    }

    public static FindOption create() {
        return new FindOption();
    }
}
