package com.sammwy.milkshake.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.sammwy.milkshake.Provider;
import com.sammwy.milkshake.ProviderInfo;
import com.sammwy.milkshake.Repository;
import com.sammwy.milkshake.providers.mongo.MongoProvider;
import com.sammwy.milkshake.query.Filter;
import com.sammwy.milkshake.schemas.EmbeddedObject;
import com.sammwy.milkshake.schemas.EmbeddedSchema;
import com.sammwy.milkshake.schemas.UserSchema;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MongoDBTests {
    private static Repository<UserSchema> repository;
    private static String id;

    private static Repository<EmbeddedSchema> embeddedRepository;

    @BeforeAll
    public static void setup() {
        System.out.println("Setting up MongoProvider...");
        Provider provider = new MongoProvider();
        provider.connect(new ProviderInfo("mongodb://localhost/milkshake-test"));
        repository = provider.addRepository(UserSchema.class);
        embeddedRepository = provider.addRepository(EmbeddedSchema.class);
    }

    @AfterAll
    public static void clearDB() {
        // repository.delete(new Filter.Find());
        System.out.println("Cleared test collection.");
    }

    @Test
    @Order(1)
    public void testCreate() {
        UserSchema user = new UserSchema();
        user.username = "sammwy";
        user.age = 23;

        boolean saved = user.save();
        id = user.getId();

        assertTrue(saved, "User should be saved");
        assertNotNull(id, "Saved user should have an ID");
    }

    @Test
    @Order(2)
    public void testRead() {
        UserSchema user = repository.findById(id);
        assertNotNull(user, "User should be found by ID");
        assertEquals("sammwy", user.username);
        assertEquals(23, user.age);
    }

    @Test
    @Order(3)
    public void testUpdate() {
        UserSchema user = repository.findById(id);
        assertNotNull(user, "User must exist before update");

        user.age = 30;
        boolean updated = user.save();

        assertTrue(updated, "User update should succeed");

        UserSchema updatedUser = repository.findById(id);
        assertNotNull(updatedUser);
        assertEquals(30, updatedUser.age);
    }

    @Test
    @Order(4)
    public void testDelete() {
        UserSchema user = repository.findById(id);
        assertNotNull(user);

        boolean deleted = user.delete();
        assertTrue(deleted, "User should be deleted");

        UserSchema deletedUser = repository.findById(id);
        assertNull(deletedUser, "User should not be found after deletion");
    }

    @Test
    @Order(5)
    public void testFindByQuery() {
        UserSchema user = new UserSchema();
        user.username = "queryTest";
        user.age = 25;
        user.save();

        List<UserSchema> results = repository.find(new Filter.Find().eq("username", "queryTest"));
        assertFalse(results.isEmpty(), "Should find at least one user with username 'queryTest'");

        for (UserSchema u : results) {
            u.delete(); // Clean up
        }
    }

    @Test
    @Order(6)
    public void testEmbedded() {
        EmbeddedSchema entity = new EmbeddedSchema();
        entity.single = "Hello World";
        entity.embedded = new EmbeddedObject();
        entity.embedded.child = "Child";
        entity.embedded.foo = 12345;
        entity.embedded.hello = true;

        boolean saved = entity.save();
        assertTrue(saved, "Entity should be saved");

        EmbeddedSchema result = embeddedRepository.findById(entity.getId());
        assertNotNull(result, "Entity should be found by ID");

        assertEquals(entity.single, result.single, "Single field should match");
        assertEquals(entity.embedded.child, result.embedded.child, "Child field should match");
        assertEquals(entity.embedded.foo, result.embedded.foo, "Foo field should match");
        assertEquals(entity.embedded.hello, result.embedded.hello, "Hello field should match");
    }
}
