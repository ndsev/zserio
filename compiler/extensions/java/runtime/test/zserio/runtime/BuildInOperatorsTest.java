package zserio.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class BuildInOperatorsTest
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
        assertTrue(BuildInOperators.isSet(DummyBitmask.Values.READ, DummyBitmask.Values.READ));
        assertTrue(BuildInOperators.isSet(DummyBitmask.Values.CREATE, DummyBitmask.Values.READ));
        assertTrue(BuildInOperators.isSet(DummyBitmask.Values.CREATE, DummyBitmask.Values.WRITE));
        assertTrue(BuildInOperators.isSet(DummyBitmask.Values.CREATE, DummyBitmask.Values.CREATE));
        assertTrue(BuildInOperators.isSet(DummyBitmask.Values.CREATE,
                DummyBitmask.Values.READ.or(DummyBitmask.Values.WRITE)));
        assertFalse(BuildInOperators.isSet(DummyBitmask.Values.READ, DummyBitmask.Values.WRITE));
        assertFalse(BuildInOperators.isSet(DummyBitmask.Values.READ, DummyBitmask.Values.CREATE));
    }

    @Test
    public void getNumBits()
    {
        assertEquals(0, BuildInOperators.getNumBits(0));
        assertEquals(1, BuildInOperators.getNumBits(1));
        assertEquals(1, BuildInOperators.getNumBits(2));
        assertEquals(2, BuildInOperators.getNumBits(3));
        assertEquals(2, BuildInOperators.getNumBits(4));
        assertEquals(3, BuildInOperators.getNumBits(5));
        assertEquals(3, BuildInOperators.getNumBits(6));
        assertEquals(3, BuildInOperators.getNumBits(7));
        assertEquals(3, BuildInOperators.getNumBits(8));
        assertEquals(4, BuildInOperators.getNumBits(16));
        assertEquals(5, BuildInOperators.getNumBits(32));
        assertEquals(6, BuildInOperators.getNumBits(64));
        assertEquals(7, BuildInOperators.getNumBits(128));
        assertEquals(8, BuildInOperators.getNumBits(256));
        assertEquals(9, BuildInOperators.getNumBits(512));
        assertEquals(10, BuildInOperators.getNumBits(1024));
        assertEquals(11, BuildInOperators.getNumBits(2048));
        assertEquals(12, BuildInOperators.getNumBits(4096));
        assertEquals(13, BuildInOperators.getNumBits(8192));
        assertEquals(14, BuildInOperators.getNumBits(16384));
        assertEquals(15, BuildInOperators.getNumBits(32768));
        assertEquals(16, BuildInOperators.getNumBits(65536));

        assertEquals(63, BuildInOperators.getNumBits(BigInteger.ONE.shiftLeft(63)));
        assertEquals(64, BuildInOperators.getNumBits(BigInteger.ONE.shiftLeft(63).add(BigInteger.ONE)));
    }
}
