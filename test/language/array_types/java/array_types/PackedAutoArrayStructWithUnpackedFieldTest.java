package array_types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.SerializeUtil;

import array_types.packed_auto_array_struct_with_unpacked_field.PackedAutoArray;
import array_types.packed_auto_array_struct_with_unpacked_field.TestStructure;

public class PackedAutoArrayStructWithUnpackedFieldTest
{
    @Test
    public void bitSizeOf() throws IOException
    {
        final PackedAutoArray packedAutoArray = createPackedAutoArray();
        assertEquals(PACKED_AUTO_ARRAY_BIT_SIZE, packedAutoArray.bitSizeOf());
    }

    @Test
    public void initializeOffsets() throws IOException
    {
        final PackedAutoArray packedAutoArray = createPackedAutoArray();
        final int bitPosition = 2;
        final int expectedEndBitPosition = bitPosition + PACKED_AUTO_ARRAY_BIT_SIZE;
        assertEquals(expectedEndBitPosition, packedAutoArray.initializeOffsets(bitPosition));
    }

    @Test
    public void writeReadFile() throws IOException
    {
        final PackedAutoArray packedAutoArray = createPackedAutoArray();
        final File file = new File(BLOB_NAME);
        SerializeUtil.serializeToFile(packedAutoArray, file);
        final PackedAutoArray readPackedAutoArray =
                SerializeUtil.deserializeFromFile(PackedAutoArray.class, file);
        assertEquals(packedAutoArray, readPackedAutoArray);
    }

    private PackedAutoArray createPackedAutoArray()
    {
        final TestStructure[] array = new TestStructure[10];
        for (int i = 0; i < UINT8_FIELD.length; ++i)
            array[i] = new TestStructure(UINT8_FIELD[i], UNPACKED_FIELD[i]);

        return new PackedAutoArray(array);
    }

    private static final short[] UINT8_FIELD = {0, 2, 4, 6, 8, 10, 12, 14, 16, 18};
    private static final BigInteger[] UNPACKED_FIELD = {BigInteger.valueOf(5000000), BigInteger.ZERO,
            BigInteger.ONE, BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO,
            BigInteger.ONE, BigInteger.ZERO};

    private static final int UINT8_MAX_BIT_NUMBER = 2;
    private static final int PACKED_AUTO_ARRAY_BIT_SIZE = 8 + // auto array size: varsize
            1 + // uint8Field packing descriptor: isPacked (true)
            6 + // uint8Field is packed: maxBitNumber
            1 + // unpackedField packing descriptor: isPacked (false)
            8 + // UINT8_FIELD[0]
            32 + // UNPACKED_FIELD[0] (4 bytes for the first value)
            9 * (UINT8_MAX_BIT_NUMBER + 1) + // deltas for uint8Field values
            9 * 8; // unpackedField varuint values (1 byte)

    private static final String BLOB_NAME = "packed_auto_array_struct_with_unpacked_field.blob";
};
