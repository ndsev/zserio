package array_types;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.File;

import org.junit.Test;

import array_types.implicit_array_int24.ImplicitArray;

import zserio.runtime.ZserioError;
import zserio.runtime.array.IntArray;
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
        IntArray array = new IntArray(numElements);
        for (int i = 0; i < numElements; ++i)
            array.setElementAt(i, i);

        final ImplicitArray implicitArray = new ImplicitArray(array);
        final int bitPosition = 2;
        final int implicitArrayBitSize = numElements * 24;
        assertEquals(implicitArrayBitSize, implicitArray.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets() throws IOException, ZserioError
    {
        final int numElements = 66;
        IntArray array = new IntArray(numElements);
        for (int i = 0; i < numElements; ++i)
            array.setElementAt(i, i);

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
        final BitStreamReader stream = new FileBitStreamReader(file);
        final ImplicitArray implicitArray = new ImplicitArray(stream);
        stream.close();

        final IntArray array = implicitArray.getArray();
        assertEquals(numElements, array.length());
        for (int i = 0; i < numElements; ++i)
            assertEquals(i, array.elementAt(i));
    }

    @Test
    public void write() throws IOException, ZserioError
    {
        final int numElements = 55;
        IntArray array = new IntArray(numElements);
        for (int i = 0; i < numElements; ++i)
            array.setElementAt(i, i);

        ImplicitArray implicitArray = new ImplicitArray(array);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        implicitArray.write(writer);
        writer.close();

        final ImplicitArray readImplicitArray = new ImplicitArray(file);
        final IntArray readArray = readImplicitArray.getArray();
        assertEquals(numElements, readArray.length());
        for (int i = 0; i < numElements; ++i)
            assertEquals(i, readArray.elementAt(i));
    }

    private void writeImplicitArrayToFile(File file, int numElements) throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);

        for (int i = 0; i < numElements; ++i)
            writer.writeSignedBits(i, 24);

        writer.close();
    }
}
