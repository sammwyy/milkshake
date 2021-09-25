package com.dotphin.milkshake.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dotphin.milkshake.entity.ID;
import com.dotphin.milkshake.entity.Prop;
import com.dotphin.milkshake.repository.Repository;

public class EntityUtils {
    public static Map<String, Object> mapEntityToProps(final Object obj) {
        final Map<String, Object> props = new HashMap<>();
        final Class<?> clazz = obj.getClass();

        try {
            for (final Field field : clazz.getFields()) {
                if (field.isAnnotationPresent(Prop.class)) {
                    field.setAccessible(true);
                    props.put(field.getName(), field.get(obj));
                    field.setAccessible(false);
                }
            }

            return props;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object mapPropsToEntity(final Repository<?> repository, final Class<?> entity,
            final Map<String, Object> props) {
        if (props == null) {
            return null;
        }

        try {
            Object obj = entity.getConstructor(Repository.class).newInstance(repository);

            for (final Field field : entity.getFields()) {
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

            return obj;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object[] mapPropsToEntity(final Repository<?> repository, final Class<?> entity,
            final List<Map<String, Object>> propList) {
        Object[] objs = new Object[propList.size()];
        int index = 0;
        for (final Map<String, Object> props : propList) {
            objs[index] = EntityUtils.mapPropsToEntity(repository, entity, props);
            index++;
        }
        return objs;
    }
}
