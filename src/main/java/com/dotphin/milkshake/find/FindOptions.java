package com.dotphin.milkshake.find;

import java.util.ArrayList;
import java.util.List;

import com.dotphin.milkshake.sorting.SortKey;
import com.dotphin.milkshake.sorting.SortOrder;

public class FindOptions {
    private int _limit = -1;
    private int _skip = -1;
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
}
