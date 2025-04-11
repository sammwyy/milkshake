package com.sammwy.milkshake;

import java.util.HashMap;
import java.util.Map;

import com.sammwy.classserializer.ClassSerializer;
import com.sammwy.classserializer.ClassSerializer.SkipNull;
import com.sammwy.classserializer.Serializer;
import com.sammwy.milkshake.annotations.Embedded;
import com.sammwy.milkshake.annotations.ID;
import com.sammwy.milkshake.annotations.Prop;
import com.sammwy.milkshake.annotations.SchemaType;
import com.sammwy.milkshake.schema.Schema;

public class SerializerRegistry {
    private static Map<Class<?>, Serializer<?>> serializers = new HashMap<>();

    public static <T> void register(Class<T> type, Serializer<T> serializer) {
        serializers.put(type, serializer);
    }

    public static Map<Class<?>, Serializer<?>> getSerializers() {
        return serializers;
    }

    @SuppressWarnings("unchecked")
    public static ClassSerializer createSerializer(boolean deflate) {
        ClassSerializer classSerializer = new ClassSerializer();

        for (Map.Entry<Class<?>, Serializer<?>> entry : serializers.entrySet()) {
            Class<Object> clazz = (Class<Object>) entry.getKey();
            Serializer<Object> serializer = (Serializer<Object>) entry.getValue();
            classSerializer.addSerializer(clazz, serializer);
        }

        classSerializer.fieldPredicate((field, obj) -> {
            Prop prop = field.getAnnotation(Prop.class);
            if (prop != null) {
                return prop.name().isEmpty() ? field.getName() : prop.name();
            }

            Embedded embedded = field.getAnnotation(Embedded.class);
            if (embedded != null) {
                return embedded.prefix().isEmpty() ? field.getName() : embedded.prefix();
            }

            ID id = field.getAnnotation(ID.class);
            if (id != null) {
                return id.name().isEmpty() ? field.getName() : id.name();
            }

            return null;
        });

        classSerializer.withSkipNull(SkipNull.NONE);

        classSerializer.addClassPredicate((clazz) -> {
            return clazz.getAnnotation(SchemaType.class) != null || Schema.class.isAssignableFrom(clazz);
        });

        if (deflate) {
            classSerializer.withDeflate("__");
        }

        return classSerializer;
    }
}
