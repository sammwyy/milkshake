package com.sammwy.milkshake.schemas;

import com.sammwy.milkshake.annotations.Prop;
import com.sammwy.milkshake.schema.Schema;

public class EmbeddedObject extends Schema {
    @Prop
    public String child;

    @Prop
    public int foo;

    @Prop
    public boolean hello;
}
