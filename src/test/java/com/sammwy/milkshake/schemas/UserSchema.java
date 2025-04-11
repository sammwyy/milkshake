package com.sammwy.milkshake.schemas;

import com.sammwy.milkshake.annotations.ID;
import com.sammwy.milkshake.annotations.Prop;
import com.sammwy.milkshake.annotations.SchemaType;
import com.sammwy.milkshake.schema.Schema;

@SchemaType("Users")
public class UserSchema extends Schema {
    @ID
    public String id;
    @Prop
    public String username = "";
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
