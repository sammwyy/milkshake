package com.dotphin.milkshakeorm.repository;

import java.util.ArrayList;
import java.util.List;

public class FindOption {
    private List<SortKey> sorting = new ArrayList<>();
    private int limit = 0;
    private int skip = 0;

    public FindOption sort(final String key, final SortOrder order) {
        this.sorting.add(new SortKey(key, order));
        return this;
    }

    public FindOption sort(final String key) {
        return this.sort(key, SortOrder.ASCENDANT);
    }

    public FindOption limit(final int limit) {
        this.limit = limit;
        return this;
    }

    public FindOption skip(final int skip) {
        this.skip = skip;
        return this;
    }

    public List<SortKey> getSorting() {
        return this.sorting;
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
