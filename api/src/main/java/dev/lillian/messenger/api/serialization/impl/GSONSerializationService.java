package dev.lillian.messenger.api.serialization.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dev.lillian.messenger.api.serialization.ISerializationService;
import dev.lillian.messenger.api.serialization.SerializationException;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public final class GSONSerializationService implements ISerializationService {
    private final Gson gson = new Gson();

    @Override
    public @NotNull String serialize(@NotNull Object object) {
        requireNonNull(object, "object");

        String serialized = "";
        serialized += object.getClass().getName();
        serialized += "@";
        serialized += gson.toJson(object);

        return serialized;
    }

    @Override
    public @NotNull Object deserialize(@NotNull String serializedObject) {
        requireNonNull(serializedObject, "serializedObject");

        String[] split = serializedObject.split("@", 2);
        if (split.length != 2) {
            throw new SerializationException("serializedObject data is not valid!");
        }

        Class<?> type;
        try {
            type = Class.forName(split[0]);
        } catch (ClassNotFoundException e) {
            throw new SerializationException(e);
        }

        try {
            return gson.fromJson(split[1], type);
        } catch (JsonSyntaxException e) {
            throw new SerializationException(e);
        }
    }
}
