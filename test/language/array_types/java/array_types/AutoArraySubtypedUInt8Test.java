package array_types;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.File;

import org.junit.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.array.UnsignedByteArray;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import array_types.auto_array_subtyped_uint8.AutoArray;

public class AutoArraySubtypedUInt8Test
{
    @Test
    public void bitSizeOfLength1() throws IOException, ZserioError
    {
        checkBitSizeOf(AUTO_ARRAY_LENGTH1);
    }

    @Test
    public void bitSizeOfLength2() throws IOException, ZserioError
    {
        checkBitSizeOf(AUTO_ARRAY_LENGTH2);
    }

    @Test
    public void initializeOffsetsLength1() throws IOException, ZserioError
    {
        checkInitializeOffsets(AUTO_ARRAY_LENGTH1);
    }

    @Test
    public void initializeOffsetsLength2() throws IOException, ZserioError
    {
        checkInitializeOffsets(AUTO_ARRAY_LENGTH2);
    }

    @Test
    public void readLength1() throws IOException, ZserioError
    {
        checkRead(AUTO_ARRAY_LENGTH1);
    }

    @Test
    public void readLength2() throws IOException, ZserioError
    {
        checkRead(AUTO_ARRAY_LENGTH2);
    }

    @Test
    public void writeLength1() throws IOException, ZserioError
    {
        checkWrite(AUTO_ARRAY_LENGTH1);
    }

    @Test
    public void writeLength2() throws IOException, ZserioError
    {
        checkWrite(AUTO_ARRAY_LENGTH2);
    }

    private void checkBitSizeOf(short numElements) throws IOException, ZserioError
    {
        final UnsignedByteArray array = new UnsignedByteArray(numElements);
        for (short i = 0; i < numElements; ++i)
            array.setElementAt(i, i);

        final AutoArray autoArray = new AutoArray(array);
        final int bitPosition = 2;
        final int autoArrayBitSize = 8 + numElements * 8;
        assertEquals(autoArrayBitSize, autoArray.bitSizeOf(bitPosition));
    }

    private void checkInitializeOffsets(short numElements) throws IOException, ZserioError
    {
        final UnsignedByteArray array = new UnsignedByteArray(numElements);
        for (short i = 0; i < numElements; ++i)
            array.setElementAt(i, i);

        final AutoArray autoArray = new AutoArray(array);
        final int bitPosition = 2;
        final int expectedEndBitPosition = bitPosition + 8 + numElements * 8;
        assertEquals(expectedEndBitPosition, autoArray.initializeOffsets(bitPosition));
    }

    private void checkRead(short numElements) throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        writeAutoArrayToFile(file, numElements);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final AutoArray autoArray = new AutoArray(stream);
        stream.close();

        final UnsignedByteArray array = autoArray.getArray();
        assertEquals(numElements, array.length());
        for (short i = 0; i < numElements; ++i)
            assertEquals(i, array.elementAt(i));
    }

    private void checkWrite(short numElements) throws IOException, ZserioError
    {
        final UnsignedByteArray array = new UnsignedByteArray(numElements);
        for (short i = 0; i < numElements; ++i)
            array.setElementAt(i, i);

        final AutoArray autoArray = new AutoArray(array);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        autoArray.write(writer);
        writer.close();

        final AutoArray readAutoArray = new AutoArray(file);
        final UnsignedByteArray readArray = readAutoArray.getArray();
        assertEquals(numElements, readArray.length());
        for (short i = 0; i < numElements; ++i)
            assertEquals(i, readArray.elementAt(i));
    }

    private void writeAutoArrayToFile(File file, short numElements) throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);

        writer.writeVarSize(numElements);
        for (short i = 0; i < numElements; ++i)
            writer.writeUnsignedByte(i);

        writer.close();
    }

    private static final short AUTO_ARRAY_LENGTH1 = 5;
    private static final short AUTO_ARRAY_LENGTH2 = 10;
}
