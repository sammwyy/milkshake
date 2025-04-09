package com.sammwy.milkshake.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sammwy.milkshake.Schema;

/**
 * Specifies the collection name for a schema class in the database.
 * This annotation allows customizing the MongoDB collection name that
 * will be used to store documents of this schema type.
 *
 * <p>
 * When not specified, the simple class name will be used as the collection
 * name.
 *
 * <p>
 * Example usage:
 * 
 * <pre>
 * {@code
 * @SchemaType("users")
 * public class User extends Schema {
 *     // class implementation
 * }
 * }
 * </pre>
 *
 * @see Schema
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SchemaType {
    /**
     * The custom name for the database collection.
     * If empty (default), the simple name of the class will be used.
     *
     * @return The collection name, or empty string to use the class name
     */
    String value() default "";
}