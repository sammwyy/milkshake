package com.sammwy.milkshake.entities;

import com.sammwy.classserializer.annotations.Serializable;
import com.sammwy.milkshake.Entity;

@Serializable
public class User extends Entity {
    public String name;
    public int age;
    public boolean verified;
}
