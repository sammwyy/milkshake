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
public @interface Prop {
    /**
     * Specifies the name of the field in the database document.
     * If empty (default), the Java field name will be used as the document field
     * name.
     *
     * @return The custom name for the database field, or empty string to use the
     *         field name
     */
    String name() default "";

    /**
     * Indicates whether this property is required for document validation.
     * When true, the system will enforce that this field has a non-null value.
     *
     * @return true if the property is required, false otherwise (default)
     */
    boolean required() default false;

    /**
     * Explicitly specifies the expected type of this property.
     * By default, the system will use the field's declared type.
     *
     * @return The expected type of the property (defaults to Object.class for
     *         automatic detection)
     */
    Class<?> type() default Object.class;

    /**
     * Specifies a default value for this property when none is provided.
     * The value should be a string representation that can be converted to the
     * field's type.
     *
     * @return The default value as a string, or empty string if no default is
     *         specified
     */
    String defaultValue() default "";
}
