package offsets;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;

import offsets.ternary_operator_offset.TernaryOffset;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class TernaryOperatorOffsetTest
{
    @Test
    public void firstOffset() throws IOException, ZserioError
    {
        final boolean isFirstOffsetUsed = true;
        testOffset(isFirstOffsetUsed);
    }

    @Test
    public void firstOffsetWriteWrong() throws IOException, ZserioError
    {
        final boolean isFirstOffsetUsed = true;
        testOffsetWriteWrong(isFirstOffsetUsed);
    }

    @Test
    public void firstOffsetReadWrong() throws IOException, ZserioError
    {
        final boolean isFirstOffsetUsed = true;
        testOffsetReadWrong(isFirstOffsetUsed);
    }

    @Test
    public void secondOffset() throws IOException, ZserioError
    {
        final boolean isFirstOffsetUsed = false;
        testOffset(isFirstOffsetUsed);
    }

    @Test
    public void secondOffsetWriteWrong() throws IOException, ZserioError
    {
        final boolean isFirstOffsetUsed = false;
        testOffsetWriteWrong(isFirstOffsetUsed);
    }

    @Test
    public void secondOffsetReadWrong() throws IOException, ZserioError
    {
        final boolean isFirstOffsetUsed = false;
        testOffsetReadWrong(isFirstOffsetUsed);
    }

    private void writeTernaryOffsetToFile(File file, boolean isFirstOffsetUsed, boolean writeWrongOffset)
            throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);

        writer.writeBool(isFirstOffsetUsed);
        if (isFirstOffsetUsed)
        {
            writer.writeBits((writeWrongOffset) ? WRONG_FIELD_OFFSET : FIELD_OFFSET, 32);
            writer.writeBits(WRONG_FIELD_OFFSET, 32);
        }
        else
        {
            writer.writeBits(WRONG_FIELD_OFFSET, 32);
            writer.writeBits((writeWrongOffset) ? WRONG_FIELD_OFFSET : FIELD_OFFSET, 32);
        }
        writer.writeSignedBits(FIELD_VALUE, 32);

        writer.close();
    }

    private void checkTernaryOffset(TernaryOffset ternaryOffset, boolean isFirstOffsetUsed)
    {
        assertEquals(isFirstOffsetUsed, ternaryOffset.getIsFirstOffsetUsed());
        if (isFirstOffsetUsed)
        {
            assertEquals(FIELD_OFFSET, ternaryOffset.getOffsets()[0]);
            assertEquals(WRONG_FIELD_OFFSET, ternaryOffset.getOffsets()[1]);
        }
        else
        {
            assertEquals(WRONG_FIELD_OFFSET, ternaryOffset.getOffsets()[0]);
            assertEquals(FIELD_OFFSET, ternaryOffset.getOffsets()[1]);
        }
        assertEquals(FIELD_VALUE, ternaryOffset.getValue());
    }

    private TernaryOffset createTernaryOffset(boolean isFirstOffsetUsed, boolean createWrongOffset)
    {
        final TernaryOffset ternaryOffset = new TernaryOffset(isFirstOffsetUsed, new long[] {
                WRONG_FIELD_OFFSET, WRONG_FIELD_OFFSET}, FIELD_VALUE);
        if (!createWrongOffset)
            ternaryOffset.initializeOffsets();

        return ternaryOffset;
    }

    private void testOffset(boolean isFirstOffsetUsed) throws IOException, ZserioError
    {
        final boolean writeWrongOffset = false;
        final TernaryOffset ternaryOffset = createTernaryOffset(isFirstOffsetUsed, writeWrongOffset);

        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        ternaryOffset.write(writer);
        writer.close();

        final BitStreamReader reader = new FileBitStreamReader(file);
        final TernaryOffset readTernaryOffset = new TernaryOffset(reader);
        reader.close();

        checkTernaryOffset(readTernaryOffset, isFirstOffsetUsed);
    }

    private void testOffsetWriteWrong(boolean isFirstOffsetUsed) throws IOException, ZserioError
    {
        final boolean writeWrongOffset = true;
        final TernaryOffset ternaryOffset = createTernaryOffset(isFirstOffsetUsed, writeWrongOffset);

        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        assertThrows(ZserioError.class, () -> ternaryOffset.write(writer));
        writer.close();
    }

    private void testOffsetReadWrong(boolean isFirstOffsetUsed) throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        final boolean writeWrongOffset = true;
        writeTernaryOffsetToFile(file, isFirstOffsetUsed, writeWrongOffset);

        final BitStreamReader reader = new FileBitStreamReader(file);
        assertThrows(ZserioError.class, () -> new TernaryOffset(reader));
        reader.close();
    }

    private static final long WRONG_FIELD_OFFSET = 0;
    private static final long FIELD_OFFSET = (1 + 32 + 32 + /* align */ + 7) / 8;
    private static final int FIELD_VALUE = 0xABCD;
}
