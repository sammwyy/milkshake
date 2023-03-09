package com.sammwy.milkshake.find;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.sammwy.milkshake.sorting.SortKey;
import com.sammwy.milkshake.sorting.SortOrder;

public class FindOptions {
    private int _limit = 0;
    private int _skip = 0;
    private List<SortKey> _sorting = new ArrayList<>();

    public FindOptions limit(int limit) {
        this._limit = limit;
        return this;
    }

    public FindOptions skip(int skip) {
        this._skip = skip;
        return this;
    }

    public FindOptions sort(SortKey sortKey) {
        this._sorting.add(sortKey);
        return this;
    }

    public FindOptions sort(String key, SortOrder order) {
        return this.sort(new SortKey(key, order));
    }

    public int getLimit() {
        return this._limit;
    }

    public int getSkip() {
        return this._skip;
    }

    public List<SortKey> getSorting() {
        return this._sorting;
    }

    public BasicDBObject getSortingAsDBO() {
        BasicDBObject sort = null;

        for (SortKey sortKey : this.getSorting()) {
            if (sort == null) {
                sort = new BasicDBObject(sortKey.getKey(), sortKey.getOrderValue());
            } else {
                sort.append(sortKey.getKey(), sortKey.getOrderValue());
            }
        }

        return sort;
    }

    public void apply(FindIterable<?> iterable) {
        BasicDBObject sorting = this.getSortingAsDBO();

        if (sorting != null) {
            iterable.sort(sorting);
        }

        if (this.getSkip() > 0) {
            iterable.skip(this.getSkip());
        }

        if (this.getLimit() > 0) {
            iterable.limit(this.getLimit());
        }
    }
}
