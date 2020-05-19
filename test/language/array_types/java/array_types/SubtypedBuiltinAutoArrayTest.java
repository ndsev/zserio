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

import array_types.subtyped_builtin_auto_array.SubtypedBuiltinAutoArray;

public class SubtypedBuiltinAutoArrayTest
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

        final SubtypedBuiltinAutoArray subtypedBuiltinAutoArray = new SubtypedBuiltinAutoArray(array);
        final int bitPosition = 2;
        final int autoArrayBitSize = 8 + numElements * 8;
        assertEquals(autoArrayBitSize, subtypedBuiltinAutoArray.bitSizeOf(bitPosition));
    }

    private void checkInitializeOffsets(short numElements) throws IOException, ZserioError
    {
        final UnsignedByteArray array = new UnsignedByteArray(numElements);
        for (short i = 0; i < numElements; ++i)
            array.setElementAt(i, i);

        final SubtypedBuiltinAutoArray subtypedBuiltinAutoArray = new SubtypedBuiltinAutoArray(array);
        final int bitPosition = 2;
        final int expectedEndBitPosition = bitPosition + 8 + numElements * 8;
        assertEquals(expectedEndBitPosition, subtypedBuiltinAutoArray.initializeOffsets(bitPosition));
    }

    private void checkRead(short numElements) throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        writeSubtypedBuiltinAutoArrayToFile(file, numElements);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final SubtypedBuiltinAutoArray subtypedBuiltinAutoArray = new SubtypedBuiltinAutoArray(stream);
        stream.close();

        final UnsignedByteArray array = subtypedBuiltinAutoArray.getArray();
        assertEquals(numElements, array.length());
        for (short i = 0; i < numElements; ++i)
            assertEquals(i, array.elementAt(i));
    }

    private void checkWrite(short numElements) throws IOException, ZserioError
    {
        final UnsignedByteArray array = new UnsignedByteArray(numElements);
        for (short i = 0; i < numElements; ++i)
            array.setElementAt(i, i);

        final SubtypedBuiltinAutoArray subtypedBuiltinAutoArray = new SubtypedBuiltinAutoArray(array);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        subtypedBuiltinAutoArray.write(writer);
        writer.close();

        final SubtypedBuiltinAutoArray readSubtypedBuiltinAutoArray = new SubtypedBuiltinAutoArray(file);
        final UnsignedByteArray readArray = readSubtypedBuiltinAutoArray.getArray();
        assertEquals(numElements, readArray.length());
        for (short i = 0; i < numElements; ++i)
            assertEquals(i, readArray.elementAt(i));
    }

    private void writeSubtypedBuiltinAutoArrayToFile(File file, short numElements) throws IOException
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
