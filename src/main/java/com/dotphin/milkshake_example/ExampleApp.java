package com.dotphin.milkshake_example;

import com.dotphin.milkshake.DataQuery;
import com.dotphin.milkshake.DatabaseType;
import com.dotphin.milkshake.Milkshake;
import com.dotphin.milkshake.repository.Repository;

public class ExampleApp {
    public static void main(final String[] args) {
        System.out.println("\nHello world from MilkshakeORM.");

        // Conectarse a la base de datos MongoDB usando su correspondiente driver.
        Milkshake.connect(DatabaseType.MONGODB, "mongodb://localhost/test");

        // Registrar repositorio.
        Milkshake.addRepository(User.class);

        // Obtener su repositorio.
        Repository<User> userRepository = Milkshake.getRepository(User.class);

        User findUser = userRepository.findOne(new DataQuery().is("username", "melon"));

        System.out.println(findUser);
        /*
         * // Crear usuario. User user = new User(); user.email =
         * "sammwy.dev@gmail.com"; user.username = "sammwy"; user.password = "12345678";
         * 
         * userRepository.put(user);
         * 
         * // Encontrar usuario. User findUser = userRepository.findOne(new
         * DataQuery().is("username", "sammwy"));
         * System.out.println(findUser.toString());
         */
    }
}
