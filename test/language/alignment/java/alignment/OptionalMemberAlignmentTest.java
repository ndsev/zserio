package alignment;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.File;

import org.junit.Test;

import alignment.optional_member_alignment.OptionalMemberAlignment;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class OptionalMemberAlignmentTest
{
    @Test
    public void readWithOptional() throws IOException, ZserioError
    {
        final boolean hasOptional = true;
        final int optionalField = 0x1234;
        final int field = 0x7654;
        final File file = new File("test.bin");
        writeOptionalMemberAlignmentToFile(file, hasOptional, optionalField, field);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final OptionalMemberAlignment optionalMemberAlignment = new OptionalMemberAlignment(stream);
        stream.close();
        checkOptionalMemberAlignment(optionalMemberAlignment, hasOptional, optionalField, field);
    }

    @Test
    public void readWithoutOptional() throws IOException, ZserioError
    {
        final boolean hasOptional = false;
        final int field = 0x2222;
        final File file = new File("test.bin");
        writeOptionalMemberAlignmentToFile(file, hasOptional, null, field);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final OptionalMemberAlignment optionalMemberAlignment = new OptionalMemberAlignment(stream);
        stream.close();
        checkOptionalMemberAlignment(optionalMemberAlignment, hasOptional, null, field);
    }

    @Test
    public void bitSizeOfWithOptional()
    {
        final OptionalMemberAlignment optionalMemberAlignment = new OptionalMemberAlignment(true, 0x4433,
                0x1122);
        assertEquals(WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE, optionalMemberAlignment.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithoutOptional()
    {
        final OptionalMemberAlignment optionalMemberAlignment = new OptionalMemberAlignment(false, null,
                0x7624);
        assertEquals(WITHOUT_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE, optionalMemberAlignment.bitSizeOf());
    }

    @Test
    public void initializeOffsetsWithOptional()
    {
        final OptionalMemberAlignment optionalMemberAlignment = new OptionalMemberAlignment(true, 0x1111,
                0x3333);
        int bitPosition = 0;
        for (; bitPosition < 32; ++bitPosition)
            assertEquals(WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE,
                    optionalMemberAlignment.initializeOffsets(bitPosition));

        assertEquals(WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE + bitPosition,
                optionalMemberAlignment.initializeOffsets(bitPosition));
    }

    @Test
    public void initializeOffsetsWithoutOptional()
    {
        final OptionalMemberAlignment optionalMemberAlignment = new OptionalMemberAlignment(false, null,
                0x3334);
        final int bitPosition = 1;
        assertEquals(WITHOUT_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE + bitPosition,
                optionalMemberAlignment.initializeOffsets(bitPosition));
    }

    @Test
    public void writeWithOptional() throws IOException, ZserioError
    {
        final boolean hasOptional = true;
        final int optionalField = 0x9ADB;
        final int field = 0x8ACD;
        final OptionalMemberAlignment optionalMemberAlignment = new OptionalMemberAlignment(hasOptional,
                optionalField, field);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        optionalMemberAlignment.write(writer);
        writer.close();
        final OptionalMemberAlignment readOptionalMemberAlignment = new OptionalMemberAlignment(file);
        checkOptionalMemberAlignment(readOptionalMemberAlignment, hasOptional, optionalField, field);
        assertTrue(optionalMemberAlignment.equals(readOptionalMemberAlignment));
    }

    @Test
    public void writeWithoutOptional() throws IOException, ZserioError
    {
        final boolean hasOptional = false;
        final int field = 0x7ACF;
        final OptionalMemberAlignment optionalMemberAlignment = new OptionalMemberAlignment(hasOptional,
                null, field);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        optionalMemberAlignment.write(writer);
        writer.close();
        final OptionalMemberAlignment readOptionalMemberAlignment = new OptionalMemberAlignment(file);
        checkOptionalMemberAlignment(readOptionalMemberAlignment, hasOptional, null, field);
        assertTrue(optionalMemberAlignment.equals(readOptionalMemberAlignment));
    }

    private void writeOptionalMemberAlignmentToFile(File file, boolean hasOptional, Integer optionalField,
            int field) throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);

        writer.writeBool(hasOptional);
        if (hasOptional)
        {
            writer.writeBits(0, 31);
            writer.writeInt(optionalField);
        }
        writer.writeInt(field);

        writer.close();
    }

    private void checkOptionalMemberAlignment(OptionalMemberAlignment optionalMemberAlignment,
            boolean hasOptional, Integer optionalField, int field)
    {
        assertEquals(hasOptional, optionalMemberAlignment.getHasOptional());
        if (hasOptional)
        {
            assertEquals(optionalField, optionalMemberAlignment.getOptionalField());
            assertTrue(optionalMemberAlignment.isOptionalFieldUsed());
        }
        else
        {
            assertFalse(optionalMemberAlignment.isOptionalFieldUsed());
        }
        assertEquals(field, (int)optionalMemberAlignment.getField());
    }

    private static final int WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE = 96;
    private static final int WITHOUT_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE = 33;
}
