package com.sammwy.milkshake.utils;

import com.sammwy.milkshake.Schema;

public class SchemaUtils {
    public static boolean validateSchema(Schema entity) {
        if (entity == null)
            throw new IllegalArgumentException("Entity cannot be null");
        return true;
    }
}