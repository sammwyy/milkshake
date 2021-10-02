package com.dotphin.milkshake_example;

import com.dotphin.milkshakeorm.entity.Entity;
import com.dotphin.milkshakeorm.entity.ID;
import com.dotphin.milkshakeorm.entity.Prop;

public class User extends Entity {
    @ID
    public String id;

    @Prop
    public String username;

    @Prop
    public String email;

    @Prop
    public String password;

    public void print() {
        System.out.println("[User] {id: " + this.id + ", username: " + this.username + ", email: " + this.email
                + ", password: " + this.password + "}");
    }
}
