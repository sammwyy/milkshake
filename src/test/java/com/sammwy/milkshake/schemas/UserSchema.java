package com.sammwy.milkshake.schemas;

import com.sammwy.milkshake.Schema;
import com.sammwy.milkshake.annotations.Prop;

public class UserSchema extends Schema {
    @Prop
    public String username;
    @Prop
    public int age;

    public UserSchema() {
    }

    public UserSchema(String username, int age) {
        super();
        this.username = username;
        this.age = age;
    }
}
