package com.dotphin.milkshakeorm.entity;

import com.dotphin.milkshakeorm.MilkshakeORM;
import com.dotphin.milkshakeorm.repository.Repository;

public class Entity {
    private final Repository<?> repository;

    public Entity() {
        this.repository = MilkshakeORM.getRepository(this.getClass());
    }

    public Entity(final Repository<?> repository) {
        this.repository = repository;
    }

    public void save() {
        try {
            this.repository.save(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        try {
            this.repository.delete(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refresh() {
        try {
            this.repository.refresh(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
