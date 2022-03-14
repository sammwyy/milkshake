package com.dotphin.milkshakeorm.utils;

import com.dotphin.milkshakeorm.entity.ID;
import com.dotphin.milkshakeorm.entity.Prop;
import com.dotphin.milkshakeorm.errors.NotIDAnnotationException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityUtils {

    public static void setEntityID(final Object obj, final String id) throws NotIDAnnotationException {
        final Class<?> clazz = obj.getClass();

        try {
            for (final Field field : clazz.getFields()) {
                if (field.isAnnotationPresent(ID.class)) {
                    field.setAccessible(true);
                    field.set(obj, id);
                    return;
                }
            }
        } catch (final Exception ignored) {
            // Ignore
        }

        throw new NotIDAnnotationException(obj);
    }

    public static String getEntityID(final Object obj) throws NotIDAnnotationException {
        final Class<?> clazz = obj.getClass();

        try {
            for (final Field field : clazz.getFields()) {
                if (field.isAnnotationPresent(ID.class)) {
                    return (String) field.get(obj);
                }
            }
        } catch (final Exception ignored) {
            // Ignore
        }

        throw new NotIDAnnotationException(obj);
    }

    public static boolean isSerializableObject(final Object value) {
        return value instanceof Integer || value instanceof String || value instanceof Boolean || value instanceof Float
                || value instanceof Double || value instanceof Map || value instanceof HashMap
                || value instanceof String[] || value instanceof Integer[] || value instanceof Boolean[]
                || value instanceof Float[] || value instanceof Double[] || value instanceof Map[]
                || value instanceof HashMap[] || value instanceof List || value instanceof ArrayList || value instanceof Long || value instanceof Long[];
    }

    public static Map<String, Object> mapEntityToProps(final Object obj) {
        final Map<String, Object> props = new HashMap<>();
        final Class<?> clazz = obj.getClass();

        try {
            for (final Field field : clazz.getFields()) {
                if (field.isAnnotationPresent(Prop.class)) {
                    Object value = field.get(obj);
                    if (isSerializableObject(value)) {
                        props.put(field.getName(), field.get(obj));
                    }
                }
            }

            return props;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void injectPropsToEntity(final Object obj, final Map<String, Object> props)
            throws IllegalAccessException {
        for (final Field field : obj.getClass().getFields()) {
            String key = field.getName();

            if (field.isAnnotationPresent(ID.class)) {
                key = "_id";
            }

            if (!props.containsKey(key)) {
                continue;
            }

            if (field.isAnnotationPresent(Prop.class) || field.isAnnotationPresent(ID.class)) {
                final Object value = props.get(key);

                field.setAccessible(true);
                try {
                    field.set(obj, value);
                } catch (IllegalArgumentException ignored) {
                    field.set(obj, value.toString());
                }
                field.setAccessible(false);
            }
        }
    }

    public static Object mapPropsToEntity(final Class<?> entity, final Map<String, Object> props) {
        if (props == null) {
            return null;
        }

        try {
            Object obj = entity.getConstructor().newInstance();
            EntityUtils.injectPropsToEntity(obj, props);
            return obj;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object[] mapPropsToEntity(final Class<?> entity, final List<Map<String, Object>> propList) {
        final List<Object> objs = new ArrayList<>();
        for (final Map<String, Object> props : propList) {
            Object value = EntityUtils.mapPropsToEntity(entity, props);
            if (value.getClass().equals(entity)) {
                objs.add(value);
            }
        }
        return objs.toArray();
    }
}
