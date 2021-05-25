package dev.lillian.messenger.jedis;

import dev.lillian.messenger.api.AbstractMessenger;
import dev.lillian.messenger.api.serialization.ISerializationService;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.util.Pool;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public final class JedisMessenger extends AbstractMessenger {
    private final Pool<Jedis> pool;
    private final String channel;

    public JedisMessenger(@NotNull Pool<Jedis> pool, @NotNull String channel, @NotNull ISerializationService serializationService) throws InterruptedException {
        super(serializationService);
        requireNonNull(pool, "pool");
        requireNonNull(channel, "channel");

        this.pool = pool;
        this.channel = channel;

        CountDownLatch latch = new CountDownLatch(1);
        try (Jedis jedis = pool.getResource()) {
            jedis.subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    receive(message);
                }

                @Override
                public void onSubscribe(String channel, int subscribedChannels) {
                    latch.countDown();
                }
            }, channel);
        }

        if (!latch.await(1, TimeUnit.SECONDS)) {
            throw new RuntimeException("Took longer than 1 second for Jedis to subscribe.");
        }
    }

    @Override
    public void post(@NotNull Object object) {
        try (Jedis jedis = pool.getResource()) {
            jedis.publish(channel, serializationService.serialize(object));
        }
    }
}
