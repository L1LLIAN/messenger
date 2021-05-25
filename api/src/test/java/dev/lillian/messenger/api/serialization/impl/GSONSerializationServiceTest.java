package dev.lillian.messenger.api.serialization.impl;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GSONSerializationServiceTest {
    private static final String VALID_STRING = "OUASD(*Y!@#NOIJU!@#*(";
    private static final long VALID_LONG = 1239879182731238971L;

    private final GSONSerializationService gsonSerializationService = new GSONSerializationService();

    @Test
    void serialize_deserialize() {
        TestPOJO original = new TestPOJO();
        original.string = VALID_STRING;
        original.aLong = VALID_LONG;

        String serialized = gsonSerializationService.serialize(original);
        assertNotNull(serialized);

        TestPOJO deserialized = (TestPOJO) gsonSerializationService.deserialize(serialized);
        assertNotNull(deserialized);
        assertEquals(original, deserialized);
    }

    static class TestPOJO {
        public String string;
        public long aLong;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestPOJO testPOJO = (TestPOJO) o;
            return aLong == testPOJO.aLong && Objects.equals(string, testPOJO.string);
        }

        @Override
        public int hashCode() {
            return Objects.hash(string, aLong);
        }
    }
}