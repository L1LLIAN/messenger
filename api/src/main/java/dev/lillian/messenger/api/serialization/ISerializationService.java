package dev.lillian.messenger.api.serialization;

import org.jetbrains.annotations.NotNull;

public interface ISerializationService {
    @NotNull
    String serialize(@NotNull Object object);

    @NotNull
    Object deserialize(@NotNull String serializedObject);
}
