package dev.lillian.messenger.api.serialization;

import org.jetbrains.annotations.NotNull;

public final class SerializationException extends RuntimeException {
    private static final long serialVersionUID = 7271692761007311028L;

    public SerializationException(@NotNull String message) {
        super(message);
    }

    public SerializationException(@NotNull Throwable throwable) {
        super(throwable);
    }
}
