package allow_implicit_arrays;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;

import allow_implicit_arrays.implicit_array_float16.ImplicitArray;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class ImplicitArrayFloat16Test
{
    @Test
    public void bitSizeOf() throws IOException, ZserioError
    {
        final int numElements = 44;
        final float[] array = new float[numElements];
        for (short i = 0; i < numElements; ++i)
            array[i] = i;

        final ImplicitArray implicitArray = new ImplicitArray(array);
        final int bitPosition = 2;
        final int implicitArrayBitSize = numElements * 16;
        assertEquals(implicitArrayBitSize, implicitArray.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets() throws IOException, ZserioError
    {
        final int numElements = 66;
        final float[] array = new float[numElements];
        for (short i = 0; i < numElements; ++i)
            array[i] = i;

        final ImplicitArray implicitArray = new ImplicitArray(array);
        final int bitPosition = 2;
        final int expectedEndBitPosition = bitPosition + numElements * 16;
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

            final float[] array = implicitArray.getArray();
            assertEquals(numElements, array.length);
            for (short i = 0; i < numElements; ++i)
                assertEquals((float)i, array[i], 0.001f);
        }
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final int numElements = 55;
        final float[] array = new float[numElements];
        for (short i = 0; i < numElements; ++i)
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
        final float[] readArray = readImplicitArray.getArray();
        assertEquals(numElements, readArray.length);
        for (short i = 0; i < numElements; ++i)
            assertEquals((float)i, readArray[i], 0.001f);
    }

    private void writeImplicitArrayToFile(File file, int numElements) throws IOException
    {
        try (final FileBitStreamWriter writer = new FileBitStreamWriter(file))
        {
            for (short i = 0; i < numElements; ++i)
                writer.writeFloat16((float)i);
        }
    }

    private static final String BLOB_NAME = "implicit_array_float16.blob";
}
