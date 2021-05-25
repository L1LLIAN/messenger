package dev.lillian.messenger.jedis;

import com.github.fppt.jedismock.RedisServer;
import dev.lillian.messenger.api.annotation.Subscribe;
import dev.lillian.messenger.api.serialization.impl.GSONSerializationService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.util.Pool;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class JedisMessengerTest {
    private static final TestPOJO VALID_POJO = new TestPOJO();
    private static final String VALID_STRING = "@@)*&!@#&^*(SZJK";
    private static RedisServer server = null;

    static {
        VALID_POJO.string = VALID_STRING;
    }

    private final CountDownLatch latch = new CountDownLatch(2);

    @BeforeAll
    static void before() throws IOException {
        server = RedisServer.newRedisServer();
        server.start();
    }

    @AfterAll
    static void after() {
        server.stop();
        server = null;
    }

    @Test
    public void post() throws InterruptedException {
        Pool<Jedis> pool = new JedisPool(server.getHost(), server.getBindPort());
        JedisMessenger messenger = new JedisMessenger(pool, "owo", new GSONSerializationService());
        messenger.subscribe(TestPOJO.class, this::validateAndCountdown);
        messenger.subscribe(this);

        messenger.post(VALID_POJO);

        latch.await(500, TimeUnit.MILLISECONDS);
    }

    @Subscribe
    private void subscription(TestPOJO pojo) {
        validateAndCountdown(pojo);
    }

    private void validateAndCountdown(TestPOJO toValidate) {
        if (VALID_POJO.equals(toValidate)) {
            latch.countDown();
        }
    }

    static class TestPOJO {
        public String string;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestPOJO testPOJO = (TestPOJO) o;
            return Objects.equals(string, testPOJO.string);
        }

        @Override
        public int hashCode() {
            return Objects.hash(string);
        }
    }
}