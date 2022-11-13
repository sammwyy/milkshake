package com.dotphin.milkshake.entities;

import com.dotphin.classserializer.annotations.Serializable;
import com.dotphin.milkshake.Entity;

@Serializable
public class User extends Entity {
    public String name;
    public int age;
    public boolean verified;
}
