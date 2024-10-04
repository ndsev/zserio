package allow_implicit_arrays;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

import allow_implicit_arrays.implicit_array_int24.ImplicitArray;

public class ImplicitArrayInt24Test
{
    @Test
    public void bitSizeOf() throws IOException, ZserioError
    {
        final int numElements = 44;
        final int[] array = new int[numElements];
        for (int i = 0; i < numElements; ++i)
            array[i] = i;

        final ImplicitArray implicitArray = new ImplicitArray(array);
        final int bitPosition = 2;
        final int implicitArrayBitSize = numElements * 24;
        assertEquals(implicitArrayBitSize, implicitArray.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets() throws IOException, ZserioError
    {
        final int numElements = 66;
        final int[] array = new int[numElements];
        for (int i = 0; i < numElements; ++i)
            array[i] = i;

        final ImplicitArray implicitArray = new ImplicitArray(array);
        final int bitPosition = 2;
        final int expectedEndBitPosition = bitPosition + numElements * 24;
        assertEquals(expectedEndBitPosition, implicitArray.initializeOffsets(bitPosition));
    }

    @Test
    public void read() throws IOException, ZserioError
    {
        final int numElements = 99;
        final BitBuffer buffer = writeImplicitArrayToBitBuffer(numElements);
        final ImplicitArray implicitArray = SerializeUtil.deserialize(ImplicitArray.class, buffer);

        final int[] array = implicitArray.getArray();
        assertEquals(numElements, array.length);
        for (int i = 0; i < numElements; ++i)
            assertEquals(i, array[i]);
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final int numElements = 55;
        final int[] array = new int[numElements];
        for (int i = 0; i < numElements; ++i)
            array[i] = i;

        ImplicitArray implicitArray = new ImplicitArray(array);
        SerializeUtil.serializeToFile(implicitArray, BLOB_NAME);

        final ImplicitArray readImplicitArray =
                SerializeUtil.deserializeFromFile(ImplicitArray.class, BLOB_NAME);
        final int[] readArray = readImplicitArray.getArray();
        assertEquals(numElements, readArray.length);
        for (int i = 0; i < numElements; ++i)
            assertEquals(i, readArray[i]);
    }

    private BitBuffer writeImplicitArrayToBitBuffer(int numElements) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            for (int i = 0; i < numElements; ++i)
                writer.writeSignedBits(i, 24);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private static final String BLOB_NAME = "implicit_array_int24.blob";
}
