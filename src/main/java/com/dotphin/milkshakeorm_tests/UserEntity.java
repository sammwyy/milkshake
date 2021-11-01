package com.dotphin.milkshakeorm_tests;

import com.dotphin.milkshakeorm.entity.Entity;
import com.dotphin.milkshakeorm.entity.ID;
import com.dotphin.milkshakeorm.entity.Prop;

public class UserEntity extends Entity {
    @ID
    public String _id;

    @Prop
    public String username;

    @Prop
    public String password;

    @Prop
    public String email;
}