package com.sammwy.milkshake.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sammwy.milkshake.schema.Schema;

/**
 * Marks a field as a persistent property in the database schema.
 * This annotation allows customization of how a field is mapped to a database
 * document property.
 *
 * <p>
 * Example usage:
 * 
 * <pre>
 * {@code
 * @Prop(name = "user_name", required = true, defaultValue = "guest")
 * private String username;
 * }
 * </pre>
 *
 * @see Schema
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Embedded {
    /**
     * Specifies the prefix for the embedded document fields.
     * This is useful when you have multiple embedded documents in a single
     * field.
     * 
     * MongoDB doesn't need this.
     * SQL Like providers need this.
     * 
     * @return The prefix for the embedded document fields
     */
    String prefix() default "";
}