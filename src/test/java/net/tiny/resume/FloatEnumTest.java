package net.tiny.resume;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class FloatEnumTest {

    public static interface FloatEnum<E extends Enum<E>> {
        float value();
        @SuppressWarnings("unchecked")
        default E toEnum() {
            return (E) this;
        }
    }

    @Test
    public void testFloatEnum() throws Exception {
        DummyFloat one = DummyFloat.One;
        assertEquals(1.1f,  one.value());
        DummyFloat o = one.toEnum();
        assertEquals(o, one);

        DummyFloat two = DummyFloat.valueOf("Two");
        assertEquals(2.2f,  two.value());
    }

    static enum DummyFloat implements FloatEnum<DummyFloat> {
        One(1.1f),
        Two(2.2f);

        private float value;
        DummyFloat(float value) {
            this.value = value;

        }

        @Override
        public float value() {
            return value;
        }

    }
}
