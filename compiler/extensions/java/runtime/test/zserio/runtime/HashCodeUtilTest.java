package zserio.runtime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.array.Array;
import zserio.runtime.array.ArrayTraits;
import zserio.runtime.array.ArrayType;
import zserio.runtime.array.RawArray;
import zserio.runtime.array.PackingContextNode;

public class HashCodeUtilTest
{
    @Test
    public void simpleType()
    {
        final int hashSeed = 1;

        final boolean boolValue = true;
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER + 1, HashCodeUtil.calcHashCode(hashSeed, boolValue));

        final Boolean boolValueBoxed = Boolean.valueOf(false);
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, boolValueBoxed));
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (Boolean)null));

        final byte byteValue = 10;
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER + 10, HashCodeUtil.calcHashCode(hashSeed, byteValue));

        final Byte byteValueBoxed = Byte.valueOf((byte)10);
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER + 10, HashCodeUtil.calcHashCode(hashSeed, byteValueBoxed));
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (Byte)null));

        final short shortValue = 10;
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER + 10, HashCodeUtil.calcHashCode(hashSeed, shortValue));

        final Short shortValueBoxed = Short.valueOf((short)10);
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER + 10, HashCodeUtil.calcHashCode(hashSeed, shortValueBoxed));
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (Short)null));

        final int intValue = 10;
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER + 10, HashCodeUtil.calcHashCode(hashSeed, intValue));

        final Integer intValueBoxed = Integer.valueOf(10);
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER + 10, HashCodeUtil.calcHashCode(hashSeed, intValueBoxed));
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (Integer)null));

        final long longValue = 10;
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER + 10, HashCodeUtil.calcHashCode(hashSeed, longValue));

        final Long longValueBoxed = Long.valueOf(10);
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER + 10, HashCodeUtil.calcHashCode(hashSeed, longValueBoxed));
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (Long)null));

        final float floatValue = 10.0f;
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER + FloatUtil.convertFloatToInt(floatValue),
                HashCodeUtil.calcHashCode(hashSeed, floatValue));

        final Float floatValueBoxed = Float.valueOf(10.0f);
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER + FloatUtil.convertFloatToInt(floatValue),
                HashCodeUtil.calcHashCode(hashSeed, floatValueBoxed));
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (Float)null));

        final double doubleValue = 10.0;
        final long doubleValueAsLong = FloatUtil.convertDoubleToLong(doubleValue);
        final int expectedDoubleHashCode = HashCodeUtil.HASH_PRIME_NUMBER +
                (int)(doubleValueAsLong ^ (doubleValueAsLong >>> 32));
        assertEquals(expectedDoubleHashCode, HashCodeUtil.calcHashCode(hashSeed, doubleValue));

        final Double doubleValueBoxed = Double.valueOf(10.0);
        assertEquals(expectedDoubleHashCode, HashCodeUtil.calcHashCode(hashSeed, doubleValueBoxed));
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (Double)null));
    }

    @Test
    public void stringType()
    {
        final int hashSeed = 1;
        final String stringValue = String.valueOf('0');
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER + (int)'0',
                HashCodeUtil.calcHashCode(hashSeed, stringValue));

        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (String)null));
    }

    @Test
    public void bitBufferType()
    {
        final int hashSeed = 1;
        final BitBuffer bitBufferValue = new BitBuffer(new byte[] {});
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER + HashCodeUtil.HASH_SEED,
                HashCodeUtil.calcHashCode(hashSeed, bitBufferValue));

        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (BitBuffer)null));
    }

    @Test
    public void enumType()
    {
        final int hashSeed = 1;
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER +
                (HashCodeUtil.HASH_PRIME_NUMBER * HashCodeUtil.HASH_SEED + Color.NONE.getValue()),
                HashCodeUtil.calcHashCode(hashSeed, Color.NONE));
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER +
                (HashCodeUtil.HASH_PRIME_NUMBER * HashCodeUtil.HASH_SEED + Color.RED.getValue()),
                HashCodeUtil.calcHashCode(hashSeed, Color.RED));
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER +
                (HashCodeUtil.HASH_PRIME_NUMBER * HashCodeUtil.HASH_SEED + Color.BLUE.getValue()),
                HashCodeUtil.calcHashCode(hashSeed, Color.BLUE));
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER +
                (HashCodeUtil.HASH_PRIME_NUMBER * HashCodeUtil.HASH_SEED + Color.BLACK.getValue()),
                HashCodeUtil.calcHashCode(hashSeed, Color.BLACK));

        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (Color)null));
    }

    @Test
    public void bitmaskType()
    {
        final int hashSeed = 1;
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER +
                (HashCodeUtil.HASH_PRIME_NUMBER * HashCodeUtil.HASH_SEED + Permissions.Values.READ.getValue()),
                HashCodeUtil.calcHashCode(hashSeed, Permissions.Values.READ));
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER +
                (HashCodeUtil.HASH_PRIME_NUMBER * HashCodeUtil.HASH_SEED + Permissions.Values.WRITE.getValue()),
                HashCodeUtil.calcHashCode(hashSeed, Permissions.Values.WRITE));
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER +
                (HashCodeUtil.HASH_PRIME_NUMBER * HashCodeUtil.HASH_SEED +
                        Permissions.Values.CREATE.getValue()),
                HashCodeUtil.calcHashCode(hashSeed, Permissions.Values.CREATE));

        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (Permissions)null));
    }

    @Test
    public void objectType()
    {
        final int hashSeed = 1;
        final DummyObject objectValue = new DummyObject(10);
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER + 10, HashCodeUtil.calcHashCode(hashSeed, objectValue));

        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (DummyObject)null));
    }

    @Test
    public void arrayType()
    {
        final int hashSeed = 1;
        final Array arrayValue = new Array(
                new RawArray.IntRawArray(new int[] { 3, 7}),
                new ArrayTraits.SignedBitFieldIntArrayTraits(32),
                ArrayType.NORMAL);
        final int rawArrayHashCode = (HashCodeUtil.HASH_PRIME_NUMBER * HashCodeUtil.HASH_SEED + 3) *
                HashCodeUtil.HASH_PRIME_NUMBER + 7;
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER + rawArrayHashCode,
                HashCodeUtil.calcHashCode(hashSeed, arrayValue));

        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (Array)null));
    }

    @Test
    public void simpleArrayType()
    {
        final int hashSeed = 1;

        final int expectedBooleanHashCode = (HashCodeUtil.HASH_PRIME_NUMBER + 0) *
                HashCodeUtil.HASH_PRIME_NUMBER + 1;
        final boolean[] boolArrayValue = new boolean[] { false, true };
        assertEquals(expectedBooleanHashCode, HashCodeUtil.calcHashCode(hashSeed, boolArrayValue));
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (boolean[])null));

        final int expectedIntegralHashCode = (HashCodeUtil.HASH_PRIME_NUMBER + 3) *
                HashCodeUtil.HASH_PRIME_NUMBER + 7;

        final byte[] byteArrayValue = new byte[] { 3, 7 };
        assertEquals(expectedIntegralHashCode, HashCodeUtil.calcHashCode(hashSeed, byteArrayValue));
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (byte[])null));

        final short[] shortArrayValue = new short[] { 3, 7 };
        assertEquals(expectedIntegralHashCode, HashCodeUtil.calcHashCode(hashSeed, shortArrayValue));
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (short[])null));

        final int[] intArrayValue = new int[] { 3, 7 };
        assertEquals(expectedIntegralHashCode, HashCodeUtil.calcHashCode(hashSeed, intArrayValue));
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (int[])null));

        final long[] longArrayValue = new long[] { 3, 7 };
        assertEquals(expectedIntegralHashCode, HashCodeUtil.calcHashCode(hashSeed, longArrayValue));
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (long[])null));

        final float[] floatArrayValue = new float[] { 10.0f };
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER + FloatUtil.convertFloatToInt(floatArrayValue[0]),
                HashCodeUtil.calcHashCode(hashSeed, floatArrayValue));
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (float[])null));

        final double[] doubleArrayValue = new double[] { 10.0 };
        final long doubleValueAsLong = FloatUtil.convertDoubleToLong(doubleArrayValue[0]);
        final int expectedDoubleArrayHashCode = HashCodeUtil.HASH_PRIME_NUMBER +
                (int)(doubleValueAsLong ^ (doubleValueAsLong >>> 32));
        assertEquals(expectedDoubleArrayHashCode, HashCodeUtil.calcHashCode(hashSeed, doubleArrayValue));
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (double[])null));
    }

    @Test
    public void bigIntegerArrayType()
    {
        final int hashSeed = 1;
        final BigInteger[] arrayValue = new BigInteger[] { BigInteger.valueOf(3), BigInteger.valueOf(7) };
        final int expectedHashCode = (HashCodeUtil.HASH_PRIME_NUMBER + 3) * HashCodeUtil.HASH_PRIME_NUMBER + 7;
        assertEquals(expectedHashCode, HashCodeUtil.calcHashCode(hashSeed, arrayValue));

        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (BigInteger[])null));
    }

    @Test
    public void stringArrayType()
    {
        final int hashSeed = 1;
        final String[] arrayValue = new String[] { String.valueOf('0') };
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER + (int)'0',
                HashCodeUtil.calcHashCode(hashSeed, arrayValue));

        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (String[])null));
    }

    @Test
    public void bitBufferArrayType()
    {
        final int hashSeed = 1;
        final BitBuffer[] arrayValue = new BitBuffer[] { new BitBuffer(new byte[] {}) };
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER + HashCodeUtil.HASH_SEED,
                HashCodeUtil.calcHashCode(hashSeed, arrayValue));

        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (BitBuffer[])null));
    }

    @Test
    public void enumArrayType()
    {
        final int hashSeed = 1;
        final Color[] arrayValue = new Color[] { Color.NONE };
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER +
                (HashCodeUtil.HASH_PRIME_NUMBER * HashCodeUtil.HASH_SEED + Color.NONE.getValue()),
                HashCodeUtil.calcHashCode(hashSeed, arrayValue));

        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (Color[])null));
    }

    @Test
    public void bitmaskArrayType()
    {
        final int hashSeed = 1;
        final Permissions[] arrayValue = new Permissions[] { Permissions.Values.READ };
        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER +
                (HashCodeUtil.HASH_PRIME_NUMBER * HashCodeUtil.HASH_SEED + Permissions.Values.READ.getValue()),
                HashCodeUtil.calcHashCode(hashSeed, arrayValue));

        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (Permissions[])null));
    }

    @Test
    public void objectArrayType()
    {
        final int hashSeed = 1;
        final DummyObject[] arrayValue = new DummyObject[] { new DummyObject(3), new DummyObject(7) };
        assertEquals((HashCodeUtil.HASH_PRIME_NUMBER + 3) * HashCodeUtil.HASH_PRIME_NUMBER + 7,
                HashCodeUtil.calcHashCode(hashSeed, arrayValue));

        assertEquals(HashCodeUtil.HASH_PRIME_NUMBER, HashCodeUtil.calcHashCode(hashSeed, (DummyObject[])null));
    }

    // enum
    private static enum Color implements ZserioEnum, SizeOf
    {
        NONE(0),
        RED(2),
        BLUE(3),
        BLACK(7);

        private Color(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }

        @Override
        public Number getGenericValue()
        {
            return value;
        }

        @Override
        public void initPackingContext(PackingContextNode contextNode)
        {}

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

        @Override
        public int bitSizeOf(PackingContextNode contextNode, long position)
        {
            return 0;
        }

        private int value;
    }

    // bitmask
    private static class Permissions implements ZserioBitmask, SizeOf
    {
        public Permissions(int value)
        {
            this.value = value;
        }

        @Override
        public int hashCode()
        {
            int result = zserio.runtime.HashCodeUtil.HASH_SEED;
            result = HashCodeUtil.calcHashCode(result, value);
            return result;
        }

        public int getValue()
        {
            return value;
        }

        @Override
        public java.lang.Number getGenericValue()
        {
            return value;
        }

        @Override
        public void initPackingContext(PackingContextNode contextNode)
        {}

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

        @Override
        public int bitSizeOf(PackingContextNode contextNode, long position)
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

    private static class DummyObject implements SizeOf
    {
        public DummyObject(int hashCode)
        {
            this.hashCode = hashCode;
        }

        @Override
        public boolean equals(Object other)
        {
            if (!(other instanceof DummyObject))
                return false;

            return hashCode == ((DummyObject)other).hashCode;
        }
        @Override
        public int hashCode()
        {
            return hashCode;
        }

        @Override
        public void initPackingContext(PackingContextNode contextNode)
        {}

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

        @Override
        public int bitSizeOf(PackingContextNode contextNode, long position)
        {
            return 0;
        }

        private final int hashCode;
    }
}
