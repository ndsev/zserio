package alignment;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.File;

import org.junit.Test;

import alignment.auto_optional_member_alignment.AutoOptionalMemberAlignment;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class AutoOptionalMemberAlignmentTest
{
    @Test
    public void readWithOptional() throws IOException, ZserioError
    {
        final int autoOptionalField = 0x1234;
        final int field = 0x7654;
        final File file = new File("test.bin");
        writeAutoOptionalMemberAlignmentToFile(file, autoOptionalField, field);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final AutoOptionalMemberAlignment autoOptionalMemberAlignment = new AutoOptionalMemberAlignment(stream);
        stream.close();
        checkAutoOptionalMemberAlignment(autoOptionalMemberAlignment, autoOptionalField, field);
    }

    @Test
    public void readWithoutOptional() throws IOException, ZserioError
    {
        final int field = 0x2222;
        final File file = new File("test.bin");
        writeAutoOptionalMemberAlignmentToFile(file, null, field);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final AutoOptionalMemberAlignment autoOptionalMemberAlignment = new AutoOptionalMemberAlignment(stream);
        stream.close();
        checkAutoOptionalMemberAlignment(autoOptionalMemberAlignment, null, field);
    }

    @Test
    public void bitSizeOfWithOptional()
    {
        final AutoOptionalMemberAlignment autoOptionalMemberAlignment = new AutoOptionalMemberAlignment(0x4433,
                0x1122);
        assertEquals(WITH_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE, autoOptionalMemberAlignment.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithoutOptional()
    {
        final AutoOptionalMemberAlignment autoOptionalMemberAlignment = new AutoOptionalMemberAlignment(null,
                0x7624);
        assertEquals(WITHOUT_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE, autoOptionalMemberAlignment.bitSizeOf());
    }

    @Test
    public void initializeOffsetsWithOptional()
    {
        final AutoOptionalMemberAlignment autoOptionalMemberAlignment = new AutoOptionalMemberAlignment(0x1111,
                0x3333);
        int bitPosition = 0;
        for (; bitPosition < 32; ++bitPosition)
            assertEquals(WITH_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE,
                    autoOptionalMemberAlignment.initializeOffsets(bitPosition));

        assertEquals(WITH_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE + bitPosition,
                autoOptionalMemberAlignment.initializeOffsets(bitPosition));
    }

    @Test
    public void initializeOffsetsWithoutOptional()
    {
        final AutoOptionalMemberAlignment autoOptionalMemberAlignment = new AutoOptionalMemberAlignment(null,
                0x3334);
        final int bitPosition = 1;
        assertEquals(WITHOUT_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE + bitPosition,
                autoOptionalMemberAlignment.initializeOffsets(bitPosition));
    }

    @Test
    public void writeWithOptional() throws IOException, ZserioError
    {
        final int autoOptionalField = 0x9ADB;
        final int field = 0x8ACD;
        final AutoOptionalMemberAlignment autoOptionalMemberAlignment = new AutoOptionalMemberAlignment(
                autoOptionalField, field);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        autoOptionalMemberAlignment.write(writer);
        writer.close();
        final AutoOptionalMemberAlignment readAutoOptionalMemberAlignment =
                new AutoOptionalMemberAlignment(file);
        checkAutoOptionalMemberAlignment(readAutoOptionalMemberAlignment, autoOptionalField, field);
        assertTrue(autoOptionalMemberAlignment.equals(readAutoOptionalMemberAlignment));
    }

    @Test
    public void writeWithoutOptional() throws IOException, ZserioError
    {
        final int field = 0x7ACF;
        final AutoOptionalMemberAlignment autoOptionalMemberAlignment = new AutoOptionalMemberAlignment(
                null, field);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        autoOptionalMemberAlignment.write(writer);
        writer.close();
        final AutoOptionalMemberAlignment readAutoOptionalMemberAlignment =
                new AutoOptionalMemberAlignment(file);
        checkAutoOptionalMemberAlignment(readAutoOptionalMemberAlignment, null, field);
        assertTrue(autoOptionalMemberAlignment.equals(readAutoOptionalMemberAlignment));
    }

    private void writeAutoOptionalMemberAlignmentToFile(File file, Integer autoOptionalField, int field)
            throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);

        if (autoOptionalField != null)
        {
            writer.writeBool(true);
            writer.writeBits(0, 31);
            writer.writeInt(autoOptionalField);
        }
        else
        {
            writer.writeBool(false);
        }
        writer.writeInt(field);

        writer.close();
    }

    private void checkAutoOptionalMemberAlignment(AutoOptionalMemberAlignment autoOptionalMemberAlignment,
            Integer autoOptionalField, int field)
    {
        if (autoOptionalField != null)
        {
            assertEquals(autoOptionalField, autoOptionalMemberAlignment.getAutoOptionalField());
            assertTrue(autoOptionalMemberAlignment.hasAutoOptionalField());
        }
        else
        {
            assertFalse(autoOptionalMemberAlignment.hasAutoOptionalField());
        }
        assertEquals(field, (int)autoOptionalMemberAlignment.getField());
    }

    private static final int WITH_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE = 96;
    private static final int WITHOUT_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE = 33;
}
