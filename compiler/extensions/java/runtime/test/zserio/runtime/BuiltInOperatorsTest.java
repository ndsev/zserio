package zserio.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

public class BuiltInOperatorsTest
{
    private static class DummyBitmask implements ZserioBitmask
    {
        public DummyBitmask(short value)
        {
            this.value = value;
        }

        @Override
        public Number getGenericValue()
        {
            return value;
        }

        public static final class Values
        {
            public static final DummyBitmask READ = new DummyBitmask((short)1);
            public static final DummyBitmask WRITE = new DummyBitmask((short)2);
            public static final DummyBitmask CREATE = new DummyBitmask((short)(1 | 2));
        }

        public DummyBitmask or(DummyBitmask other)
        {
            return new DummyBitmask((short)(value | other.value));
        }

        private short value;
    }

    @Test
    public void isSet()
    {
        assertTrue(BuiltInOperators.isSet(DummyBitmask.Values.READ, DummyBitmask.Values.READ));
        assertTrue(BuiltInOperators.isSet(DummyBitmask.Values.CREATE, DummyBitmask.Values.READ));
        assertTrue(BuiltInOperators.isSet(DummyBitmask.Values.CREATE, DummyBitmask.Values.WRITE));
        assertTrue(BuiltInOperators.isSet(DummyBitmask.Values.CREATE, DummyBitmask.Values.CREATE));
        assertTrue(BuiltInOperators.isSet(
                DummyBitmask.Values.CREATE, DummyBitmask.Values.READ.or(DummyBitmask.Values.WRITE)));
        assertFalse(BuiltInOperators.isSet(DummyBitmask.Values.READ, DummyBitmask.Values.WRITE));
        assertFalse(BuiltInOperators.isSet(DummyBitmask.Values.READ, DummyBitmask.Values.CREATE));
    }

    @Test
    public void numBits()
    {
        assertEquals(0, BuiltInOperators.numBits(0));
        assertEquals(1, BuiltInOperators.numBits(1));
        assertEquals(1, BuiltInOperators.numBits(2));
        assertEquals(2, BuiltInOperators.numBits(3));
        assertEquals(2, BuiltInOperators.numBits(4));
        assertEquals(3, BuiltInOperators.numBits(5));
        assertEquals(3, BuiltInOperators.numBits(6));
        assertEquals(3, BuiltInOperators.numBits(7));
        assertEquals(3, BuiltInOperators.numBits(8));
        assertEquals(4, BuiltInOperators.numBits(16));
        assertEquals(5, BuiltInOperators.numBits(32));
        assertEquals(6, BuiltInOperators.numBits(64));
        assertEquals(7, BuiltInOperators.numBits(128));
        assertEquals(8, BuiltInOperators.numBits(256));
        assertEquals(9, BuiltInOperators.numBits(512));
        assertEquals(10, BuiltInOperators.numBits(1024));
        assertEquals(11, BuiltInOperators.numBits(2048));
        assertEquals(12, BuiltInOperators.numBits(4096));
        assertEquals(13, BuiltInOperators.numBits(8192));
        assertEquals(14, BuiltInOperators.numBits(16384));
        assertEquals(15, BuiltInOperators.numBits(32768));
        assertEquals(16, BuiltInOperators.numBits(65536));

        assertEquals(63, BuiltInOperators.numBits(BigInteger.ONE.shiftLeft(63)));
        assertEquals(64, BuiltInOperators.numBits(BigInteger.ONE.shiftLeft(63).add(BigInteger.ONE)));
    }

    @Test
    void lengthOf()
    {
        assertEquals(0, BuiltInOperators.lengthOf(""));
        assertEquals(3, BuiltInOperators.lengthOf("abc"));
        assertEquals(3, BuiltInOperators.lengthOf("€"));
        assertEquals(1, BuiltInOperators.lengthOf("$"));
        assertEquals(4, BuiltInOperators.lengthOf("€$"));
    }
}
