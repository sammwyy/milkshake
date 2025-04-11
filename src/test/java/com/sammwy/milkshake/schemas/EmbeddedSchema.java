package com.sammwy.milkshake.schemas;

import com.sammwy.milkshake.annotations.Embedded;
import com.sammwy.milkshake.annotations.ID;
import com.sammwy.milkshake.annotations.Prop;
import com.sammwy.milkshake.annotations.SchemaType;
import com.sammwy.milkshake.schema.Schema;

@SchemaType("Embedded")
public class EmbeddedSchema extends Schema {
    @ID
    public String id;

    @Prop
    public String single = "";

    @Embedded
    public EmbeddedObject embedded = new EmbeddedObject();
}
