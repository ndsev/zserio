package zserio.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import zserio.runtime.array.Array;
import zserio.runtime.array.ArrayTraits;
import zserio.runtime.array.ArrayType;
import zserio.runtime.array.RawArray;
import zserio.runtime.io.BitBuffer;

public class CompareUtilTest
{
    @Test
    public void simpleType()
    {
        assertTrue(CompareUtil.compare(false, true) < 0);
        assertEquals(0, CompareUtil.compare(true, true));
        assertTrue(CompareUtil.compare(Boolean.TRUE, Boolean.FALSE) > 0);
        assertTrue(CompareUtil.compare((Boolean)null, Boolean.FALSE) < 0);

        assertTrue(CompareUtil.compare((byte)-1, (byte)1) < 0);
        assertEquals(0, CompareUtil.compare(Byte.valueOf((byte)1), Byte.valueOf((byte)1)));
        assertTrue(CompareUtil.compare(Byte.valueOf((byte)1), (Byte)null) > 0);

        assertTrue(CompareUtil.compare((short)-1, (short)1) < 0);
        assertTrue(CompareUtil.compare(Short.valueOf((short)2), Short.valueOf((short)1)) > 0);
        assertEquals(0, CompareUtil.compare((Short)null, (Short)null));

        assertTrue(CompareUtil.compare(-1, 1) < 0);
        assertTrue(CompareUtil.compare(Integer.valueOf(2), Integer.valueOf(1)) > 0);
        assertTrue(CompareUtil.compare((Integer)null, Integer.valueOf(0)) < 0);

        assertTrue(CompareUtil.compare(-1L, 1L) < 0);
        assertTrue(CompareUtil.compare(Long.valueOf(2), Long.valueOf(1)) > 0);
        assertTrue(CompareUtil.compare((Long)null, Long.valueOf(0)) < 0);

        assertTrue(CompareUtil.compare(BigInteger.ONE, BigInteger.TEN) < 0);
        assertEquals(0, CompareUtil.compare(BigInteger.TEN, BigInteger.valueOf(10)));
        assertTrue(CompareUtil.compare((BigInteger)null, BigInteger.ZERO) < 0);
    }

    @Test
    public void floatType()
    {
        assertTrue(CompareUtil.compare(1.0f, 2.0f) < 0);
        assertTrue(CompareUtil.compare(-0.0f, 0.0f) < 0);
        assertEquals(0, CompareUtil.compare(Float.NaN, Float.NaN));
        assertTrue(CompareUtil.compare(Float.POSITIVE_INFINITY, Float.NaN) < 0);
        assertTrue(CompareUtil.compare(Float.valueOf(2.0f), Float.valueOf(1.0f)) > 0);
        assertTrue(CompareUtil.compare((Float)null, Float.valueOf(0.0f)) < 0);

        assertTrue(CompareUtil.compare(1.0, 2.0) < 0);
        assertTrue(CompareUtil.compare(-0.0, 0.0) < 0);
        assertEquals(0, CompareUtil.compare(Double.NaN, Double.NaN));
        assertTrue(CompareUtil.compare(Double.valueOf(2.0), Double.valueOf(1.0)) > 0);
        assertTrue(CompareUtil.compare((Double)null, Double.valueOf(0.0)) < 0);
    }

    @Test
    public void stringType()
    {
        assertEquals(0, CompareUtil.compare("", ""));
        assertTrue(CompareUtil.compare("", "a") < 0);
        assertTrue(CompareUtil.compare("ab", "a") > 0);
        assertTrue(CompareUtil.compare("ab", "b") < 0);
        assertTrue(CompareUtil.compare((String)null, "") < 0);

        // U+FF61 is less than U+1F600 (surrogate pair) by code point, but greater by UTF-16 code unit
        final String bmpString = "｡";
        final String supplementaryString = "😀";
        assertTrue(bmpString.compareTo(supplementaryString) > 0);
        assertTrue(CompareUtil.compare(bmpString, supplementaryString) < 0);
        assertTrue(CompareUtil.compare(supplementaryString, bmpString) > 0);
        assertTrue(CompareUtil.compare("a" + supplementaryString, "a" + supplementaryString + "a") < 0);
    }

