package com.dotphin.milkshake_example;

import com.dotphin.milkshakeorm.providers.MongoProvider;
import com.dotphin.milkshakeorm.utils.MapFactory;

public class ExampleApp {
    public static void main(final String[] args) {
        MongoProvider prov = new MongoProvider();
        prov.connect("mongodb://localhost/testing");

        // Create
        System.out.println("[1] Created -> " + prov.create("hello", MapFactory.create("username", "sammwy")));

        // Read
        System.out.println("[2] Read -> " + prov.findOne("hello", MapFactory.create("username", "sammwy")));

        // Update
        System.out.println("[3] Update -> " + prov.updateOne("hello", MapFactory.create("username", "sammwy"),
                MapFactory.create("username", "melon")));

        // Read again
        System.out.println("[4] Read -> " + prov.findOne("hello", MapFactory.create("username", "sammwy")));
        System.out.println("[5] Read ->" + prov.findOne("hello", MapFactory.create("username", "melon")));

        // Delete
        System.out.println("[6] Delete -> " + prov.deleteOne("hello", MapFactory.create("username", "melon")));
        System.out.println("[7] Read ->" + prov.findOne("hello", MapFactory.create("username", "melon")));
    }
}
