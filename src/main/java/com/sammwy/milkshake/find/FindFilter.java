package com.sammwy.milkshake.find;

import com.mongodb.client.model.Filters;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

enum FindContext {
    AND, OR, NOR
}

public class FindFilter {
    private Bson filter = null;
    private Bson currentFilter = new BsonDocument();
    private FindContext context = FindContext.AND;
    private boolean is_not = false;

    public FindFilter() {
    }

    public FindFilter(String key, Object value) {
        this.isEquals(key, value);
    }

    private FindFilter calcContext() {
        if (is_not) {
            is_not = false;
            currentFilter = Filters.not(currentFilter);
        }

        if (filter == null) {
            filter = currentFilter;
        } else if (context == FindContext.AND) {
            filter = Filters.and(filter, currentFilter);
        } else if (context == FindContext.OR) {
            filter = Filters.or(filter, currentFilter);
        } else if (context == FindContext.NOR) {
            filter = Filters.nor(filter, currentFilter);
        }

        currentFilter = null;
        return this;
    }

    public Bson build() {
        if (this.filter == null) {
            this.filter = new Document();
        }

        return this.filter;
    }

    public FindFilter and() {
        this.context = FindContext.AND;
        return this;
    }

    public FindFilter or() {
        this.context = FindContext.OR;
        return this;
    }

    public FindFilter not(String key, Object value) {
        this.is_not = true;
        return this;
    }

    public FindFilter isEquals(String key, Object value) {
        this.currentFilter = Filters.eq(key, value);
        return this.calcContext();
    }

    public FindFilter isNotEquals(String key, Object value) {
        this.currentFilter = Filters.ne(key, value);
        return this.calcContext();
    }

    public FindFilter isLess(String key, Object value) {
        this.currentFilter = Filters.lt(key, value);
        return this.calcContext();
    }

    public FindFilter isLessOrEquals(String key, Object value) {
        this.currentFilter = Filters.lte(key, value);
        return this.calcContext();
    }

    public FindFilter isGreater(String key, Object value) {
        this.currentFilter = Filters.gt(key, value);
        return this.calcContext();
    }

    public FindFilter isGreaterOrEquals(String key, Object value) {
        this.currentFilter = Filters.gte(key, value);
        return this.calcContext();
    }

    public FindFilter isNull(String key) {
        this.currentFilter = Filters.exists(key, false);
        return this.calcContext();
    }

    public FindFilter isNotNull(String key) {
        this.currentFilter = Filters.exists(key, true);
        return this.calcContext();
    }

    public FindFilter isIDEquals(String id) {
        this.currentFilter = Filters.eq(new ObjectId(id));
        return this.calcContext();
    }
}