    @Test
    public void bytesType()
    {
        assertEquals(0, CompareUtil.compare(new byte[] {}, new byte[] {}));
        assertTrue(CompareUtil.compare(new byte[] {}, new byte[] {0}) < 0);
        assertTrue(CompareUtil.compare(new byte[] {1, 2}, new byte[] {1}) > 0);
        assertTrue(CompareUtil.compare(new byte[] {1, 2}, new byte[] {2}) < 0);
        // bytes are unsigned
        assertTrue(CompareUtil.compare(new byte[] {(byte)0x01}, new byte[] {(byte)0xFF}) < 0);
        assertTrue(CompareUtil.compare((byte[])null, new byte[] {}) < 0);
    }

    @Test
    public void bitBufferType()
    {
        final BitBuffer bitBuffer1 = new BitBuffer(new byte[] {(byte)0xAB, (byte)0xE0}, 11);
        final BitBuffer bitBuffer2 = new BitBuffer(new byte[] {(byte)0xAB, (byte)0xF0}, 11);
        final BitBuffer bitBuffer3 = new BitBuffer(new byte[] {(byte)0xAB, (byte)0x00}, 11);
        assertEquals(0, CompareUtil.compare(bitBuffer1, bitBuffer2));
        assertTrue(CompareUtil.compare(bitBuffer3, bitBuffer1) < 0);
        assertTrue(CompareUtil.compare((BitBuffer)null, bitBuffer1) < 0);
    }

    @Test
    public void enumType()
    {
        // compared by value, not by declaration order
        assertTrue(Color.BLUE.compareTo(Color.RED) > 0);
        assertTrue(CompareUtil.compare(Color.BLUE, Color.RED) < 0);
        assertTrue(CompareUtil.compare(Color.NONE, Color.BLACK) < 0);
        assertEquals(0, CompareUtil.compare(Color.RED, Color.RED));
        assertTrue(CompareUtil.compare((Color)null, Color.NONE) < 0);

        assertTrue(CompareUtil.compare(BigColor.BIG, BigColor.SMALL) > 0);
        assertEquals(0, CompareUtil.compare(BigColor.SMALL, BigColor.SMALL));
    }

    @Test
    public void objectType()
    {
        assertTrue(CompareUtil.compare(Permissions.Values.READ, Permissions.Values.WRITE) < 0);
        assertEquals(0, CompareUtil.compare(Permissions.Values.WRITE, new Permissions(2)));
        assertTrue(CompareUtil.compare(Permissions.Values.CREATE, Permissions.Values.WRITE) > 0);
        assertTrue(CompareUtil.compare((Permissions)null, Permissions.Values.READ) < 0);

        assertTrue(CompareUtil.compare(new DummyObject(3), new DummyObject(7)) < 0);
        assertTrue(CompareUtil.compare(new DummyObject(7), (DummyObject)null) > 0);
    }

    @Test
    public void arrayType()
    {
        final Array array1 = new Array(new RawArray.IntRawArray(new int[] {3, 7}),
                new ArrayTraits.SignedBitFieldIntArrayTraits(32), ArrayType.NORMAL);
        final Array array2 = new Array(new RawArray.IntRawArray(new int[] {3, 8}),
                new ArrayTraits.SignedBitFieldIntArrayTraits(32), ArrayType.NORMAL);
        final Array array3 = new Array(new RawArray.IntRawArray(new int[] {3, 7}),
                new ArrayTraits.SignedBitFieldIntArrayTraits(32), ArrayType.AUTO);
        assertTrue(CompareUtil.compare(array1, array2) < 0);
        assertEquals(0, CompareUtil.compare(array1, array3));
        assertTrue(CompareUtil.compare((Array)null, array1) < 0);
    }

