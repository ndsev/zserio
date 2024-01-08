package allow_implicit_arrays;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

import allow_implicit_arrays.implicit_array_uint64.ImplicitArray;

public class ImplicitArrayUInt64Test
{
    @Test
    public void bitSizeOf() throws IOException, ZserioError
    {
        final int numElements = 44;
        final BigInteger[] array = new BigInteger[numElements];
        for (int i = 0; i < numElements; ++i)
            array[i] = BigInteger.valueOf(i);

        final ImplicitArray implicitArray = new ImplicitArray(array);
        final int bitPosition = 2;
        final int implicitArrayBitSize = numElements * 64;
        assertEquals(implicitArrayBitSize, implicitArray.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets() throws IOException, ZserioError
    {
        final int numElements = 66;
        final BigInteger[] array = new BigInteger[numElements];
        for (int i = 0; i < numElements; ++i)
            array[i] = BigInteger.valueOf(i);

        final ImplicitArray implicitArray = new ImplicitArray(array);
        final int bitPosition = 2;
        final int expectedEndBitPosition = bitPosition + numElements * 64;
        assertEquals(expectedEndBitPosition, implicitArray.initializeOffsets(bitPosition));
    }

    @Test
    public void read() throws IOException, ZserioError
    {
        final int numElements = 99;
        final BitBuffer buffer = writeImplicitArrayToBitBuffer(numElements);
        final ImplicitArray implicitArray = SerializeUtil.deserialize(ImplicitArray.class, buffer);

        final BigInteger[] array = implicitArray.getArray();
        assertEquals(numElements, array.length);
        for (int i = 0; i < numElements; ++i)
            assertEquals(BigInteger.valueOf(i), array[i]);
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final int numElements = 55;
        final BigInteger[] array = new BigInteger[numElements];
        for (int i = 0; i < numElements; ++i)
            array[i] = BigInteger.valueOf(i);

        ImplicitArray implicitArray = new ImplicitArray(array);
        SerializeUtil.serializeToFile(implicitArray, BLOB_NAME);

        final ImplicitArray readImplicitArray =
                SerializeUtil.deserializeFromFile(ImplicitArray.class, BLOB_NAME);
        final BigInteger[] readArray = readImplicitArray.getArray();
        assertEquals(numElements, readArray.length);
        for (int i = 0; i < numElements; ++i)
            assertEquals(BigInteger.valueOf(i), readArray[i]);
    }

    private BitBuffer writeImplicitArrayToBitBuffer(int numElements) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            for (int i = 0; i < numElements; ++i)
                writer.writeBigInteger(BigInteger.valueOf(i), 64);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private static final String BLOB_NAME = "implicit_array_uint64.blob";
}
