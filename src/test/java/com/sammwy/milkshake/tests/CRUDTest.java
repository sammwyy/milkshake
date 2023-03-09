package com.sammwy.milkshake.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sammwy.milkshake.entities.User;
import com.sammwy.milkshake.Milkshake;
import com.sammwy.milkshake.Provider;
import com.sammwy.milkshake.Repository;
import com.sammwy.milkshake.find.FindFilter;

@TestMethodOrder(OrderAnnotation.class)
public class CRUDTest {
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
        user.name = "Sammwy";
        user.age = 20;
        user.verified = true;
        user.save();

        assertNotNull(user.getID());
    }

    @Test
    @Order(4)
    public void read() {
        Repository<User> users = Milkshake.getRepository(User.class);
        FindFilter filter = new FindFilter("name", "Sammwy").and().isEquals("age", 20);
        User user = users.findOne(filter);

        assertNotNull(user);
        assertEquals(user.name, "Sammwy");
        assertEquals(user.age, 20);
        assertEquals(user.verified, true);
    }

    @Test
    @Order(5)
    public void update() {
        Repository<User> users = Milkshake.getRepository(User.class);
        FindFilter filter = new FindFilter("name", "Sammwy").and().isGreater("age", 19);
        User user = users.findOne(filter);

        user.name = null;
        user.age = 0;
        user.verified = false;
        user.save();

        filter = new FindFilter("name", null).and().isLess("age", 1);
        user = users.findOne(filter);

        assertNull(user.name);
        assertEquals(user.age, 0);
        assertFalse(user.verified);
    }

    @Test
    @Order(6)
    public void delete() {
        Repository<User> users = Milkshake.getRepository(User.class);
        FindFilter filter = new FindFilter("name", null).and().isLess("age", 1);
        User user = users.findOne(filter);
        user.delete();
        user = users.findOne(filter);
        assertNull(user);
    }
}
