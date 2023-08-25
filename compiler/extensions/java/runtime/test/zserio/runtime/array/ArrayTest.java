package zserio.runtime.array;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;

import zserio.runtime.BitPositionUtil;
import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;

import test_object.ArrayBitmask;
import test_object.ArrayEnum;
import test_object.ArrayObject;

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

        // empty array
        final RawArray emptyRawArray1 = new RawArray.ByteRawArray(new byte[] {});
        testArray(emptyRawArray1, 0, 0, rawArray2, emptyRawArray, arrayTraits);
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
        final RawArray rawArray1 = new RawArray.BigIntegerRawArray(new BigInteger[] {
                BigInteger.valueOf(8589934592L), BigInteger.valueOf(8589934593L)});
        final int array1BitSizeOf = 2 * 64;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.BigIntegerRawArray(new BigInteger[] {
                BigInteger.valueOf(8589934594L), BigInteger.valueOf(8589934595L)});
        final RawArray emptyRawArray = new RawArray.BigIntegerRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.BitFieldBigIntegerArrayTraits(64);
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
        final RawArray rawArray1 = new RawArray.BigIntegerRawArray(new BigInteger[] {
                BigInteger.valueOf(1), BigInteger.valueOf(1073741824L)});
        final int array1BitSizeOf = 8 + 40;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.BigIntegerRawArray(new BigInteger[] {
                BigInteger.valueOf(1), BigInteger.valueOf(2147483648L)});
        final RawArray emptyRawArray = new RawArray.BigIntegerRawArray();
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
    public void bytesArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.BytesRawArray(
                new byte[][] { new byte[] {(byte)1, (byte)255}, new byte[] {(byte)127, (byte)128} });
        final int array1BitSizeOf = 2 * (1 + 2) * 8;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.BytesRawArray(
                new byte[][] { new byte[] {(byte)0, (byte)0}, new byte[] {(byte)255, (byte)255} });
        final RawArray emptyRawArray = new RawArray.BytesRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.BytesArrayTraits();
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final byte[][] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void stringArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.StringRawArray(new String[] { "Text1", "Text2"});
        final int array1BitSizeOf = 2 * (1 + 5) * 8;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.StringRawArray(new String[] { "Text1", "Text3"});
        final RawArray emptyRawArray = new RawArray.StringRawArray();
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
        final RawArray rawArray1 = new RawArray.BitBufferRawArray(new BitBuffer[] {
                new BitBuffer(new byte[] {(byte)0xAB, (byte)0xE0}, 11),
                new BitBuffer(new byte[] {(byte)0xAB, (byte)0xCD, (byte)0xFE}, 23)});
        final int array1BitSizeOf = 8 + 11 + 8 + 23;
        final int array1AlignedBitSizeOf = 8 + 11 + 5 + 8 + 23;
        final RawArray rawArray2 = new RawArray.BitBufferRawArray(new BitBuffer[] {
                new BitBuffer(new byte[] {(byte)0xBA, (byte)0xE0}, 11),
                new BitBuffer(new byte[] {(byte)0xBA, (byte)0xCD, (byte)0xFE}, 23)});
        final RawArray emptyRawArray = new RawArray.BitBufferRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.BitBufferArrayTraits();
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final BitBuffer[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void bitmaskArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.ObjectRawArray<ArrayBitmask>(ArrayBitmask.class,
                new ArrayBitmask[] {ArrayBitmask.Values.CREATE, ArrayBitmask.Values.READ});
        final int array1BitSizeOf = 2 * 8;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.ObjectRawArray<ArrayBitmask>(ArrayBitmask.class,
                new ArrayBitmask[] {ArrayBitmask.Values.CREATE, ArrayBitmask.Values.WRITE});
        final RawArray emptyRawArray = new RawArray.ObjectRawArray<ArrayBitmask>(ArrayBitmask.class);
        final ArrayTraits arrayTraits = new ArrayTraits.WritePackableObjectArrayTraits<ArrayBitmask>(
                new PackableArrayBitmaskElementFactory());
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final ArrayBitmask[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void enumArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.EnumRawArray<ArrayEnum>(ArrayEnum.class,
                new ArrayEnum[] {ArrayEnum.VALUE1, ArrayEnum.VALUE2});
        final int array1BitSizeOf = 2 * 8;
        final int array1AlignedBitSizeOf = array1BitSizeOf;
        final RawArray rawArray2 = new RawArray.EnumRawArray<ArrayEnum>(ArrayEnum.class,
                new ArrayEnum[] {ArrayEnum.VALUE1, ArrayEnum.VALUE3});
        final RawArray emptyRawArray = new RawArray.EnumRawArray<ArrayEnum>(ArrayEnum.class);
        final ArrayTraits arrayTraits = new ArrayTraits.WritePackableObjectArrayTraits<ArrayEnum>(
                new PackableArrayEnumElementFactory());
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final ArrayEnum[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());
    }

    @Test
    public void writeObjectArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.ObjectRawArray<ArrayObject>(ArrayObject.class,
                new ArrayObject[] {new ArrayObject((byte)1), new ArrayObject((byte)2)});
        final int array1BitSizeOf = 2 * 3;
        final int array1AlignedBitSizeOf = 3 + 5 + 3;
        final RawArray rawArray2 = new RawArray.ObjectRawArray<ArrayObject>(ArrayObject.class,
                new ArrayObject[] {new ArrayObject((byte)1), new ArrayObject((byte)3)});
        final RawArray emptyRawArray = new RawArray.ObjectRawArray<ArrayObject>(ArrayObject.class);
        final ArrayTraits arrayTraits = new ArrayTraits.WriteObjectArrayTraits<ArrayObject>(
                new PackableArrayObjectElementFactory());
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, rawArray2, emptyRawArray, arrayTraits);

        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final ArrayObject[] expectedRawArray = rawArray1.getRawArray();
        assertArrayEquals(expectedRawArray, normalArray1.getRawArray());
        assertEquals(expectedRawArray.length, normalArray1.size());

        // check that the array is not packable
        assertThrows(UnsupportedOperationException.class, () -> testPackedArray(rawArray1, emptyRawArray,
                arrayTraits, array1BitSizeOf, array1AlignedBitSizeOf));
    }

    @Test
    public void signedBitFieldBytePackedArray() throws IOException
    {
        final int elementBitSize = 5;
        final RawArray emptyRawArray = new RawArray.ByteRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.SignedBitFieldByteArrayTraits(elementBitSize);

        // none-zero delta
        final RawArray rawArray1 = new RawArray.ByteRawArray(new byte[] {
                -10, -8, -6, -7, -4, -1, 0, 2, 4, 6, 9});
        final int array1MaxDeltaBitSize = 2;
        final int array1BitSizeOf = calcPackedBitSize(elementBitSize, rawArray1.size(), array1MaxDeltaBitSize);
        final int array1AlignedBitSizeOf = calcAlignedPackedBitSize(elementBitSize, rawArray1.size(),
                array1MaxDeltaBitSize);
        testPackedArray(rawArray1, emptyRawArray, arrayTraits, array1BitSizeOf, array1AlignedBitSizeOf);

        // zero delta
        final RawArray rawArray2 = new RawArray.ByteRawArray(new byte[] {-10, -10, -10, -10, -10, -10, -10});
        final int array2BitSizeOf = PACKING_DESCRIPTOR_BITSIZE + elementBitSize;
        final int array2AlignedBitSizeOf = PACKING_DESCRIPTOR_BITSIZE + elementBitSize + /* alignment */ 4;
        testPackedArray(rawArray2, emptyRawArray, arrayTraits, array2BitSizeOf, array2AlignedBitSizeOf);

        // one-element array
        final RawArray rawArray3 = new RawArray.ByteRawArray(new byte[] {-10});
        final int array3BitSizeOf = 1 + elementBitSize;
        final int array3AlignedBitSizeOf = 1 + elementBitSize;
        testPackedArray(rawArray3, emptyRawArray, arrayTraits, array3BitSizeOf, array3AlignedBitSizeOf);

        // empty array
        final RawArray rawArray4 = new RawArray.ByteRawArray(new byte[] {});
        final int array4BitSizeOf = 0;
        final int array4AlignedBitSizeOf = 0;
        testPackedArray(rawArray4, emptyRawArray, arrayTraits, array4BitSizeOf, array4AlignedBitSizeOf);
    }

    @Test
    public void signedBitFieldShortPackedArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.ShortRawArray(new short[] {
                -512, -481, -422, -389, -350, -300, -267, -250});
        final RawArray emptyRawArray = new RawArray.ShortRawArray();
        final ArrayTraits arrayTraits13 = new ArrayTraits.SignedBitFieldShortArrayTraits(13);
        testPackedArray(rawArray1, emptyRawArray, arrayTraits13);

        // will not be packed because unpacked 16bit values will be more efficient
        // (6 bits more are needed to store max_bit_number in descriptor if packing was enabled)
        final RawArray rawArray2 = new RawArray.ShortRawArray(new short[] {
                Short.MIN_VALUE, -1, 10, 20, 30, 40}); // max_bit_number 15, delta needs 16 bits
        final ArrayTraits arrayTraits16 = new ArrayTraits.SignedBitFieldShortArrayTraits(16);
        final int unpackedBitSizeOf = 1 + 6 * 16;
        final int unpackedAlignedBitSizeOf = 1 + 16 + 7 + 5 * 16;
        testPackedArray(rawArray2, emptyRawArray, arrayTraits16, unpackedBitSizeOf, unpackedAlignedBitSizeOf);
    }

    @Test
    public void signedBitFieldIntPackedArray() throws IOException
    {
        final RawArray rawArray = new RawArray.IntRawArray(new int[] {-131072, -131000, -130000, -121111,
                -112345, -109873});
        final RawArray emptyRawArray = new RawArray.IntRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.SignedBitFieldIntArrayTraits(29);
        testPackedArray(rawArray, emptyRawArray, arrayTraits);
    }

    @Test
    public void signedBitFieldLongPackedArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.LongRawArray(new long[] {-8589934592L, -8589934500L,
                -8589934000L, -8589933592L, -8589933000L, -8589931234L});
        final RawArray emptyRawArray = new RawArray.LongRawArray();
        final ArrayTraits arrayTraits1 = new ArrayTraits.SignedBitFieldLongArrayTraits(61);
        testPackedArray(rawArray1, emptyRawArray, arrayTraits1);

        // packing not enabled, delta is too big
        final RawArray rawArray2 = new RawArray.LongRawArray(new long[] {Long.MIN_VALUE, Long.MAX_VALUE});
        final ArrayTraits arrayTraits2 = new ArrayTraits.SignedBitFieldLongArrayTraits(64);
        testPackedArray(rawArray2, emptyRawArray, arrayTraits2);

        // packing not enabled, delta is too big
        final RawArray rawArray3 = new RawArray.LongRawArray(new long[] {Long.MIN_VALUE, 0, Long.MAX_VALUE});
        final ArrayTraits arrayTraits3 = new ArrayTraits.SignedBitFieldLongArrayTraits(64);
        testPackedArray(rawArray3, emptyRawArray, arrayTraits3);
    }

    @Test
    public void bitFieldBytePackedArray() throws IOException
    {
        final int elementBitSize = 5;
        final RawArray emptyRawArray = new RawArray.ByteRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.BitFieldByteArrayTraits(5);

        // none-zero delta
        final RawArray rawArray1 = new RawArray.ByteRawArray(new byte[] {
                1, 2, 5, 4, 7, 10, 12, 15, 18, 20, 22, 23});
        final int array1MaxDeltaBitSize = 2;
        final int array1BitSizeOf = calcPackedBitSize(elementBitSize, rawArray1.size(), array1MaxDeltaBitSize);
        final int array1AlignedBitSizeOf = calcAlignedPackedBitSize(elementBitSize, rawArray1.size(),
                array1MaxDeltaBitSize);
        testPackedArray(rawArray1, emptyRawArray, arrayTraits, array1BitSizeOf, array1AlignedBitSizeOf);

        // zero delta
        final RawArray rawArray2 = new RawArray.ByteRawArray(new byte[] {1, 1, 1, 1, 1, 1, 1});
        final int array2BitSizeOf = PACKING_DESCRIPTOR_BITSIZE + elementBitSize;
        final int array2AlignedBitSizeOf = PACKING_DESCRIPTOR_BITSIZE + elementBitSize + /* alignment */ 4;
        testPackedArray(rawArray2, emptyRawArray, arrayTraits, array2BitSizeOf, array2AlignedBitSizeOf);

        // one-element array
        final RawArray rawArray3 = new RawArray.ByteRawArray(new byte[] {1});
        final int array3BitSizeOf = 1 + elementBitSize;
        final int array3AlignedBitSizeOf = 1 + elementBitSize;
        testPackedArray(rawArray3, emptyRawArray, arrayTraits, array3BitSizeOf, array3AlignedBitSizeOf);

        // empty array
        final RawArray rawArray4 = new RawArray.ByteRawArray(new byte[] {});
        final int array4BitSizeOf = 0;
        final int array4AlignedBitSizeOf = 0;
        testPackedArray(rawArray4, emptyRawArray, arrayTraits, array4BitSizeOf, array4AlignedBitSizeOf);
    }

    @Test
    public void bitFieldShortPackedArray() throws IOException
    {
        final RawArray rawArray1 = new RawArray.ShortRawArray(new short[] {512, 500, 459, 400, 333, 300, 222,
                200, 201});
        final RawArray emptyRawArray = new RawArray.ShortRawArray();
        final ArrayTraits arrayTraits13 = new ArrayTraits.BitFieldShortArrayTraits(13);
        testPackedArray(rawArray1, emptyRawArray, arrayTraits13);

        final ArrayTraits arrayTraits8 = new ArrayTraits.BitFieldShortArrayTraits(8);

        // will not be packed because unpacked 8bit values will be more efficient
        final RawArray rawArray2 = new RawArray.ShortRawArray(new short[] {
                255, 0, 10, 20, 30, 40}); // max_bit_number 8, delta needs 9 bits
        final int array2BitSizeOf = 1 + 6 * 8;
        final int array2AlignedBitSizeOf = 1 + 8 + 7 + 5 * 8;
        testPackedArray(rawArray2, emptyRawArray, arrayTraits8, array2BitSizeOf, array2AlignedBitSizeOf);

        // will not be packed because unpacked 8bit values will be more efficient
        // (6 bits more are needed to store max_bit_number in descriptor if packing was enabled)
        final RawArray rawArray3 = new RawArray.ShortRawArray(new short[] {
                255, 128, 10, 20, 30, 40}); // max_bit_number 7, delta needs 8 bits
        final int array3BitSizeOf = 1 + 6 * 8;
        final int array3AlignedBitSizeOf = 1 + 8 + 7 + 5 * 8;
        testPackedArray(rawArray3, emptyRawArray, arrayTraits8, array3BitSizeOf, array3AlignedBitSizeOf);
    }

    @Test
    public void bitFieldIntPackedArray() throws IOException
    {
        final RawArray rawArray = new RawArray.IntRawArray(new int[] {131072, 131000, 130123, 129123,
                128123, 120124});
        final RawArray emptyRawArray = new RawArray.IntRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.BitFieldIntArrayTraits(29);
        testPackedArray(rawArray, emptyRawArray, arrayTraits);
    }

    @Test
    public void bitFieldLongPackedArray() throws IOException
    {
        final RawArray rawArray = new RawArray.LongRawArray(new long[] {8589934592L, 8589934000L, 8589933592L,
                8589933000L, 8589932000L, 8589932001L});
        final RawArray emptyRawArray = new RawArray.LongRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.BitFieldLongArrayTraits(61);
        testPackedArray(rawArray, emptyRawArray, arrayTraits);
    }

    @Test
    public void bitFieldBigIntegerPackedArray() throws IOException
    {
        final RawArray rawArray = new RawArray.BigIntegerRawArray(new BigInteger[] {
                BigInteger.valueOf(8589934592L),
                BigInteger.valueOf(8589934000L),
                BigInteger.valueOf(8589933592L),
                BigInteger.valueOf(8589933000L),
                BigInteger.valueOf(8589932000L),
                BigInteger.valueOf(8589932001L)});
        final RawArray emptyRawArray = new RawArray.BigIntegerRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.BitFieldBigIntegerArrayTraits(64);
        testPackedArray(rawArray, emptyRawArray, arrayTraits);
    }

    @Test
    public void varInt16PackedArray() throws IOException
    {
        final RawArray rawArray = new RawArray.ShortRawArray(new short[] {-1, 200, 399, 409, 600, 800, 1024});
        final RawArray emptyRawArray = new RawArray.ShortRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.VarInt16ArrayTraits();
        testPackedArray(rawArray, emptyRawArray, arrayTraits);
    }

    @Test
    public void varInt32PackedArray() throws IOException
    {
        final RawArray rawArray = new RawArray.IntRawArray(new int[] {-1, 1000, 5000, 8000, 12354, 16384});
        final RawArray emptyRawArray = new RawArray.IntRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.VarInt32ArrayTraits();
        testPackedArray(rawArray, emptyRawArray, arrayTraits);
    }

    @Test
    public void varInt64PackedArray() throws IOException
    {
        final RawArray rawArray = new RawArray.LongRawArray(new long[] {-1, 10737412L, 10737414L, 10737416L,
                10737418L, 107374182L});
        final RawArray emptyRawArray = new RawArray.LongRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.VarInt64ArrayTraits();
        testPackedArray(rawArray, emptyRawArray, arrayTraits);
    }

    @Test
    public void varIntPackedArray() throws IOException
    {
        final RawArray rawArray = new RawArray.LongRawArray(new long[] {-1, 10737412L, 10737414L, 10737416L,
                10737418L, 107374182L});
        final RawArray emptyRawArray = new RawArray.LongRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.VarIntArrayTraits();
        testPackedArray(rawArray, emptyRawArray, arrayTraits);
    }

    @Test
    public void varUInt16PackedArray() throws IOException
    {
        final RawArray rawArray = new RawArray.ShortRawArray(new short[] {1, 234, 453, 700, 894, 999,
                900, 1024});
        final RawArray emptyRawArray = new RawArray.ShortRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.VarUInt16ArrayTraits();
        testPackedArray(rawArray, emptyRawArray, arrayTraits);
    }

    @Test
    public void varUInt32PackedArray() throws IOException
    {
        final RawArray rawArray = new RawArray.IntRawArray(new int[] {1, 1000, 4444, 2222, 6666, 9999,
                11111, 12345, 16384});
        final RawArray emptyRawArray = new RawArray.IntRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.VarUInt32ArrayTraits();
        testPackedArray(rawArray, emptyRawArray, arrayTraits);
    }

    @Test
    public void varUInt64PackedArray() throws IOException
    {
        final RawArray rawArray = new RawArray.LongRawArray(new long[] {1L, 374182L, 107374182L, 10737418L,
                73741824L, 573741824L, 1073741824L});
        final RawArray emptyRawArray = new RawArray.LongRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.VarUInt64ArrayTraits();
        testPackedArray(rawArray, emptyRawArray, arrayTraits);

        final RawArray unpackedRawArray = new RawArray.LongRawArray(new long[] {5000000L, 0, 0, 0, 0, 0, 0});
        final int unpackedBitSizeOf = 1 + 32 + 6 * 8;
        final int unpackedAlignedBitSizeOf = 1 + 32 + 7 + 6 * 8;
        testPackedArray(unpackedRawArray, emptyRawArray, arrayTraits,
                unpackedBitSizeOf, unpackedAlignedBitSizeOf);
    }

    @Test
    public void varUIntPackedArray() throws IOException
    {
        final RawArray rawArray = new RawArray.BigIntegerRawArray(new BigInteger[] {
                BigInteger.valueOf(10737412L),
                BigInteger.valueOf(10737414L),
                BigInteger.valueOf(10737416L),
                BigInteger.valueOf(10737418L),
                BigInteger.valueOf(107374182L),
                BigInteger.valueOf(107374185L)});
        final RawArray emptyRawArray = new RawArray.BigIntegerRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.VarUIntArrayTraits();
        testPackedArray(rawArray, emptyRawArray, arrayTraits);
    }

    @Test
    public void varSizePackedArray() throws IOException
    {
        final RawArray rawArray = new RawArray.IntRawArray(new int[] {1, 4000, 2000, 5600, 11111, 13333,
                15432, 16384});
        final RawArray emptyRawArray = new RawArray.IntRawArray();
        final ArrayTraits arrayTraits = new ArrayTraits.VarSizeArrayTraits();
        testPackedArray(rawArray, emptyRawArray, arrayTraits);
    }

    @Test
    public void bitmaskPackedArray() throws IOException
    {
        final RawArray rawArray = new RawArray.ObjectRawArray<ArrayBitmask>(ArrayBitmask.class,
                new ArrayBitmask[] {
                        ArrayBitmask.Values.CREATE,
                        ArrayBitmask.Values.READ,
                        ArrayBitmask.Values.WRITE,
                        ArrayBitmask.Values.READ,
                        ArrayBitmask.Values.WRITE});
        final RawArray emptyRawArray = new RawArray.ObjectRawArray<ArrayBitmask>(ArrayBitmask.class);
        final ArrayTraits arrayTraits = new ArrayTraits.WritePackableObjectArrayTraits<ArrayBitmask>(
                new PackableArrayBitmaskElementFactory());
        testPackedArray(rawArray, emptyRawArray, arrayTraits);
    }

    @Test
    public void enumPackedArray() throws IOException
    {
        final RawArray rawArray = new RawArray.EnumRawArray<ArrayEnum>(ArrayEnum.class,
                new ArrayEnum[] {
                        ArrayEnum.VALUE1,
                        ArrayEnum.VALUE2,
                        ArrayEnum.VALUE3,
                        ArrayEnum.VALUE2,
                        ArrayEnum.VALUE1});
        final RawArray emptyRawArray = new RawArray.EnumRawArray<ArrayEnum>(ArrayEnum.class);
        final ArrayTraits arrayTraits = new ArrayTraits.WritePackableObjectArrayTraits<ArrayEnum>(
                new PackableArrayEnumElementFactory());
        testPackedArray(rawArray, emptyRawArray, arrayTraits);
    }

    @Test
    public void writeObjectPackedArray() throws IOException
    {
        final RawArray rawArray = new RawArray.ObjectRawArray<ArrayObject>(ArrayObject.class,
                new ArrayObject[] {
                        new ArrayObject((byte)0),
                        new ArrayObject((byte)1),
                        new ArrayObject((byte)2),
                        new ArrayObject((byte)3),
                        new ArrayObject((byte)4)});
        final RawArray emptyRawArray = new RawArray.ObjectRawArray<ArrayObject>(ArrayObject.class);
        final ArrayTraits arrayTraits = new ArrayTraits.WritePackableObjectArrayTraits<ArrayObject>(
                new PackableArrayObjectElementFactory());
        testPackedArray(rawArray, emptyRawArray, arrayTraits);
    }

    private static class PackableArrayBitmaskElementFactory implements PackableElementFactory<ArrayBitmask>
    {
        @Override
        public ArrayBitmask create(BitStreamReader reader, int index) throws IOException
        {
            return new ArrayBitmask(reader);
        }

        @Override
        public PackingContext createPackingContext()
        {
            return new DeltaContext();
        }

        @Override
        public ArrayBitmask create(PackingContext context, BitStreamReader in, int index) throws IOException
        {
            return new ArrayBitmask(context, in);
        }
    }

    private static class PackableArrayEnumElementFactory implements PackableElementFactory<ArrayEnum>
    {
        @Override
        public ArrayEnum create(BitStreamReader reader, int index) throws IOException
        {
            return ArrayEnum.readEnum(reader);
        }

        @Override
        public PackingContext createPackingContext()
        {
            return new DeltaContext();
        }

        @Override
        public ArrayEnum create(PackingContext context, BitStreamReader in, int index) throws IOException
        {
            return ArrayEnum.readEnum(context, in);
        }
    }

    private static class PackableArrayObjectElementFactory implements PackableElementFactory<ArrayObject>
    {
        @Override
        public ArrayObject create(BitStreamReader reader, int index) throws IOException
        {
            return new ArrayObject(reader);
        }

        @Override
        public PackingContext createPackingContext()
        {
            return new ArrayObject.ZserioPackingContext();
        }

        @Override
        public ArrayObject create(PackingContext context, BitStreamReader in, int index) throws IOException
        {
            return new ArrayObject(context, in);
        }
    }

    private static class ArrayTestOffsetChecker implements OffsetChecker
    {
        @Override
        public void checkOffset(int index, long byteOffset)
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
        testArraySize(rawArray1, rawArray2, emptyRawArray, arrayTraits);
        testArrayEquals(rawArray1, rawArray2, emptyRawArray, arrayTraits);
        testArrayHashCode(rawArray1, rawArray2, emptyRawArray, arrayTraits);
        testArray(rawArray1, array1BitSizeOf, array1AlignedBitSizeOf, emptyRawArray, arrayTraits);
    }

    private static void testArraySize(RawArray rawArray1, RawArray rawArray2, RawArray emptyRawArray,
            ArrayTraits arrayTraits)
    {
        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final Array autoArray1 = new Array(rawArray1, arrayTraits, ArrayType.AUTO);
        final Array implicitArray1 = new Array(rawArray1, arrayTraits, ArrayType.IMPLICIT);
        final Array alignedNormalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL,
                new ArrayTestOffsetChecker());
        final Array normalArray2 = new Array(rawArray2, arrayTraits, ArrayType.NORMAL);
        final Array autoArray2 = new Array(rawArray2, arrayTraits, ArrayType.AUTO);
        final Array autoEmptyArray = new Array(emptyRawArray, arrayTraits, ArrayType.AUTO);

        assertEquals(rawArray1.size(), normalArray1.size());
        assertEquals(rawArray1.size(), autoArray1.size());
        assertEquals(rawArray1.size(), implicitArray1.size());
        assertEquals(rawArray1.size(), alignedNormalArray1.size());
        assertEquals(rawArray2.size(), normalArray2.size());
        assertEquals(rawArray2.size(), autoArray2.size());
        assertEquals(emptyRawArray.size(), autoEmptyArray.size());
    }

    private static void testArrayEquals(RawArray rawArray1, RawArray rawArray2, RawArray emptyRawArray,
            ArrayTraits arrayTraits)
    {
        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final Array autoArray1 = new Array(rawArray1, arrayTraits, ArrayType.AUTO);
        final Array implicitArray1 = new Array(rawArray1, arrayTraits, ArrayType.IMPLICIT);
        final Array alignedNormalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL,
                new ArrayTestOffsetChecker(), new ArrayTestOffsetInitializer());
        final Array normalArray2 = new Array(rawArray2, arrayTraits, ArrayType.NORMAL);
        final Array autoArray2 = new Array(rawArray2, arrayTraits, ArrayType.AUTO);
        final Array autoEmptyArray = new Array(emptyRawArray, arrayTraits, ArrayType.AUTO);
        final Array autoWrongArray = new Array(null, arrayTraits, ArrayType.AUTO);

        assertTrue(normalArray1.equals(autoArray1));
        assertTrue(normalArray1.equals(implicitArray1));
        assertTrue(normalArray1.equals(alignedNormalArray1));
        assertFalse(normalArray1.equals(normalArray2));
        assertFalse(normalArray1.equals(autoArray2));
        assertFalse(normalArray1.equals(autoEmptyArray));
        assertFalse(normalArray1.equals(autoWrongArray));
        assertFalse(normalArray1.equals(null));
    }

    private static void testArrayHashCode(RawArray rawArray1, RawArray rawArray2, RawArray emptyRawArray,
            ArrayTraits arrayTraits)
    {
        final Array normalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL);
        final Array autoArray1 = new Array(rawArray1, arrayTraits, ArrayType.AUTO);
        final Array implicitArray1 = new Array(rawArray1, arrayTraits, ArrayType.IMPLICIT);
        final Array alignedNormalArray1 = new Array(rawArray1, arrayTraits, ArrayType.NORMAL,
                new ArrayTestOffsetChecker(), new ArrayTestOffsetInitializer());
        final Array normalArray2 = new Array(rawArray2, arrayTraits, ArrayType.NORMAL);
        final Array autoArray2 = new Array(rawArray2, arrayTraits, ArrayType.AUTO);
        final Array autoEmptyArray = new Array(emptyRawArray, arrayTraits, ArrayType.AUTO);

        assertEquals(normalArray1.hashCode(), autoArray1.hashCode());
        assertEquals(normalArray1.hashCode(), implicitArray1.hashCode());
        assertEquals(normalArray1.hashCode(), alignedNormalArray1.hashCode());
        assertNotEquals(normalArray1.hashCode(), normalArray2.hashCode());
        assertNotEquals(normalArray1.hashCode(), autoArray2.hashCode());
        assertNotEquals(normalArray1.hashCode(), autoEmptyArray.hashCode());
    }

    private static void testArray(RawArray rawArray1, int array1BitSizeOf,
            int array1AlignedBitSizeOf, RawArray readRawArray, ArrayTraits arrayTraits) throws IOException
    {
        testArrayNormal(rawArray1, readRawArray, arrayTraits, array1BitSizeOf);
        final int autoArray1SizeBitSizeOf = BitSizeOfCalculator.getBitSizeOfVarSize(rawArray1.size());
        testArrayAuto(rawArray1, readRawArray, arrayTraits, autoArray1SizeBitSizeOf + array1BitSizeOf);
        testArrayImplicit(rawArray1, readRawArray, arrayTraits, array1BitSizeOf);

        final OffsetChecker offsetChecker = new ArrayTestOffsetChecker();
        final OffsetInitializer offsetInitializer = new ArrayTestOffsetInitializer();
        testArrayAligned(rawArray1, readRawArray, arrayTraits, offsetChecker, offsetInitializer,
                array1AlignedBitSizeOf);
        testArrayAlignedAuto(rawArray1, readRawArray, arrayTraits, offsetChecker, offsetInitializer,
                autoArray1SizeBitSizeOf + array1AlignedBitSizeOf);
    }

    private static void testArrayNormal(RawArray rawArray, RawArray readRawArray, ArrayTraits arrayTraits,
            int expectedBitSizeOf) throws IOException
    {
        final Array array = new Array(rawArray, arrayTraits, ArrayType.NORMAL);
        for (int bitPosition = 0; bitPosition < 8; ++bitPosition)
        {
            final int bitSizeOf = array.bitSizeOf(bitPosition);
            assertEquals(expectedBitSizeOf, bitSizeOf);
            assertEquals(bitPosition + bitSizeOf, array.initializeOffsets(bitPosition));

            try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
            {
                if (bitPosition > 0)
                    writer.writeBits(0, bitPosition);
                array.write(writer);
                final long writtenBitPosition = writer.getBitPosition();
                assertEquals(bitPosition + bitSizeOf, writtenBitPosition);
                final byte[] writtenByteArray = writer.toByteArray();

                final BitBuffer readerBuffer = new BitBuffer(writtenByteArray, writtenBitPosition);
                try (final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(readerBuffer))
                {
                    if (bitPosition > 0)
                        assertEquals(0, reader.readBits(bitPosition));
                    final Array readArray = new Array(readRawArray, arrayTraits, ArrayType.NORMAL);
                    readArray.read(reader, rawArray.size());
                    assertEquals(array, readArray);
                }
            }
        }
    }

    private static void testArrayAuto(RawArray rawArray, RawArray readRawArray, ArrayTraits arrayTraits,
            int expectedBitSizeOf) throws IOException
    {
        final Array array = new Array(rawArray, arrayTraits, ArrayType.AUTO);
        for (int bitPosition = 0; bitPosition < 8; ++bitPosition)
        {
            final int bitSizeOf = array.bitSizeOf(bitPosition);
            assertEquals(expectedBitSizeOf, bitSizeOf);
            assertEquals(bitPosition + bitSizeOf, array.initializeOffsets(bitPosition));

            try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
            {
                if (bitPosition > 0)
                    writer.writeBits(0, bitPosition);
                array.write(writer);
                final long writtenBitPosition = writer.getBitPosition();
                assertEquals(bitPosition + bitSizeOf, writtenBitPosition);
                final byte[] writtenByteArray = writer.toByteArray();

                final BitBuffer readerBuffer = new BitBuffer(writtenByteArray, writtenBitPosition);
                try (final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(readerBuffer))
                {
                    if (bitPosition > 0)
                        assertEquals(0, reader.readBits(bitPosition));
                    final Array readArray = new Array(readRawArray, arrayTraits, ArrayType.AUTO);
                    readArray.read(reader);
                    assertEquals(array, readArray);
                }
            }
        }
    }

    private static void testArrayImplicit(RawArray rawArray, RawArray readRawArray, ArrayTraits arrayTraits,
            int expectedBitSizeOf) throws IOException
    {
        final Array array = new Array(rawArray, arrayTraits, ArrayType.IMPLICIT);
        for (int bitPosition = 0; bitPosition < 8; ++bitPosition)
        {
            final int bitSizeOf = array.bitSizeOf(bitPosition);
            assertEquals(expectedBitSizeOf, bitSizeOf);
            assertEquals(bitPosition + bitSizeOf, array.initializeOffsets(bitPosition));

            try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
            {
                if (bitPosition > 0)
                    writer.writeBits(0, bitPosition);
                array.write(writer);
                final long writtenBitPosition = writer.getBitPosition();
                assertEquals(bitPosition + bitSizeOf, writtenBitPosition);
                final byte[] writtenByteArray = writer.toByteArray();

                final BitBuffer readerBuffer = new BitBuffer(writtenByteArray, writtenBitPosition);
                try (final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(readerBuffer))
                {
                    if (bitPosition > 0)
                        assertEquals(0, reader.readBits(bitPosition));
                    if (arrayTraits.isBitSizeOfConstant())
                    {
                        final Array readArray = new Array(readRawArray, arrayTraits, ArrayType.IMPLICIT);
                        readArray.read(reader);
                        assertEquals(array, readArray);
                    }
                    else
                    {
                        final Array readArray = new Array(readRawArray, arrayTraits, ArrayType.IMPLICIT);
                        assertThrows(UnsupportedOperationException.class, () -> readArray.read(reader));
                    }
                }
            }
        }
    }

    private static void testArrayAligned(RawArray rawArray, RawArray readRawArray, ArrayTraits arrayTraits,
            OffsetChecker offsetChecker, OffsetInitializer offsetInitializer, int expectedBitSizeOf)
                    throws IOException
    {
        final Array array = new Array(rawArray, arrayTraits, ArrayType.NORMAL, offsetChecker,
                offsetInitializer);
        for (int bitPosition = 0; bitPosition < 8; ++bitPosition)
        {
            final int bitSizeOf = array.bitSizeOf(bitPosition);
            if (array.size() > 0)
            {
                final int alignedExpectedBitSizeOf = expectedBitSizeOf +
                        (int)BitPositionUtil.alignTo(8, bitPosition) - bitPosition;
                assertEquals(alignedExpectedBitSizeOf, bitSizeOf);
            }
            assertEquals(bitPosition + bitSizeOf, array.initializeOffsets(bitPosition));

            try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
            {
                if (bitPosition > 0)
                    writer.writeBits(0, bitPosition);
                array.write(writer);
                final long writtenBitPosition = writer.getBitPosition();
                assertEquals(bitPosition + bitSizeOf, writtenBitPosition);
                final byte[] writtenByteArray = writer.toByteArray();

                final BitBuffer readerBuffer = new BitBuffer(writtenByteArray, writtenBitPosition);
                try (final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(readerBuffer))
                {
                    if (bitPosition > 0)
                        assertEquals(0, reader.readBits(bitPosition));
                    final Array readArray = new Array(readRawArray, arrayTraits, ArrayType.NORMAL, offsetChecker,
                            offsetInitializer);
                    readArray.read(reader, rawArray.size());
                    assertEquals(array, readArray);
                }
            }
        }
    }

    private static void testArrayAlignedAuto(RawArray rawArray, RawArray readRawArray, ArrayTraits arrayTraits,
            OffsetChecker offsetChecker, OffsetInitializer offsetInitializer, int expectedBitSizeOf)
                    throws IOException
    {
        final Array array = new Array(rawArray, arrayTraits, ArrayType.AUTO, offsetChecker, offsetInitializer);
        for (int bitPosition = 0; bitPosition < 8; ++bitPosition)
        {
            final int bitSizeOf = array.bitSizeOf(bitPosition);
            if (array.size() > 0)
            {
                final int alignedExpectedBitSizeOf = expectedBitSizeOf +
                        (int)BitPositionUtil.alignTo(8, bitPosition) - bitPosition;
                assertEquals(alignedExpectedBitSizeOf, bitSizeOf);
            }
            assertEquals(bitPosition + bitSizeOf, array.initializeOffsets(bitPosition));

            try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
            {
                if (bitPosition > 0)
                    writer.writeBits(0, bitPosition);
                array.write(writer);
                final long writtenBitPosition = writer.getBitPosition();
                assertEquals(bitPosition + bitSizeOf, writtenBitPosition);
                final byte[] writtenByteArray = writer.toByteArray();

                final BitBuffer readerBuffer = new BitBuffer(writtenByteArray, writtenBitPosition);
                try (final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(readerBuffer))
                {
                    if (bitPosition > 0)
                        assertEquals(0, reader.readBits(bitPosition));
                    final Array readArray = new Array(readRawArray, arrayTraits, ArrayType.AUTO, offsetChecker,
                                offsetInitializer);
                    readArray.read(reader);
                    assertEquals(array, readArray);
                }
            }
        }
    }

    private static void testPackedArray(RawArray rawArray, RawArray emptyRawArray, ArrayTraits arrayTraits)
            throws IOException
    {
        testPackedArray(rawArray, emptyRawArray, arrayTraits, UNKNOWN_BITSIZE, UNKNOWN_BITSIZE);
    }

    private static void testPackedArray(RawArray rawArray, RawArray readRawArray, ArrayTraits arrayTraits,
            int bitSizeOf, int alignedBitSizeOf) throws IOException
    {
        testPackedArrayNormal(rawArray, readRawArray, arrayTraits, bitSizeOf);
        final int autoSizeBitSizeOf = BitSizeOfCalculator.getBitSizeOfVarSize(rawArray.size());
        final int autoBitSizeOf = (bitSizeOf != UNKNOWN_BITSIZE) ?
                autoSizeBitSizeOf + bitSizeOf : UNKNOWN_BITSIZE;
        testPackedArrayAuto(rawArray, readRawArray, arrayTraits, autoBitSizeOf);
        testPackedArrayImplicit(rawArray, readRawArray, arrayTraits);

        final OffsetChecker offsetChecker = new ArrayTestOffsetChecker();
        final OffsetInitializer offsetInitializer = new ArrayTestOffsetInitializer();
        testPackedArrayAligned(rawArray, readRawArray, arrayTraits, offsetChecker, offsetInitializer,
                alignedBitSizeOf);
        final int autoAlignedBitSizeOf = (alignedBitSizeOf != UNKNOWN_BITSIZE) ?
                autoSizeBitSizeOf + alignedBitSizeOf : UNKNOWN_BITSIZE;
        testPackedArrayAlignedAuto(rawArray, readRawArray, arrayTraits, offsetChecker, offsetInitializer,
                autoAlignedBitSizeOf);
    }

    private static void testPackedArrayNormal(RawArray rawArray, RawArray readRawArray,
            ArrayTraits arrayTraits, int expectedBitSizeOf) throws IOException
    {
        final Array array = new Array(rawArray, arrayTraits, ArrayType.NORMAL);
        for (int bitPosition = 0; bitPosition < 8; ++bitPosition)
        {
            final int bitSizeOf = array.bitSizeOfPacked(bitPosition);
            if (expectedBitSizeOf != UNKNOWN_BITSIZE)
                assertEquals(expectedBitSizeOf, bitSizeOf);
            assertEquals(bitPosition + bitSizeOf, array.initializeOffsetsPacked(bitPosition));

            try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
            {
                if (bitPosition > 0)
                    writer.writeBits(0, bitPosition);
                array.writePacked(writer);
                final long writtenBitPosition = writer.getBitPosition();
                assertEquals(bitPosition + bitSizeOf, writtenBitPosition);
                final byte[] writtenByteArray = writer.toByteArray();

                final BitBuffer readerBuffer = new BitBuffer(writtenByteArray, writtenBitPosition);
                try (final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(readerBuffer))
                {
                    if (bitPosition > 0)
                        assertEquals(0, reader.readBits(bitPosition));
                    final Array readArray = new Array(readRawArray, arrayTraits, ArrayType.NORMAL);
                    readArray.readPacked(reader, rawArray.size());
                    assertEquals(array, readArray);
                }
            }
        }
    }

    private static void testPackedArrayAuto(RawArray rawArray, RawArray readRawArray, ArrayTraits arrayTraits,
            int expectedBitSizeOf) throws IOException
    {
        final Array array = new Array(rawArray, arrayTraits, ArrayType.AUTO);
        for (int bitPosition = 0; bitPosition < 8; ++bitPosition)
        {
            final int bitSizeOf = array.bitSizeOfPacked(bitPosition);
            if (expectedBitSizeOf != UNKNOWN_BITSIZE)
                assertEquals(expectedBitSizeOf, bitSizeOf);
            assertEquals(bitPosition + bitSizeOf, array.initializeOffsetsPacked(bitPosition));

            try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
            {
                if (bitPosition > 0)
                    writer.writeBits(0, bitPosition);
                array.writePacked(writer);
                final long writtenBitPosition = writer.getBitPosition();
                assertEquals(bitPosition + bitSizeOf, writtenBitPosition);
                final byte[] writtenByteArray = writer.toByteArray();

                final BitBuffer readerBuffer = new BitBuffer(writtenByteArray, writtenBitPosition);
                try (final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(readerBuffer))
                {
                    if (bitPosition > 0)
                        assertEquals(0, reader.readBits(bitPosition));
                    final Array readArray = new Array(readRawArray, arrayTraits, ArrayType.AUTO);
                    readArray.readPacked(reader);
                    assertEquals(array, readArray);
                }
            }
        }
    }

    private static void testPackedArrayImplicit(RawArray rawArray, RawArray readRawArray,
            ArrayTraits arrayTraits) throws IOException
    {
        final Array array = new Array(rawArray, arrayTraits, ArrayType.IMPLICIT);

        assertThrows(UnsupportedOperationException.class, () -> array.bitSizeOfPacked(0));

        assertThrows(UnsupportedOperationException.class, () -> array.initializeOffsetsPacked(0));

        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            assertThrows(UnsupportedOperationException.class, () -> array.writePacked(writer));
        }

        try (final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(new byte[]{}))
        {
            assertThrows(UnsupportedOperationException.class, () -> array.readPacked(reader, rawArray.size()));
        }
    }

    private static void testPackedArrayAligned(RawArray rawArray, RawArray readRawArray,
            ArrayTraits arrayTraits, OffsetChecker offsetChecker, OffsetInitializer offsetInitializer,
            int expectedBitSizeOf) throws IOException
    {
        final Array array = new Array(rawArray, arrayTraits, ArrayType.NORMAL, offsetChecker,
                offsetInitializer);
        for (int bitPosition = 0; bitPosition < 8; ++bitPosition)
        {
            final int bitSizeOf = array.bitSizeOfPacked(bitPosition);
            if (expectedBitSizeOf != UNKNOWN_BITSIZE && bitPosition == 0)
                assertEquals(expectedBitSizeOf, bitSizeOf);
            assertEquals(bitPosition + bitSizeOf, array.initializeOffsetsPacked(bitPosition));

            try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
            {
                if (bitPosition > 0)
                    writer.writeBits(0, bitPosition);
                array.writePacked(writer);
                final long writtenBitPosition = writer.getBitPosition();
                assertEquals(bitPosition + bitSizeOf, writtenBitPosition);
                final byte[] writtenByteArray = writer.toByteArray();

                final BitBuffer readerBuffer = new BitBuffer(writtenByteArray, writtenBitPosition);
                try (final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(readerBuffer))
                {
                    if (bitPosition > 0)
                        assertEquals(0, reader.readBits(bitPosition));
                    final Array readArray = new Array(readRawArray, arrayTraits, ArrayType.NORMAL,
                            offsetChecker, offsetInitializer);
                    readArray.readPacked(reader, rawArray.size());
                    assertEquals(array, readArray);
                }
            }
        }
    }

    private static void testPackedArrayAlignedAuto(RawArray rawArray, RawArray readRawArray,
            ArrayTraits arrayTraits, OffsetChecker offsetChecker, OffsetInitializer offsetInitializer,
            int expectedBitSizeOf) throws IOException
    {
        final Array array = new Array(rawArray, arrayTraits, ArrayType.AUTO, offsetChecker, offsetInitializer);
        for (int bitPosition = 0; bitPosition < 8; ++bitPosition)
        {
            final int bitSizeOf = array.bitSizeOfPacked(bitPosition);
            if (expectedBitSizeOf != UNKNOWN_BITSIZE && bitPosition == 0)
                assertEquals(expectedBitSizeOf, bitSizeOf);
            assertEquals(bitPosition + bitSizeOf, array.initializeOffsetsPacked(bitPosition));

            try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
            {
                if (bitPosition > 0)
                    writer.writeBits(0, bitPosition);
                array.writePacked(writer);
                final long writtenBitPosition = writer.getBitPosition();
                assertEquals(bitPosition + bitSizeOf, writtenBitPosition);
                final byte[] writtenByteArray = writer.toByteArray();

                final BitBuffer readerBuffer = new BitBuffer(writtenByteArray, writtenBitPosition);
                try (final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(readerBuffer))
                {
                    if (bitPosition > 0)
                        assertEquals(0, reader.readBits(bitPosition));
                    final Array readArray = new Array(readRawArray, arrayTraits, ArrayType.AUTO,
                            offsetChecker, offsetInitializer);
                    readArray.readPacked(reader);
                    assertEquals(array, readArray);
                }
            }
        }
    }

    private static int calcPackedBitSize(int elementBitSize, int arraySize, int maxDeltaBitSize)
    {
        return PACKING_DESCRIPTOR_BITSIZE + elementBitSize + (arraySize - 1) * (maxDeltaBitSize + 1);
    }

    private static int calcAlignedPackedBitSize(int elementBitSize, int arraySize, int maxDeltaBitSize)
    {
        final int firstElementWithDescriptorBitSize = PACKING_DESCRIPTOR_BITSIZE + elementBitSize;
        final int firstAlignedElementWithDescriptorBitSize = (firstElementWithDescriptorBitSize + 7) / 8 * 8;
        final int alignedMaxDeltaBitSize = (maxDeltaBitSize + 1 + 7) / 8 * 8;

        return firstAlignedElementWithDescriptorBitSize +
                (arraySize - 2) * alignedMaxDeltaBitSize + (maxDeltaBitSize + 1);
    }

    private static int PACKING_DESCRIPTOR_BITSIZE = 1 + 6;
    private static int UNKNOWN_BITSIZE = -1;
}
