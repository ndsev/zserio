package alignment;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

import alignment.optional_member_alignment.OptionalMemberAlignment;

public class OptionalMemberAlignmentTest
{
    @Test
    public void readWithOptional() throws IOException, ZserioError
    {
        final boolean hasOptional = true;
        final int optionalField = 0x1234;
        final int field = 0x7654;
        final BitBuffer buffer = writeOptionalMemberAlignmentToBitBuffer(hasOptional, optionalField, field);
        final OptionalMemberAlignment optionalMemberAlignment =
                SerializeUtil.deserialize(OptionalMemberAlignment.class, buffer);
        checkOptionalMemberAlignment(optionalMemberAlignment, hasOptional, optionalField, field);
    }

    @Test
    public void readWithoutOptional() throws IOException, ZserioError
    {
        final boolean hasOptional = false;
        final int field = 0x2222;
        final BitBuffer buffer = writeOptionalMemberAlignmentToBitBuffer(hasOptional, null, field);
        final OptionalMemberAlignment optionalMemberAlignment =
                SerializeUtil.deserialize(OptionalMemberAlignment.class, buffer);
        checkOptionalMemberAlignment(optionalMemberAlignment, hasOptional, null, field);
    }

    @Test
    public void bitSizeOfWithOptional()
    {
        final OptionalMemberAlignment optionalMemberAlignment =
                new OptionalMemberAlignment(true, 0x4433, 0x1122);
        assertEquals(WITH_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE, optionalMemberAlignment.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithoutOptional()
    {
        final OptionalMemberAlignment optionalMemberAlignment =
                new OptionalMemberAlignment(false, null, 0x7624);
        assertEquals(WITHOUT_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE, optionalMemberAlignment.bitSizeOf());
    }

    @Test
    public void initializeOffsetsWithOptional()
    {
        final OptionalMemberAlignment optionalMemberAlignment =
                new OptionalMemberAlignment(true, 0x1111, 0x3333);
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
        final OptionalMemberAlignment optionalMemberAlignment =
                new OptionalMemberAlignment(false, null, 0x3334);
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
        final OptionalMemberAlignment optionalMemberAlignment =
                new OptionalMemberAlignment(hasOptional, optionalField, field);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        optionalMemberAlignment.write(writer);
        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final OptionalMemberAlignment readOptionalMemberAlignment = new OptionalMemberAlignment(reader);
        checkOptionalMemberAlignment(readOptionalMemberAlignment, hasOptional, optionalField, field);
        assertTrue(optionalMemberAlignment.equals(readOptionalMemberAlignment));
    }

    @Test
    public void writeWithoutOptional() throws IOException, ZserioError
    {
        final boolean hasOptional = false;
        final int field = 0x7ACF;
        final OptionalMemberAlignment optionalMemberAlignment =
                new OptionalMemberAlignment(hasOptional, null, field);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        optionalMemberAlignment.write(writer);
        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final OptionalMemberAlignment readOptionalMemberAlignment = new OptionalMemberAlignment(reader);
        checkOptionalMemberAlignment(readOptionalMemberAlignment, hasOptional, null, field);
        assertTrue(optionalMemberAlignment.equals(readOptionalMemberAlignment));
    }

    private BitBuffer writeOptionalMemberAlignmentToBitBuffer(
            boolean hasOptional, Integer optionalField, int field) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBool(hasOptional);
            if (hasOptional)
            {
                writer.writeBits(0, 31);
                writer.writeInt(optionalField);
            }
            writer.writeInt(field);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
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
