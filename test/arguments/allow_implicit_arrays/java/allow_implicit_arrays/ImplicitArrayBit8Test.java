package allow_implicit_arrays;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import allow_implicit_arrays.implicit_array_bit8.ImplicitArray;

import zserio.runtime.ZserioError;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;
import zserio.runtime.io.BitBuffer;

public class ImplicitArrayBit8Test
{
    @Test
    public void bitSizeOf() throws IOException, ZserioError
    {
        final int numElements = 44;
        final short[] array = new short[numElements];
        for (int i = 0; i < numElements; ++i)
            array[i] = (short)i;

        final ImplicitArray implicitArray = new ImplicitArray(array);
        final int bitPosition = 2;
        final int implicitArrayBitSize = numElements * 8;
        assertEquals(implicitArrayBitSize, implicitArray.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets() throws IOException, ZserioError
    {
        final int numElements = 66;
        final short[] array = new short[numElements];
        for (short i = 0; i < numElements; ++i)
            array[i] = i;

        final ImplicitArray implicitArray = new ImplicitArray(array);
        final int bitPosition = 2;
        final int expectedEndBitPosition = bitPosition + numElements * 8;
        assertEquals(expectedEndBitPosition, implicitArray.initializeOffsets(bitPosition));
    }

    @Test
    public void read() throws IOException, ZserioError
    {
        final int numElements = 99;
        final BitBuffer buffer = writeImplicitArrayToBitBuffer(numElements);
        final ImplicitArray implicitArray = SerializeUtil.deserialize(ImplicitArray.class, buffer);

        final short[] array = implicitArray.getArray();
        assertEquals(numElements, array.length);
        for (short i = 0; i < numElements; ++i)
            assertEquals(i, array[i]);
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final int numElements = 55;
        final short[] array = new short[numElements];
        for (short i = 0; i < numElements; ++i)
            array[i] = i;

        ImplicitArray implicitArray = new ImplicitArray(array);
        SerializeUtil.serializeToFile(implicitArray, BLOB_NAME);

        final ImplicitArray readImplicitArray = SerializeUtil.deserializeFromFile(ImplicitArray.class,
                BLOB_NAME);
        final short[] readArray = readImplicitArray.getArray();
        assertEquals(numElements, readArray.length);
        for (short i = 0; i < numElements; ++i)
            assertEquals(i, readArray[i]);
    }

    private BitBuffer writeImplicitArrayToBitBuffer(int numElements) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            for (int i = 0; i < numElements; ++i)
                writer.writeUnsignedByte((short)i);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private static final String BLOB_NAME = "implicit_array_bit8.blob";
}
