package com.sammwy.milkshake.sorting;

public enum SortOrder {
    ASCENDANT(1), DESCENDANT(-1);

    private int value;

    SortOrder(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
