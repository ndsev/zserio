package array_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;

import zserio.runtime.io.FileBitStreamWriter;
import zserio.runtime.io.BitBuffer;
import array_types.packed_auto_array_struct_with_bytes.PackedAutoArray;
import array_types.packed_auto_array_struct_with_bytes.TestStructure;

public class PackedAutoArrayStructWithBytesTest
{
    @Test
    public void writeReadFile() throws IOException
    {
        final PackedAutoArray packedAutoArray = createPackedAutoArray();
        final File file = new File(BLOB_NAME);
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);
        packedAutoArray.write(writer);
        writer.close();

        assertEquals(writer.getBitPosition(), packedAutoArray.bitSizeOf());
        assertEquals(writer.getBitPosition(), packedAutoArray.initializeOffsets());

        final PackedAutoArray readPackedAutoArray = new PackedAutoArray(file);
        assertEquals(packedAutoArray, readPackedAutoArray);
    }

    private PackedAutoArray createPackedAutoArray()
    {
        final TestStructure[] array = new TestStructure[10];
        for (int i = 0; i < UINT32_FIELD.length; ++i)
            array[i] = new TestStructure(UINT32_FIELD[i], BYTES_FIELD[i], UINT8_FIELD[i]);

        return new PackedAutoArray(array);
    }

    private static final long[] UINT32_FIELD = {
            100000, 110000, 120000, 130000, 140000, 150000, 160000, 170000, 180000, 190000};
    private static final byte[][] BYTES_FIELD = new byte[][]{
            new byte[]{(byte)0xAB, (byte)0xCD, (byte)0xEF}, new byte[]{(byte)0x00},
            new byte[]{(byte)0x01}, new byte[]{(byte)0x00}, new byte[]{(byte)0x01}, new byte[]{(byte)0x00},
            new byte[]{(byte)0x01}, new byte[]{(byte)0x00}, new byte[]{(byte)0x01}, new byte[]{(byte)0x00}};
    private static final short[] UINT8_FIELD = {0, 2, 4, 6, 8, 10, 12, 14, 16, 18};

    private static final String BLOB_NAME = "packed_auto_array_struct_with_bytes.blob";
};
