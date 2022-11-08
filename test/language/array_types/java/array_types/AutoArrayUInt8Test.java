package array_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import array_types.auto_array_uint8.AutoArray;

public class AutoArrayUInt8Test
{
    @Test
    public void emptyConstructor()
    {
        final AutoArray autoArray = new AutoArray();
        assertEquals(null, autoArray.getUint8Array());
    }

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
    public void writeReadLength1() throws IOException, ZserioError
    {
        checkWriteRead(AUTO_ARRAY_LENGTH1);
    }

    @Test
    public void writeReadLength2() throws IOException, ZserioError
    {
        checkWriteRead(AUTO_ARRAY_LENGTH2);
    }

    private void checkBitSizeOf(short numElements) throws IOException, ZserioError
    {
        final short[] uint8Array = new short[numElements];
        for (short i = 0; i < numElements; ++i)
            uint8Array[i] = i;

        final AutoArray autoArray = new AutoArray(uint8Array);
        final int bitPosition = 2;
        final int autoArrayBitSize = 8 + numElements * 8;
        assertEquals(autoArrayBitSize, autoArray.bitSizeOf(bitPosition));
    }

    private void checkInitializeOffsets(short numElements) throws IOException, ZserioError
    {
        final short[] uint8Array = new short[numElements];
        for (short i = 0; i < numElements; ++i)
            uint8Array[i] = i;

        final AutoArray autoArray = new AutoArray(uint8Array);
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

        final short[] uint8Array = autoArray.getUint8Array();
        assertEquals(numElements, uint8Array.length);
        for (short i = 0; i < numElements; ++i)
            assertEquals(i, uint8Array[i]);
    }

    private void checkWriteRead(short numElements) throws IOException, ZserioError
    {
        final short[] uint8Array = new short[numElements];
        for (short i = 0; i < numElements; ++i)
            uint8Array[i] = i;

        final AutoArray autoArray = new AutoArray(uint8Array);
        final File file = new File(BLOB_NAME_BASE + numElements + ".blob");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        autoArray.write(writer);
        writer.close();

        assertEquals(autoArray.bitSizeOf(), writer.getBitPosition());
        assertEquals(autoArray.initializeOffsets(), writer.getBitPosition());

        final AutoArray readAutoArray = new AutoArray(file);
        final short[] readUint8Array = readAutoArray.getUint8Array();
        assertEquals(numElements, readUint8Array.length);
        for (short i = 0; i < numElements; ++i)
            assertEquals(i, readUint8Array[i]);
    }

    private void writeAutoArrayToFile(File file, short numElements) throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);

        writer.writeVarSize(numElements);
        for (short i = 0; i < numElements; ++i)
            writer.writeUnsignedByte(i);

        writer.close();
    }

    private static final String BLOB_NAME_BASE = "auto_array_uint8_";
    private static final short AUTO_ARRAY_LENGTH1 = 5;
    private static final short AUTO_ARRAY_LENGTH2 = 10;
}
