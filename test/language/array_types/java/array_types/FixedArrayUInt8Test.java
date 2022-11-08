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

import array_types.fixed_array_uint8.FixedArray;

public class FixedArrayUInt8Test
{
    @Test
    public void emptyConstructor()
    {
        final FixedArray fixedArray = new FixedArray();
        assertEquals(null, fixedArray.getUint8Array());
    }

    @Test
    public void bitSizeOf() throws IOException, ZserioError
    {
        final short[] uint8Array = new short[FIXED_ARRAY_LENGTH];
        for (short i = 0; i < FIXED_ARRAY_LENGTH; ++i)
            uint8Array[i] = i;

        final FixedArray fixedArray = new FixedArray(uint8Array);
        final int bitPosition = 2;
        final int fixedArrayBitSize = FIXED_ARRAY_LENGTH * 8;
        assertEquals(fixedArrayBitSize, fixedArray.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets() throws IOException, ZserioError
    {
        final short[] uint8Array = new short[FIXED_ARRAY_LENGTH];
        for (short i = 0; i < FIXED_ARRAY_LENGTH; ++i)
            uint8Array[i] = i;

        final FixedArray fixedArray = new FixedArray(uint8Array);
        final int bitPosition = 2;
        final int expectedEndBitPosition = bitPosition + FIXED_ARRAY_LENGTH * 8;
        assertEquals(expectedEndBitPosition, fixedArray.initializeOffsets(bitPosition));
    }

    @Test
    public void read() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        writeFixedArrayToFile(file);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final FixedArray fixedArray = new FixedArray(stream);
        stream.close();

        final short[] uint8Array = fixedArray.getUint8Array();
        assertEquals(FIXED_ARRAY_LENGTH, uint8Array.length);
        for (short i = 0; i < FIXED_ARRAY_LENGTH; ++i)
            assertEquals(i, uint8Array[i]);
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final short[] uint8Array = new short[FIXED_ARRAY_LENGTH];
        for (short i = 0; i < FIXED_ARRAY_LENGTH; ++i)
            uint8Array[i] = i;

        FixedArray fixedArray = new FixedArray(uint8Array);
        final File file = new File(BLOB_NAME);
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        fixedArray.write(writer);
        writer.close();

        assertEquals(fixedArray.bitSizeOf(), writer.getBitPosition());
        assertEquals(fixedArray.initializeOffsets(), writer.getBitPosition());

        final FixedArray readFixedArray = new FixedArray(file);
        final short[] readUint8Array = readFixedArray.getUint8Array();
        assertEquals(FIXED_ARRAY_LENGTH, readUint8Array.length);
        for (short i = 0; i < FIXED_ARRAY_LENGTH; ++i)
            assertEquals(i, readUint8Array[i]);
    }

    @Test
    public void writeWrongArray() throws IOException, ZserioError
    {
        final short[] uint8Array = new short[FIXED_ARRAY_LENGTH + 1];
        for (short i = 0; i < FIXED_ARRAY_LENGTH; ++i)
            uint8Array[i] = i;

        FixedArray fixedArray = new FixedArray(uint8Array);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        assertThrows(ZserioError.class, () -> fixedArray.write(writer));
        writer.close();
    }

    private void writeFixedArrayToFile(File file) throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);

        for (short i = 0; i < FIXED_ARRAY_LENGTH; ++i)
            writer.writeUnsignedByte(i);

        writer.close();
    }

    private static final String BLOB_NAME = "fixed_array_uint8.blob";
    private static final int FIXED_ARRAY_LENGTH = 5;
}
