package com.sammwy.milkshake.providers;

import com.sammwy.classserializer.ClassSerializer;
import com.sammwy.milkshake.Provider;
import com.sammwy.milkshake.SerializerRegistry;

public abstract class AbstractProvider implements Provider {
    private ClassSerializer serializer;

    public AbstractProvider() {
        this.serializer = SerializerRegistry.createSerializer(!this.supportsEmbedded());
    }

    @Override
    public ClassSerializer getSerializer() {
        return serializer;
    }
}
