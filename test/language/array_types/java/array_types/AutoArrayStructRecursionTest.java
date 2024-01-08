package array_types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

import array_types.auto_array_struct_recursion.AutoArrayRecursion;

public class AutoArrayStructRecursionTest
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
        final AutoArrayRecursion autoArrayRecursion = createAutoArrayRecursion(numElements);
        final int bitPosition = 2;
        final int autoArrayRecursionBitSize = 8 + 8 + numElements * (8 + 8);
        assertEquals(autoArrayRecursionBitSize, autoArrayRecursion.bitSizeOf(bitPosition));
    }

    private void checkInitializeOffsets(short numElements) throws IOException, ZserioError
    {
        final AutoArrayRecursion autoArrayRecursion = createAutoArrayRecursion(numElements);
        final int bitPosition = 2;
        final int expectedEndBitPosition = bitPosition + 8 + 8 + numElements * (8 + 8);
        assertEquals(expectedEndBitPosition, autoArrayRecursion.initializeOffsets(bitPosition));
    }

    private void checkRead(short numElements) throws IOException, ZserioError
    {
        final BitBuffer buffer = writeAutoArrayRecursionToBitBuffer(numElements);
        final AutoArrayRecursion autoArrayRecursion =
                SerializeUtil.deserialize(AutoArrayRecursion.class, buffer);
        checkAutoArrayRecursion(autoArrayRecursion, numElements);
    }

    private void checkWriteRead(short numElements) throws IOException, ZserioError
    {
        final AutoArrayRecursion autoArrayRecursion = createAutoArrayRecursion(numElements);
        final File file = new File(BLOB_NAME_BASE + numElements + ".blob");
        SerializeUtil.serializeToFile(autoArrayRecursion, file);

        final AutoArrayRecursion readAutoArrayRecursion =
                SerializeUtil.deserializeFromFile(AutoArrayRecursion.class, file);
        checkAutoArrayRecursion(readAutoArrayRecursion, numElements);
    }

    private AutoArrayRecursion createAutoArrayRecursion(short numElements)
    {
        final AutoArrayRecursion[] autoArray = new AutoArrayRecursion[numElements];
        for (short i = 1; i <= numElements; ++i)
        {
            final AutoArrayRecursion element = new AutoArrayRecursion(i, new AutoArrayRecursion[0]);
            autoArray[i - 1] = element;
        }

        return new AutoArrayRecursion((short)0, autoArray);
    }

    private void checkAutoArrayRecursion(AutoArrayRecursion autoArrayRecursion, short numElements)
    {
        assertEquals(0, autoArrayRecursion.getId());
        final AutoArrayRecursion[] autoArray = autoArrayRecursion.getAutoArrayRecursion();
        assertEquals(numElements, autoArray.length);
        for (short i = 1; i <= numElements; ++i)
        {
            final AutoArrayRecursion element = autoArray[i - 1];
            assertEquals(i, element.getId());
            assertEquals(0, element.getAutoArrayRecursion().length);
        }
    }

    private BitBuffer writeAutoArrayRecursionToBitBuffer(short numElements) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeUnsignedByte((short)0);
            writer.writeVarSize(numElements);
            for (short i = 1; i <= numElements; ++i)
            {
                writer.writeUnsignedByte(i);
                writer.writeVarSize(0);
            }

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private static final String BLOB_NAME_BASE = "auto_array_struct_recursion_";
    private static final short AUTO_ARRAY_LENGTH1 = 5;
    private static final short AUTO_ARRAY_LENGTH2 = 10;
}
