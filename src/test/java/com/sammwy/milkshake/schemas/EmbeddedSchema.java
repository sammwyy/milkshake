package com.sammwy.milkshake.schemas;

import com.sammwy.milkshake.annotations.Embedded;
import com.sammwy.milkshake.annotations.Prop;
import com.sammwy.milkshake.annotations.SchemaType;
import com.sammwy.milkshake.schema.Schema;

@SchemaType("Embedded")
public class EmbeddedSchema extends Schema {
    @Prop
    public String single;

    @Embedded
    public EmbeddedObject embedded;
}
