package com.dotphin.milkshake_example;

import com.dotphin.milkshakeorm.DatabaseType;
import com.dotphin.milkshakeorm.MilkshakeORM;
import com.dotphin.milkshakeorm.repository.Repository;
import com.dotphin.milkshakeorm.utils.MapFactory;

public class ExampleApp {

    public static void test1_connect_mongodb() {
        System.out.println("=== Test #1 - Connect to MongoDB ===");

        MilkshakeORM.connect(DatabaseType.MONGODB, "mongodb://localhost/testing");

        System.out.println("\n");
    }

    public static void test2_register_repository() {
        System.out.println("=== Test #2 - Register Repository ===");

        MilkshakeORM.addRepository(User.class);

        System.out.println("\n");
    }

    public static void test3_create_op() {
        System.out.println("=== Test #3 - Create operation ===");

        User user = new User();
        user.username = "sammwy";
        user.email = "sammwy.dev@gmail.com";
        user.password = "12345678";
        user.save();

        user.print();

        System.out.println("\n");
    }

    public static void test4_read_op() {
        System.out.println("=== Test #4 - Read operation ===");

        Repository<User> repository = MilkshakeORM.getRepository(User.class);
        User user = repository.findOne(MapFactory.create("username", "sammwy"));
        user.print();

        System.out.println("\n");
    }

    public static void test5_update_op() {
        System.out.println("=== Test #5 - Update operation ===");

        Repository<User> repository = MilkshakeORM.getRepository(User.class);
        User user = repository.findOne(MapFactory.create("username", "sammwy"));
        user.username = "melon";
        user.save();
        user.print();

        System.out.println("\n");
    }

    public static void test6_read_post_op() {
        System.out.println("=== Test #6 - Read post operation ===");

        Repository<User> repository = MilkshakeORM.getRepository(User.class);

        User user1 = repository.findOne(MapFactory.create("username", "sammwy"));
        if (user1 != null) {
            user1.print();
        } else {
            System.out.println("[User 1] Null");
        }

        User user2 = repository.findOne(MapFactory.create("username", "melon"));
        if (user2 != null) {
            user2.print();
        } else {
            System.out.println("[User 2] Null");
        }

        System.out.println("\n");
    }

    public static void test7_delete_op() {
        System.out.println("=== Test #7 - Delete operation ===");

        Repository<User> repository = MilkshakeORM.getRepository(User.class);
        repository.findOne(MapFactory.create("username", "melon")).delete();

        User user = repository.findOne(MapFactory.create("username", "melon"));

        if (user != null) {
            user.print();
        } else {
            System.out.println("[User] Null");
        }

        System.out.println("\n");
    }

    public static void main(final String[] args) {
        test1_connect_mongodb();
        test2_register_repository();
        test3_create_op();
        test4_read_op();
        test5_update_op();
        test6_read_post_op();
        test7_delete_op();
    }
}
