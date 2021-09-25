package com.dotphin.milkshake;

import java.util.HashMap;
import java.util.Map;

public class DataQuery {

    private Map<String, Object> queries;
    private int _skip = 0;
    private int _limit = Integer.MAX_VALUE;

    public DataQuery() {
        this.queries = new HashMap<>();
    }

    public Map<String, Object> getQueries() {
        return this.queries;
    }

    public int getLimit() {
        return this._limit;
    }

    public int getSkip() {
        return this._skip;
    }

    public DataQuery is(final String key, final Object value) {
        this.queries.put(key, value);
        return this;
    }

    public DataQuery limit(final int value) {
        this._limit = value;
        return this;
    }

    public DataQuery skip(final int value) {
        this._skip = value;
        return this;
    }

    public DataQuery first() {
        this._limit = 1;
        return this;
    }

    public DataQuery atIndex(final int index) {
        this.skip(index - 1);
        this.first();
        return this;
    }
}
