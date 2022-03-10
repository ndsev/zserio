package allow_implicit_arrays;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;

import allow_implicit_arrays.implicit_array_int24.ImplicitArray;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

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
        final File file = new File("test.bin");
        final int numElements = 99;
        writeImplicitArrayToFile(file, numElements);
        try (final BitStreamReader stream = new FileBitStreamReader(file))
        {
            final ImplicitArray implicitArray = new ImplicitArray(stream);

            final int[] array = implicitArray.getArray();
            assertEquals(numElements, array.length);
            for (int i = 0; i < numElements; ++i)
                assertEquals(i, array[i]);
        }
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final int numElements = 55;
        final int[] array = new int[numElements];
        for (int i = 0; i < numElements; ++i)
            array[i] = i;

        ImplicitArray implicitArray = new ImplicitArray(array);
        final File file = new File(BLOB_NAME);
        try (final BitStreamWriter writer = new FileBitStreamWriter(file))
        {
            implicitArray.write(writer);

            assertEquals(implicitArray.bitSizeOf(), writer.getBitPosition());
            assertEquals(implicitArray.initializeOffsets(0), writer.getBitPosition());
        }

        final ImplicitArray readImplicitArray = new ImplicitArray(file);
        final int[] readArray = readImplicitArray.getArray();
        assertEquals(numElements, readArray.length);
        for (int i = 0; i < numElements; ++i)
            assertEquals(i, readArray[i]);
    }

    private void writeImplicitArrayToFile(File file, int numElements) throws IOException
    {
        try (final FileBitStreamWriter writer = new FileBitStreamWriter(file))
        {
            for (int i = 0; i < numElements; ++i)
                writer.writeSignedBits(i, 24);
        }
    }

    private static final String BLOB_NAME = "implicit_array_int24.blob";
}
