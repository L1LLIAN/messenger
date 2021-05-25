package dev.lillian.messenger.api;

import dev.lillian.messenger.api.annotation.Subscribe;
import dev.lillian.messenger.api.serialization.ISerializationService;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public abstract class AbstractMessenger {
    protected final ISerializationService serializationService;
    private final Map<Class<?>, Set<Consumer<Object>>> subscriptionMap = new HashMap<>();

    public AbstractMessenger(@NotNull ISerializationService serializationService) {
        requireNonNull(serializationService, "serializationService");
        this.serializationService = serializationService;
    }

    public final <T> void subscribe(@NotNull Class<T> type, @NotNull Consumer<T> onReceive) {
        subscriptionMap.compute(type, ($, set) -> {
            if (set == null) {
                set = new HashSet<>();
            }
            //noinspection unchecked
            set.add((Consumer<Object>) onReceive);
            return set;
        });
    }

    public final void subscribe(@NotNull Object parent) {
        for (Method method : parent.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Subscribe.class)) {
                continue;
            }

            if (method.getParameterCount() != 1) {
                continue;
            }

            Class<?> type = method.getParameterTypes()[0];
            if (!method.trySetAccessible()) {
                System.out.printf("Couldn't set method %s in %s accessible.%n", method.getName(), parent.getClass().getName());
                continue;
            }

            subscribe(type, (msg) -> {
                try {
                    method.invoke(msg);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public abstract void post(@NotNull Object object);

    protected final void receive(@NotNull String serializedObject) {
        requireNonNull(serializedObject, "serializedObject");
        Object object = serializationService.deserialize(serializedObject);
        for (Consumer<Object> consumer : subscriptionMap.getOrDefault(object.getClass(), Collections.emptySet())) {
            consumer.accept(object);
        }
    }
}
