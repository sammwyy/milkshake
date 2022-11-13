package com.dotphin.milkshake.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.dotphin.milkshake.Milkshake;
import com.dotphin.milkshake.Provider;
import com.dotphin.milkshake.Repository;
import com.dotphin.milkshake.entities.User;
import com.dotphin.milkshake.find.FindFilter;
import com.dotphin.milkshake.operations.Operation;

@TestMethodOrder(OrderAnnotation.class)
public class OperationTest {
    @Test
    @Order(1)
    public void connection() {
        Provider provider = Milkshake.connect("mongodb://localhost:27017/unittesting");
        provider.findMany("dummy_collection", new FindFilter());

        assertTrue(provider.isActive());
        assertNotNull(Milkshake.getProvider());
    }

    @Test
    @Order(2)
    public void registerRepository() {
        Repository<User> repository = Milkshake.addRepository(User.class);
        repository.findMany(new FindFilter());
    }

    @Test
    @Order(3)
    public void create() {
        User user = new User();
        user.name = "testing";
        user.age = 20;
        user.verified = true;
        user.save();
    }

    @Test
    @Order(4)
    public void updateWithOperation() {
        Repository<User> users = Milkshake.getRepository(User.class);
        FindFilter filter = new FindFilter("name", "testing");
        Operation operation = new Operation().unset("name");
        users.updateMany(filter, operation);

        User user = users.findOne(new FindFilter("name", null));
        assertNotNull(user);
        assertNull(user.name);

        user.delete();
        user = users.findOne(filter);
        assertNull(user);
    }
}
