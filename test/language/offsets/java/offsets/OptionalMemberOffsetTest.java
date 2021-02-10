package offsets;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.File;

import org.junit.Test;

import offsets.optional_member_offset.OptionalMemberOffset;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class OptionalMemberOffsetTest
{
    @Test
    public void readWithOptional() throws IOException, ZserioError
    {
        final boolean hasOptional = true;
        final int optionalField = 0x1212;
        final int field = 0x2121;
        final File file = new File("test.bin");
        writeOptionalMemberOffsetToFile(file, hasOptional, OPTIONAL_FIELD_OFFSET, optionalField, field);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final OptionalMemberOffset optionalMemberOffset = new OptionalMemberOffset(stream);
        stream.close();
        checkOptionalMemberOffset(optionalMemberOffset, hasOptional, OPTIONAL_FIELD_OFFSET, optionalField,
                field);
    }

    @Test
    public void readWithoutOptional() throws IOException, ZserioError
    {
        final boolean hasOptional = false;
        final long optionalFieldOffset = 0xABCD;
        final int field = 0x2121;
        final File file = new File("test.bin");
        writeOptionalMemberOffsetToFile(file, hasOptional, optionalFieldOffset, null, field);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final OptionalMemberOffset optionalMemberOffset = new OptionalMemberOffset(stream);
        stream.close();
        checkOptionalMemberOffset(optionalMemberOffset, hasOptional, optionalFieldOffset, null, field);
    }

    @Test
    public void bitSizeOfWithOptional()
    {
        final OptionalMemberOffset optionalMemberOffset = new OptionalMemberOffset(true, OPTIONAL_FIELD_OFFSET,
                0x1010, 0x2020);
        assertEquals(WITH_OPTIONAL_MEMBER_OFFSET_BIT_SIZE, optionalMemberOffset.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithoutOptional()
    {
        final OptionalMemberOffset optionalMemberOffset = new OptionalMemberOffset(false, 0xDEAD, null, 0xBEEF);
        assertEquals(WITHOUT_OPTIONAL_MEMBER_OFFSET_BIT_SIZE, optionalMemberOffset.bitSizeOf());
    }

    @Test
    public void initializeOffsetsWithOptional()
    {
        final boolean hasOptional = true;
        final int optionalField = 0x1010;
        final int field = 0x2020;
        final OptionalMemberOffset optionalMemberOffset = new OptionalMemberOffset(hasOptional,
                WRONG_OPTIONAL_FIELD_OFFSET, optionalField, field);
        final int bitPosition = 2;
        assertEquals(WITH_OPTIONAL_MEMBER_OFFSET_BIT_SIZE, optionalMemberOffset.initializeOffsets(bitPosition));
        checkOptionalMemberOffset(optionalMemberOffset, hasOptional, OPTIONAL_FIELD_OFFSET, optionalField,
                field);
    }

    @Test
    public void initializeOffsetsWithoutOptional()
    {
        final boolean hasOptional = false;
        final long optionalFieldOffset = 0xABCD;
        final int field = 0x2020;
        final OptionalMemberOffset optionalMemberOffset = new OptionalMemberOffset(hasOptional,
                optionalFieldOffset, null, field);
        final int bitPosition = 2;
        assertEquals(WITHOUT_OPTIONAL_MEMBER_OFFSET_BIT_SIZE + bitPosition,
                optionalMemberOffset.initializeOffsets(bitPosition));
        checkOptionalMemberOffset(optionalMemberOffset, hasOptional, optionalFieldOffset, null, field);
    }

    @Test
    public void writeWithOptional() throws IOException, ZserioError
    {
        final boolean hasOptional = true;
        final int optionalField = 0x1A1A;
        final int field = 0xA1A1;
        final OptionalMemberOffset optionalMemberOffset = new OptionalMemberOffset(hasOptional,
                WRONG_OPTIONAL_FIELD_OFFSET, optionalField, field);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        optionalMemberOffset.write(writer);
        writer.close();
        checkOptionalMemberOffset(optionalMemberOffset, hasOptional, OPTIONAL_FIELD_OFFSET, optionalField,
                field);
        final OptionalMemberOffset readOptionalMemberOffset = new OptionalMemberOffset(file);
        checkOptionalMemberOffset(readOptionalMemberOffset, hasOptional, OPTIONAL_FIELD_OFFSET, optionalField,
                field);
        assertTrue(optionalMemberOffset.equals(readOptionalMemberOffset));
    }

    @Test
    public void writeWithoutOptional() throws IOException, ZserioError
    {
        final boolean hasOptional = false;
        final long optionalFieldOffset = 0xABCE;
        final int field = 0x7ACF;
        final OptionalMemberOffset optionalMemberOffset = new OptionalMemberOffset(hasOptional,
                optionalFieldOffset, null, field);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        optionalMemberOffset.write(writer);
        writer.close();
        final OptionalMemberOffset readOptionalMemberOffset = new OptionalMemberOffset(file);
        checkOptionalMemberOffset(readOptionalMemberOffset, hasOptional, optionalFieldOffset, null, field);
        assertTrue(optionalMemberOffset.equals(readOptionalMemberOffset));
    }

    private void writeOptionalMemberOffsetToFile(File file, boolean hasOptional, long optionalFieldOffset,
            Integer optionalField, int field) throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);

        writer.writeBool(hasOptional);
        writer.writeUnsignedInt(optionalFieldOffset);
        if (hasOptional)
        {
            writer.writeBits(0, 7);
            writer.writeInt(optionalField);
        }
        writer.writeInt(field);

        writer.close();
    }

    private void checkOptionalMemberOffset(OptionalMemberOffset optionalMemberOffset, boolean hasOptional,
            long optionalFieldOffset, Integer optionalField, int field)
    {
        assertEquals(hasOptional, optionalMemberOffset.getHasOptional());
        assertEquals(optionalFieldOffset, optionalMemberOffset.getOptionalFieldOffset());
        if (hasOptional)
        {
            assertEquals(optionalField, optionalMemberOffset.getOptionalField());
            assertTrue(optionalMemberOffset.isOptionalFieldUsed());
        }
        else
        {
            assertFalse(optionalMemberOffset.isOptionalFieldUsed());
        }
        assertEquals(field, optionalMemberOffset.getField());
    }

    private static final int WITH_OPTIONAL_MEMBER_OFFSET_BIT_SIZE = 104;
    private static final int WITHOUT_OPTIONAL_MEMBER_OFFSET_BIT_SIZE = 65;

    private static final long OPTIONAL_FIELD_OFFSET = (long)5;
    private static final long WRONG_OPTIONAL_FIELD_OFFSET = (long)0;
}
