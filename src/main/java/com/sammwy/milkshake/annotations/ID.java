package com.sammwy.milkshake.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.sammwy.milkshake.schema.Schema;

/**
 * Marks a field as the unique identifier for a document in the database.
 * This annotation indicates which field should be used as the primary key
 * when persisting and retrieving documents.
 * 
 * <p>
 * Example usage:
 * 
 * <pre>
 * {@code
 * @ID(auto = true)
 * private String id;
 * }
 * </pre>
 * 
 * @see Schema
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ID {
    /**
     * Specifies the name of the field in the database document. "_id" is the
     * default field.
     * name.
     *
     * @return The custom name for the database field.
     */
    String name() default "_id";
}