    @Test
    public void simpleArrayType()
    {
        assertTrue(CompareUtil.compare(new boolean[] {true}, new boolean[] {true, false}) < 0);
        assertTrue(CompareUtil.compare(new boolean[] {true}, new boolean[] {false, false}) > 0);
        assertEquals(0, CompareUtil.compare(new boolean[] {true}, new boolean[] {true}));
        assertTrue(CompareUtil.compare((boolean[])null, new boolean[] {}) < 0);

        assertTrue(CompareUtil.compare(new short[] {-1}, new short[] {1}) < 0);
        assertTrue(CompareUtil.compare(new short[] {1, 2}, new short[] {1}) > 0);
        assertTrue(CompareUtil.compare((short[])null, new short[] {}) < 0);

        assertTrue(CompareUtil.compare(new int[] {-1}, new int[] {1}) < 0);
        assertTrue(CompareUtil.compare(new int[] {1, 2}, new int[] {1}) > 0);
        assertTrue(CompareUtil.compare((int[])null, new int[] {}) < 0);

        assertTrue(CompareUtil.compare(new long[] {-1}, new long[] {1}) < 0);
        assertTrue(CompareUtil.compare(new long[] {1, 2}, new long[] {1}) > 0);
        assertTrue(CompareUtil.compare((long[])null, new long[] {}) < 0);

        assertTrue(CompareUtil.compare(new float[] {-0.0f}, new float[] {0.0f}) < 0);
        assertTrue(CompareUtil.compare(new float[] {1.0f, 2.0f}, new float[] {1.0f}) > 0);
        assertTrue(CompareUtil.compare((float[])null, new float[] {}) < 0);

        assertTrue(CompareUtil.compare(new double[] {-0.0}, new double[] {0.0}) < 0);
        assertTrue(CompareUtil.compare(new double[] {1.0, 2.0}, new double[] {1.0}) > 0);
        assertTrue(CompareUtil.compare((double[])null, new double[] {}) < 0);

        assertTrue(
                CompareUtil.compare(new BigInteger[] {BigInteger.ONE}, new BigInteger[] {BigInteger.TEN}) < 0);
        assertTrue(CompareUtil.compare(new BigInteger[] {BigInteger.ONE, BigInteger.ONE},
                           new BigInteger[] {BigInteger.ONE}) > 0);
        assertTrue(CompareUtil.compare((BigInteger[])null, new BigInteger[] {}) < 0);
    }

    @Test
    public void objectArrayType()
    {
        assertTrue(CompareUtil.compare(new byte[][] {{(byte)0x01}}, new byte[][] {{(byte)0xFF}}) < 0);
        assertTrue(CompareUtil.compare(new byte[][] {{}, {}}, new byte[][] {{}}) > 0);
        assertTrue(CompareUtil.compare((byte[][])null, new byte[][] {}) < 0);

        assertTrue(CompareUtil.compare(new String[] {"｡"}, new String[] {"😀"}) < 0);
        assertTrue(CompareUtil.compare(new String[] {"a", "b"}, new String[] {"a"}) > 0);
        assertTrue(CompareUtil.compare((String[])null, new String[] {}) < 0);

        final BitBuffer bitBuffer1 = new BitBuffer(new byte[] {(byte)0xAB});
        final BitBuffer bitBuffer2 = new BitBuffer(new byte[] {(byte)0xAC});
        assertTrue(CompareUtil.compare(new BitBuffer[] {bitBuffer1}, new BitBuffer[] {bitBuffer2}) < 0);
        assertTrue(CompareUtil.compare(new BitBuffer[] {bitBuffer1, bitBuffer1}, new BitBuffer[] {bitBuffer1}) >
                0);
        assertTrue(CompareUtil.compare((BitBuffer[])null, new BitBuffer[] {}) < 0);

        assertTrue(CompareUtil.compare(new Color[] {Color.BLUE}, new Color[] {Color.RED}) < 0);
        assertTrue(CompareUtil.compare(new Color[] {Color.RED, Color.RED}, new Color[] {Color.RED}) > 0);
        assertTrue(CompareUtil.compare((Color[])null, new Color[] {}) < 0);

        assertTrue(CompareUtil.compare(
                           new DummyObject[] {new DummyObject(3)}, new DummyObject[] {new DummyObject(7)}) < 0);
        assertEquals(0,
                CompareUtil.compare(
                        new DummyObject[] {new DummyObject(3)}, new DummyObject[] {new DummyObject(3)}));
        assertTrue(CompareUtil.compare((DummyObject[])null, new DummyObject[] {}) < 0);
    }

