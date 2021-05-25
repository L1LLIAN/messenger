package dev.lillian.messenger.local;

import dev.lillian.messenger.api.annotation.Subscribe;
import dev.lillian.messenger.api.serialization.impl.GSONSerializationService;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class LocalMessengerTest {
    private static final TestPOJO VALID_POJO = new TestPOJO();
    private static final String VALID_STRING = "@@)*&!@#&^*(SZJK";

    static {
        VALID_POJO.string = VALID_STRING;
    }

    private final LocalMessenger messenger = new LocalMessenger(new GSONSerializationService());
    private final CountDownLatch latch = new CountDownLatch(2);

    @Test
    void post() throws InterruptedException {
        messenger.subscribe(TestPOJO.class, this::validateAndCountdown);
        messenger.subscribe(this);

        messenger.post(VALID_POJO);

        latch.await(250, TimeUnit.MILLISECONDS);
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