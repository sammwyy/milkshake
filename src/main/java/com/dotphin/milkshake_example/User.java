package com.dotphin.milkshake_example;

import com.dotphin.milkshake.entity.Entity;
import com.dotphin.milkshake.entity.ID;
import com.dotphin.milkshake.entity.Prop;

public class User extends Entity {
    @ID
    public String id;

    @Prop(isRequired = true)
    public String username;

    @Prop(isRequired = true)
    public String email;

    @Prop(isRequired = true)
    public String password;

    public String toString() {
        return "[ID=" + id + ", Username=" + this.username + ", Email=" + this.email + ", Password=" + this.password
                + "]";
    }
}
