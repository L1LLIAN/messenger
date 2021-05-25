package dev.lillian.messenger.local;

import dev.lillian.messenger.api.AbstractMessenger;
import dev.lillian.messenger.api.serialization.ISerializationService;
import org.jetbrains.annotations.NotNull;

public final class LocalMessenger extends AbstractMessenger {
    public LocalMessenger(@NotNull ISerializationService serializationService) {
        super(serializationService);
    }

    @Override
    public void post(@NotNull Object object) {
        String serialized = serializationService.serialize(object);
        receive(serialized);
    }
}
