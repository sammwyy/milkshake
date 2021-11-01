package com.dotphin.milkshakeorm_tests;

import com.dotphin.milkshakeorm.MilkshakeORM;
import com.dotphin.milkshakeorm.providers.Provider;
import com.dotphin.milkshakeorm.repository.Repository;
import com.dotphin.milkshakeorm.utils.MapFactory;

public class MS_ORM_Unit_testing {
    public static void main(final String[] args) {
        final Provider provider = MilkshakeORM.connect("mongodb://localhost/test-1");
        final Repository<UserEntity> repository = MilkshakeORM.addRepository(UserEntity.class, provider);
        System.out.println("");

        // (C)RUD - Create operation
        final UserEntity create = new UserEntity();
        create.username = "test";
        create.password = "1234";
        create.email = "test@example.com";
        create.save();
        if (create._id != null) {
            System.out.println("[1] Create: OK");
        } else {
            System.out.println("[1] Create: Failed");
        }

        // C(R)UD - Read operation
        final UserEntity read = repository.findOne(MapFactory.create("username", "test"));
        if (read != null && read.username.equals("test")) {
            System.out.println("[2] Read: OK");
        } else {
            System.out.println("[2] Read: Failed");
        }

        final UserEntity update = repository.findOne(MapFactory.create("username", "test"));
        update.username = "another";
        update.save();
        final UserEntity updateRead = repository.findOne(MapFactory.create("username", "another"));
        if (updateRead != null)
            System.out.println("[3] Update: OK");
        else
            System.out.println("[3] Update: Failed");

        updateRead.delete();
        final UserEntity delete = repository.findOne(MapFactory.create("username", "another"));
        if (delete == null)
            System.out.println("[4] Delete: OK");
        else
            System.out.println("[4] Delete: Failed");
    }
}