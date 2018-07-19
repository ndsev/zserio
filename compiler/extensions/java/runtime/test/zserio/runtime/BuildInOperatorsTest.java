package zserio.runtime;

import static org.junit.Assert.assertEquals;

import java.math.BigInteger;

import org.junit.Test;

public class BuildInOperatorsTest
{
    @Test
    public void getNumBits()
    {
        assertEquals(1, BuildInOperators.getNumBits(0));
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
