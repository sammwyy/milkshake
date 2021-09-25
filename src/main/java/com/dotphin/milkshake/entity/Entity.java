package com.dotphin.milkshake.entity;

import com.dotphin.milkshake.repository.Repository;

public class Entity {

    // private final Repository<?> repository;

    protected Entity() {
        // this.repository = null;
        throw new Error("Cannot instantiate.");
    }

    public Entity(final Repository<?> repository) {
        // this.repository = repository;
    }

    public void save() {

    }

}