    @Test
    public void compareObjects()
    {
        assertEquals(0, CompareUtil.compareObjects(null, null));
        assertTrue(CompareUtil.compareObjects(null, Integer.valueOf(0)) < 0);
        assertTrue(CompareUtil.compareObjects(Integer.valueOf(0), null) > 0);

        assertTrue(CompareUtil.compareObjects(Integer.valueOf(1), Integer.valueOf(2)) < 0);
        assertTrue(CompareUtil.compareObjects("｡", "😀") < 0);
        assertTrue(CompareUtil.compareObjects(new byte[] {(byte)0x01}, new byte[] {(byte)0xFF}) < 0);
        assertTrue(CompareUtil.compareObjects(Color.BLUE, Color.RED) < 0);
        assertTrue(CompareUtil.compareObjects(new DummyObject(3), new DummyObject(7)) < 0);

        // different classes are ordered by class names
        assertTrue(CompareUtil.compareObjects(Integer.valueOf(1), "a") < 0);
        assertTrue(CompareUtil.compareObjects("a", Integer.valueOf(1)) > 0);
    }

    // enum with values not in declaration order
    private static enum Color implements ZserioEnum, SizeOf {
        NONE(0),
        RED(7),
        BLUE(3),
        BLACK(9);

        private Color(int value)
        {
            this.value = value;
        }

        @Override
        public Number getGenericValue()
        {
            return value;
        }

        @Override
        public int bitSizeOf()
        {
            return 0;
        }

        @Override
        public int bitSizeOf(long position)
        {
            return 0;
        }

        private int value;
    }

    // enum with BigInteger values
    private static enum BigColor implements ZserioEnum, SizeOf {
        BIG(new BigInteger("18446744073709551615")),
        SMALL(BigInteger.ONE);

        private BigColor(BigInteger value)
        {
            this.value = value;
        }

        @Override
        public Number getGenericValue()
        {
            return value;
        }

        @Override
        public int bitSizeOf()
        {
            return 0;
        }

        @Override
        public int bitSizeOf(long position)
        {
            return 0;
        }

        private BigInteger value;
    }

    // bitmask
    private static class Permissions implements ZserioBitmask, SizeOf, Comparable<Permissions>
    {
        public Permissions(int value)
        {
            this.value = value;
        }

        @Override
        public boolean equals(Object other)
        {
            if (!(other instanceof Permissions))
                return false;

            return value == ((Permissions)other).value;
        }

        @Override
        public int hashCode()
        {
            return value;
        }

        @Override
        public int compareTo(Permissions other)
        {
            return CompareUtil.compare(value, other.value);
        }

        @Override
        public java.lang.Number getGenericValue()
        {
            return value;
        }

        @Override
        public int bitSizeOf()
        {
            return 0;
        }

        @Override
        public int bitSizeOf(long position)
        {
            return 0;
        }

        public static final class Values
        {
            public static final Permissions READ = new Permissions(1);
            public static final Permissions WRITE = new Permissions(2);
            public static final Permissions CREATE = new Permissions(4);
        }

        private int value;
    }

    private static class DummyObject implements SizeOf, Comparable<DummyObject>
    {
        public DummyObject(int value)
        {
            this.value = value;
        }

        @Override
        public boolean equals(Object other)
        {
            if (!(other instanceof DummyObject))
                return false;

            return value == ((DummyObject)other).value;
        }

        @Override
        public int hashCode()
        {
            return value;
        }

        @Override
        public int compareTo(DummyObject other)
        {
            return CompareUtil.compare(value, other.value);
        }

        @Override
        public int bitSizeOf()
        {
            return 0;
        }

        @Override
        public int bitSizeOf(long position)
        {
            return 0;
        }

        private final int value;
    }
}
