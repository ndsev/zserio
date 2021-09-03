package zserio.runtime.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;

import zserio.runtime.ZserioError;
import zserio.runtime.BitPositionUtil;
import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.SizeOf;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.InitializeOffsetsWriter;

public class ArrayTest
{
    @Test
    public void signedBitFieldByteArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.ByteRawArray(new byte[] {-1, -2});
        final int array1BitSizeOf = 2 * 5;
        final int array1AlignedBitSizeOf = 5 + 3 + 5;
        final RawArray rawArray2 = new RawArray.ByteRawArray(new byte[] {-3, -4});
        final RawArray emptyRawArray = new RawArray.ByteRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.SignedBitFieldByteArrayTraits(5);
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final byte[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void signedBitFieldShortArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.ShortRawArray(new short[] {-512, -513});
        final int array1BitSizeOf = 2 * 13;
        final int array1AlignedBitSizeOf = 13 + 3 + 13;
        final RawArray rawArray2 = new RawArray.ShortRawArray(new short[] {-514, -515});
        final RawArray emptyRawArray = new RawArray.ShortRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.SignedBitFieldShortArrayTraits(13);
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final short[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void signedBitFieldIntArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.IntRawArray(new int[] {-131072, -131073});
        final int array1BitSizeOf = 2 * 29;
        final int array1AlignedBitSizeOf = 29 + 3 + 29;
        final RawArray rawArray2 = new RawArray.IntRawArray(new int[] {-131074, -131075});
        final RawArray emptyRawArray = new RawArray.IntRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.SignedBitFieldIntArrayTraits(29);
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final int[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void signedBitFieldLongArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.LongRawArray(new long[] {-8589934592L, -8589934593L});
        final int array1BitSizeOf = 2 * 61;
        final int array1AlignedBitSizeOf = 61 + 3 + 61;
        final RawArray rawArray2 = new RawArray.LongRawArray(new long[] {-8589934594L, -8589934595L});
        final RawArray emptyRawArray = new RawArray.LongRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.SignedBitFieldLongArrayTraits(61);
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final long[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void bitFieldByteArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.ByteRawArray(new byte[] {1, 2});
        final int array1BitSizeOf = 2 * 5;
        final int array1AlignedBitSizeOf = 5 + 3 + 5;
        final RawArray rawArray2 = new RawArray.ByteRawArray(new byte[] {3, 4});
        final RawArray emptyRawArray = new RawArray.ByteRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.BitFieldByteArrayTraits(5);
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final byte[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void bitFieldShortArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.ShortRawArray(new short[] {512, 513});
        final int array1BitSizeOf = 2 * 13;
        final int array1AlignedBitSizeOf = 13 + 3 + 13;
        final RawArray rawArray2 = new RawArray.ShortRawArray(new short[] {514, 515});
        final RawArray emptyRawArray = new RawArray.ShortRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.BitFieldShortArrayTraits(13);
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final short[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void bitFieldIntArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.IntRawArray(new int[] {131072, 131073});
        final int array1BitSizeOf = 2 * 29;
        final int array1AlignedBitSizeOf = 29 + 3 + 29;
        final RawArray rawArray2 = new RawArray.IntRawArray(new int[] {131074, 131075});
        final RawArray emptyRawArray = new RawArray.IntRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.BitFieldIntArrayTraits(29);
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final int[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void bitFieldLongArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.LongRawArray(new long[] {8589934592L, 8589934593L});
        final int array1BitSizeOf = 2 * 61;
        final int array1AlignedBitSizeOf = 61 + 3 + 61;
        final RawArray rawArray2 = new RawArray.LongRawArray(new long[] {8589934594L, 8589934595L});
        final RawArray emptyRawArray = new RawArray.LongRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.BitFieldLongArrayTraits(61);
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final long[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void bitFieldBigIntegerArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.ObjectRawArray<BigInteger>(BigInteger.class, new BigInteger[] {
                BigInteger.valueOf(8589934592L), BigInteger.valueOf(8589934593L)});
        final int array1BitSizeOf = 2 * 64;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.ObjectRawArray<BigInteger>(BigInteger.class, new BigInteger[] {
                BigInteger.valueOf(8589934594L), BigInteger.valueOf(8589934595L)});
        final RawArray emptyRawArray = new RawArray.ObjectRawArray<BigInteger>(BigInteger.class);
        final ArrayTraits arrayTraits = new ArrayTraits.BitFieldBigIntegerArrayTraits();
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final BigInteger[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void varInt16Array() throws IOException
    {
        final RawArray rawArray1 = new RawArray.ShortRawArray(new short[] {-1, 1024});
        final int array1BitSizeOf = 8 + 16;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.ShortRawArray(new short[] {-1, 8192});
        final RawArray emptyRawArray = new RawArray.ShortRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.VarInt16ArrayTraits();
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final short[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void varInt32Array() throws IOException
    {
        final RawArray rawArray1 = new RawArray.IntRawArray(new int[] {-1, 16384});
        final int array1BitSizeOf = 8 + 24;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.IntRawArray(new int[] {-1, 32768});
        final RawArray emptyRawArray = new RawArray.IntRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.VarInt32ArrayTraits();
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final int[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void varInt64Array() throws IOException
    {
        final RawArray rawArray1 = new RawArray.LongRawArray(new long[] {-1, 1073741824L});
        final int array1BitSizeOf = 8 + 40;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.LongRawArray(new long[] {-1, 2147483648L});
        final RawArray emptyRawArray = new RawArray.LongRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.VarInt64ArrayTraits();
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final long[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void varIntArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.LongRawArray(new long[] {-1, 1073741824L});
        final int array1BitSizeOf = 8 + 40;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.LongRawArray(new long[] {-1, 2147483648L});
        final RawArray emptyRawArray = new RawArray.LongRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.VarIntArrayTraits();
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final long[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void varUInt16Array() throws IOException
    {
        final RawArray rawArray1 = new RawArray.ShortRawArray(new short[] {1, 1024});
        final int array1BitSizeOf = 8 + 16;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.ShortRawArray(new short[] {1, 8192});
        final RawArray emptyRawArray = new RawArray.ShortRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.VarUInt16ArrayTraits();
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final short[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void varUInt32Array() throws IOException
    {
        final RawArray rawArray1 = new RawArray.IntRawArray(new int[] {1, 16384});
        final int array1BitSizeOf = 8 + 24;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.IntRawArray(new int[] {1, 32768});
        final RawArray emptyRawArray = new RawArray.IntRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.VarUInt32ArrayTraits();
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final int[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void varUInt64Array() throws IOException
    {
        final RawArray rawArray1 = new RawArray.LongRawArray(new long[] {1, 1073741824L});
        final int array1BitSizeOf = 8 + 40;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.LongRawArray(new long[] {1, 2147483648L});
        final RawArray emptyRawArray = new RawArray.LongRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.VarUInt64ArrayTraits();
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final long[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void varUIntArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.ObjectRawArray<BigInteger>(BigInteger.class, new BigInteger[] {
                BigInteger.valueOf(1), BigInteger.valueOf(1073741824L)});
        final int array1BitSizeOf = 8 + 40;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.ObjectRawArray<BigInteger>(BigInteger.class, new BigInteger[] {
                BigInteger.valueOf(1), BigInteger.valueOf(2147483648L)});
        final RawArray emptyRawArray = new RawArray.ObjectRawArray<BigInteger>(BigInteger.class);
        final ArrayTraits arrayTraits = new ArrayTraits.VarUIntArrayTraits();
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final BigInteger[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void varSizeArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.IntRawArray(new int[] {1, 16384});
        final int array1BitSizeOf = 8 + 24;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.IntRawArray(new int[] {1, 32768});
        final RawArray emptyRawArray = new RawArray.IntRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.VarSizeArrayTraits();
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final int[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void float16Array() throws IOException
    {
        final RawArray rawArray1 = new RawArray.FloatRawArray(new float[] {-1.0f, 1.0f});
        final int array1BitSizeOf = 2 * 16;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.FloatRawArray(new float[] {-3.5f, 3.5f});
        final RawArray emptyRawArray = new RawArray.FloatRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.Float16ArrayTraits();
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final float[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray(), Float.MIN_VALUE);
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void float32Array() throws IOException
    {
        final RawArray rawArray1 = new RawArray.FloatRawArray(new float[] {-1.0f, 1.0f});
        final int array1BitSizeOf = 2 * 32;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.FloatRawArray(new float[] {-3.5f, 3.5f});
        final RawArray emptyRawArray = new RawArray.FloatRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.Float32ArrayTraits();
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final float[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray(), Float.MIN_VALUE);
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void float64Array() throws IOException
    {
        final RawArray rawArray1 = new RawArray.DoubleRawArray(new double[] {-1.0, 1.0});
        final int array1BitSizeOf = 2 * 64;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.DoubleRawArray(new double[] {-3.5, 3.5});
        final RawArray emptyRawArray = new RawArray.DoubleRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.Float64ArrayTraits();
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final double[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray(), Double.MIN_VALUE);
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void stringArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.ObjectRawArray<String>(String.class, new String[] {
                "Text1", "Text2"});
        final int array1BitSizeOf = 2 * (1 + 5) * 8;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.ObjectRawArray<String>(String.class, new String[] {
                "Text1", "Text3"});
        final RawArray emptyRawArray = new RawArray.ObjectRawArray<String>(String.class);
        final ArrayTraits arrayTraits = new ArrayTraits.StringArrayTraits();
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final String[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void boolArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.BooleanRawArray(new boolean[] {true, false});
        final int array1BitSizeOf = 2 * 1;
        final int array1AlignedBitSizeOf = 1 + 7 + 1;
        final RawArray rawArray2 = new RawArray.BooleanRawArray(new boolean[] {true, true});
        final RawArray emptyRawArray = new RawArray.BooleanRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.BoolArrayTraits();
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final boolean[] expectedRawArray = rawArray1.getRawArray();
        assertEquals(expectedRawArray.length, normalArray1.size());
        final boolean[] normalRawArray = normalArray1.getRawArray();
        assertEquals(expectedRawArray.length, normalRawArray.length);
        for (int i = 0; i < expectedRawArray.length; ++i)
            assertEquals(expectedRawArray[i], normalRawArray[i]);
    }

    @Test
    public void bitBufferArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.ObjectRawArray<BitBuffer>(BitBuffer.class, new BitBuffer[] {
                new BitBuffer(new byte[] {(byte)0xAB, (byte)0xE0}, 11),
                new BitBuffer(new byte[] {(byte)0xAB, (byte)0xCD, (byte)0xFE}, 23)});
        final int array1BitSizeOf = 8 + 11 + 8 + 23;
        final int array1AlignedBitSizeOf = 8 + 11 + 5 + 8 + 23;
        final RawArray rawArray2 = new RawArray.ObjectRawArray<BitBuffer>(BitBuffer.class, new BitBuffer[] {
                new BitBuffer(new byte[] {(byte)0xBA, (byte)0xE0}, 11),
                new BitBuffer(new byte[] {(byte)0xBA, (byte)0xCD, (byte)0xFE}, 23)});
        final RawArray emptyRawArray = new RawArray.ObjectRawArray<BitBuffer>(BitBuffer.class);
        final ArrayTraits arrayTraits = new ArrayTraits.BitBufferArrayTraits();
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final BitBuffer[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void writeObjectArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.ObjectRawArray<ArrayTestObject>(ArrayTestObject.class,
                new ArrayTestObject[] {new ArrayTestObject((byte)1), new ArrayTestObject((byte)2)});
        final int array1BitSizeOf = 2 * 3;
        final int array1AlignedBitSizeOf = 3 + 5 + 3;
        final RawArray rawArray2 = new RawArray.ObjectRawArray<ArrayTestObject>(ArrayTestObject.class,
                new ArrayTestObject[] {new ArrayTestObject((byte)1), new ArrayTestObject((byte)3)});
        final RawArray emptyRawArray = new RawArray.ObjectRawArray<ArrayTestObject>(ArrayTestObject.class);
        final ArrayTraits arrayTraits = new ArrayTraits.WriteObjectArrayTraits<ArrayTestObject>(
                new ArrayTestElementFactory());
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final ArrayTestObject[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    private static class ArrayTestObject implements InitializeOffsetsWriter, SizeOf
    {
        public ArrayTestObject(zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException, zserio.runtime.ZserioError
        {
            read(in);
        }

        public ArrayTestObject(byte value)
        {
            setValue(value);
        }

        @Override
        public int bitSizeOf()
        {
            return bitSizeOf(0);
        }

        @Override
        public int bitSizeOf(long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += 3;

            return (int)(endBitPosition - bitPosition);
        }

        public void setValue(byte value)
        {
            this.value = value;
        }

        @Override
        public boolean equals(java.lang.Object obj)
        {
            if (obj instanceof ArrayTestObject)
            {
                final ArrayTestObject that = (ArrayTestObject)obj;

                return value == that.value;
            }

            return false;
        }

        @Override
        public int hashCode()
        {
            int result = zserio.runtime.Util.HASH_SEED;

            result = zserio.runtime.Util.HASH_PRIME_NUMBER * result + value;

            return result;
        }

        public void read(final zserio.runtime.io.BitStreamReader in)
                throws java.io.IOException, zserio.runtime.ZserioError
        {
            value = (byte)in.readBits(3);
        }

        @Override
        public long initializeOffsets(long bitPosition)
        {
            long endBitPosition = bitPosition;

            endBitPosition += 3;

            return endBitPosition;
        }

        @Override
        public void write(zserio.runtime.io.BitStreamWriter out)
                throws java.io.IOException, zserio.runtime.ZserioError
        {
            write(out, true);
        }

        @Override
        public void write(zserio.runtime.io.BitStreamWriter out, boolean callInitializeOffsets)
                throws java.io.IOException, zserio.runtime.ZserioError
        {
            out.writeBits(value, 3);
        }

        private byte value;
    }

    private static class ArrayTestElementFactory implements ElementFactory<ArrayTestObject>
    {
        @Override
        public ArrayTestObject create(BitStreamReader reader, int index) throws IOException, ZserioError
        {
            return new ArrayTestObject(reader);
        }
    }

    private static class ArrayTestOffsetChecker implements OffsetChecker
    {
        @Override
        public void checkOffset(int index, long byteOffset) throws ZserioError
        {
        }
    };

    private static class ArrayTestOffsetInitializer implements OffsetInitializer
    {
        @Override
        public void setOffset(int index, long byteOffset)
        {
        }
    };

    private static void testArray(RawArray rawArray1, int array1BitSizeOf, int array1AlignedBitSizeOf,
            RawArray rawArray2, RawArray emptyRawArray, ArrayTraits arrayTraits) throws IOException
    {
        testArrayEquals(rawArray1, rawArray2, arrayTraits);
        testArrayHashCode(rawArray1, rawArray2, arrayTraits);
        testArrayZserioMethods(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, emptyRawArray, arrayTraits);
    }

    private static void testArrayEquals(RawArray rawArray1, RawArray rawArray2, ArrayTraits arrayTraits)
    {
        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final Array autoArray1 = new Array(rawArray1, arrayTraits, ArrayType.AUTO);
        final Array implicitArray1 = new Array(rawArray1, arrayTraits, ArrayType.IMPLICIT);
        final Array alignedNormalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL,
                new ArrayTestOffsetChecker(), new ArrayTestOffsetInitializer());
        final Array normalArray2 = new Array(rawArray2, arrayTraits, ArrayType.NORMAL);
        final Array autoArray2 = new Array(rawArray2, arrayTraits, ArrayType.AUTO);

        assertTrue(normalArray1.equals(autoArray1));
        assertTrue(normalArray1.equals(implicitArray1));
        assertTrue(normalArray1.equals(alignedNormalArray1));
        assertFalse(normalArray1.equals(normalArray2));
        assertFalse(normalArray1.equals(autoArray2));
    }

    private static void testArrayHashCode(RawArray rawArray1, RawArray rawArray2, ArrayTraits arrayTraits)
    {
        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final Array autoArray1 = new Array(rawArray1, arrayTraits, ArrayType.AUTO);
        final Array implicitArray1 = new Array(rawArray1, arrayTraits, ArrayType.IMPLICIT);
        final Array alignedNormalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL,
                new ArrayTestOffsetChecker(), new ArrayTestOffsetInitializer());
        final Array normalArray2 = new Array(rawArray2, arrayTraits, ArrayType.NORMAL);
        final Array autoArray2 = new Array(rawArray2, arrayTraits, ArrayType.AUTO);

        assertEquals(normalArray1.hashCode(), autoArray1.hashCode());
        assertEquals(normalArray1.hashCode(), implicitArray1.hashCode());
        assertEquals(normalArray1.hashCode(), alignedNormalArray1.hashCode());
        assertFalse(normalArray1.hashCode() == normalArray2.hashCode());
        assertFalse(normalArray1.hashCode() == autoArray2.hashCode());
    }

    private static void testArrayZserioMethods(RawArray rawArray1, int array1BitSizeOf,
            int array1AlignedBitSizeOf, RawArray emptyRawArray, ArrayTraits arrayTraits) throws IOException
    {
        testArrayNormalZserioMethods(rawArray1, emptyRawArray, arrayTraits, array1BitSizeOf);
        final int autoArray1BitSizeOf = BitSizeOfCalculator.getBitSizeOfVarSize(rawArray1.size());
        testArrayAutoZserioMethods(rawArray1, emptyRawArray, arrayTraits,
                autoArray1BitSizeOf + array1BitSizeOf);
        if (arrayTraits.isBitSizeOfConstant())
            testArrayImplicitZserioMethods(rawArray1, emptyRawArray, arrayTraits, array1BitSizeOf);

        final OffsetChecker offsetChecker = new ArrayTestOffsetChecker();
        final OffsetInitializer offsetInitializer = new ArrayTestOffsetInitializer();
        testArrayAlignedZserioMethods(rawArray1, emptyRawArray, arrayTraits, offsetChecker,
                offsetInitializer, array1AlignedBitSizeOf);
        testArrayAlignedAutoZserioMethods(rawArray1, emptyRawArray, arrayTraits, offsetChecker,
                offsetInitializer, autoArray1BitSizeOf + array1AlignedBitSizeOf);
    }

    private static void testArrayNormalZserioMethods(RawArray rawArray, RawArray emptyRawArray,
            ArrayTraits arrayTraits, int expectedBitSizeOf) throws IOException
    {
        final Array array = new Array(rawArray, arrayTraits, ArrayType.NORMAL);
        for (int bitPosition = 0; bitPosition < 8; ++bitPosition)
        {
            assertEquals(expectedBitSizeOf, array.bitSizeOf(bitPosition));
            assertEquals(bitPosition + expectedBitSizeOf, array.initializeOffsets(bitPosition));

            final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
            if (bitPosition > 0)
                writer.writeBits(0, bitPosition);
            array.write(writer);
            final long writtenBitPosition = writer.getBitPosition();
            assertEquals(bitPosition + expectedBitSizeOf, writtenBitPosition);
            final byte[] writtenByteArray = writer.toByteArray();
            writer.close();

            final BitBuffer readerBuffer = new BitBuffer(writtenByteArray, writtenBitPosition);
            final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(readerBuffer);
            if (bitPosition > 0)
                assertEquals(0, reader.readBits(bitPosition));
            final Array readArray = new Array(reader, rawArray.size(), emptyRawArray, arrayTraits,
                    ArrayType.NORMAL);
            assertEquals(array, readArray);
        }
    }

    private static void testArrayAutoZserioMethods(RawArray rawArray, RawArray emptyRawArray,
            ArrayTraits arrayTraits, int expectedBitSizeOf) throws IOException
    {
        final Array array = new Array(rawArray, arrayTraits, ArrayType.AUTO);
        for (int bitPosition = 0; bitPosition < 8; ++bitPosition)
        {
            assertEquals(expectedBitSizeOf, array.bitSizeOf(bitPosition));
            assertEquals(bitPosition + expectedBitSizeOf, array.initializeOffsets(bitPosition));

            final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
            if (bitPosition > 0)
                writer.writeBits(0, bitPosition);
            array.write(writer);
            final long writtenBitPosition = writer.getBitPosition();
            assertEquals(bitPosition + expectedBitSizeOf, writtenBitPosition);
            final byte[] writtenByteArray = writer.toByteArray();
            writer.close();

            final BitBuffer readerBuffer = new BitBuffer(writtenByteArray, writtenBitPosition);
            final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(readerBuffer);
            if (bitPosition > 0)
                assertEquals(0, reader.readBits(bitPosition));
            final Array readArray = new Array(reader, emptyRawArray, arrayTraits, ArrayType.AUTO);
            assertEquals(array, readArray);
        }
    }

    private static void testArrayImplicitZserioMethods(RawArray rawArray, RawArray emptyRawArray,
            ArrayTraits arrayTraits, int expectedBitSizeOf) throws IOException
    {
        final Array array = new Array(rawArray, arrayTraits, ArrayType.IMPLICIT);
        for (int bitPosition = 0; bitPosition < 8; ++bitPosition)
        {
            assertEquals(expectedBitSizeOf, array.bitSizeOf(bitPosition));
            assertEquals(bitPosition + expectedBitSizeOf, array.initializeOffsets(bitPosition));

            final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
            if (bitPosition > 0)
                writer.writeBits(0, bitPosition);
            array.write(writer);
            final long writtenBitPosition = writer.getBitPosition();
            assertEquals(bitPosition + expectedBitSizeOf, writtenBitPosition);
            final byte[] writtenByteArray = writer.toByteArray();
            writer.close();

            final BitBuffer readerBuffer = new BitBuffer(writtenByteArray, writtenBitPosition);
            final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(readerBuffer);
            if (bitPosition > 0)
                assertEquals(0, reader.readBits(bitPosition));
            final Array readArray = new Array(reader, emptyRawArray, arrayTraits, ArrayType.IMPLICIT);
            assertEquals(array, readArray);
        }
    }

    private static void testArrayAlignedZserioMethods(RawArray rawArray, RawArray emptyRawArray,
            ArrayTraits arrayTraits, OffsetChecker offsetChecker, OffsetInitializer offsetInitializer,
            int expectedBitSizeOf) throws IOException
    {
        final Array array = new Array(rawArray, arrayTraits, ArrayType.NORMAL, offsetChecker,
                offsetInitializer);
        for (int bitPosition = 0; bitPosition < 8; ++bitPosition)
        {
            final int alignedExpectedBitSizeOf = expectedBitSizeOf +
                    (int)BitPositionUtil.alignTo(8, bitPosition) - bitPosition;
            assertEquals(alignedExpectedBitSizeOf, array.bitSizeOf(bitPosition));
            assertEquals(bitPosition + alignedExpectedBitSizeOf, array.initializeOffsets(bitPosition));

            final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
            if (bitPosition > 0)
                writer.writeBits(0, bitPosition);
            array.write(writer);
            final long writtenBitPosition = writer.getBitPosition();
            assertEquals(bitPosition + alignedExpectedBitSizeOf, writtenBitPosition);
            final byte[] writtenByteArray = writer.toByteArray();
            writer.close();

            final BitBuffer readerBuffer = new BitBuffer(writtenByteArray, writtenBitPosition);
            final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(readerBuffer);
            if (bitPosition > 0)
                assertEquals(0, reader.readBits(bitPosition));
            final Array readArray = new Array(reader, rawArray.size(), emptyRawArray, arrayTraits,
                    ArrayType.NORMAL, offsetChecker, offsetInitializer);
            assertEquals(array, readArray);
        }
    }

    private static void testArrayAlignedAutoZserioMethods(RawArray rawArray, RawArray emptyRawArray,
            ArrayTraits arrayTraits, OffsetChecker offsetChecker, OffsetInitializer offsetInitializer,
            int expectedBitSizeOf) throws IOException
    {
        final Array array = new Array(rawArray, arrayTraits, ArrayType.AUTO, offsetChecker, offsetInitializer);
        for (int bitPosition = 0; bitPosition < 8; ++bitPosition)
        {
            final int alignedExpectedBitSizeOf = expectedBitSizeOf +
                    (int)BitPositionUtil.alignTo(8, bitPosition) - bitPosition;
            assertEquals(alignedExpectedBitSizeOf, array.bitSizeOf(bitPosition));
            assertEquals(bitPosition + alignedExpectedBitSizeOf, array.initializeOffsets(bitPosition));

            final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
            if (bitPosition > 0)
                writer.writeBits(0, bitPosition);
            array.write(writer);
            final long writtenBitPosition = writer.getBitPosition();
            assertEquals(bitPosition + alignedExpectedBitSizeOf, writtenBitPosition);
            final byte[] writtenByteArray = writer.toByteArray();
            writer.close();

            final BitBuffer readerBuffer = new BitBuffer(writtenByteArray, writtenBitPosition);
            final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(readerBuffer);
            if (bitPosition > 0)
                assertEquals(0, reader.readBits(bitPosition));
            final Array readArray = new Array(reader, emptyRawArray, arrayTraits, ArrayType.AUTO, offsetChecker,
                        offsetInitializer);
            assertEquals(array, readArray);
        }
    }
}
