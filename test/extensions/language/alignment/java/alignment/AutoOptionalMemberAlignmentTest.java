package alignment;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

import alignment.auto_optional_member_alignment.AutoOptionalMemberAlignment;

public class AutoOptionalMemberAlignmentTest
{
    @Test
    public void readWithOptional() throws IOException, ZserioError
    {
        final int autoOptionalField = 0x1234;
        final int field = 0x7654;
        final BitBuffer buffer = writeAutoOptionalMemberAlignmentToBitBuffer(autoOptionalField, field);
        final AutoOptionalMemberAlignment autoOptionalMemberAlignment =
                SerializeUtil.deserialize(AutoOptionalMemberAlignment.class, buffer);
        checkAutoOptionalMemberAlignment(autoOptionalMemberAlignment, autoOptionalField, field);
    }

    @Test
    public void readWithoutOptional() throws IOException, ZserioError
    {
        final int field = 0x2222;
        final BitBuffer buffer = writeAutoOptionalMemberAlignmentToBitBuffer(null, field);
        final AutoOptionalMemberAlignment autoOptionalMemberAlignment =
                SerializeUtil.deserialize(AutoOptionalMemberAlignment.class, buffer);
        checkAutoOptionalMemberAlignment(autoOptionalMemberAlignment, null, field);
    }

    @Test
    public void bitSizeOfWithOptional()
    {
        final AutoOptionalMemberAlignment autoOptionalMemberAlignment =
                new AutoOptionalMemberAlignment(0x4433, 0x1122);
        assertEquals(WITH_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE, autoOptionalMemberAlignment.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithoutOptional()
    {
        final AutoOptionalMemberAlignment autoOptionalMemberAlignment =
                new AutoOptionalMemberAlignment(null, 0x7624);
        assertEquals(WITHOUT_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE, autoOptionalMemberAlignment.bitSizeOf());
    }

    @Test
    public void initializeOffsetsWithOptional()
    {
        final AutoOptionalMemberAlignment autoOptionalMemberAlignment =
                new AutoOptionalMemberAlignment(0x1111, 0x3333);
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
        final AutoOptionalMemberAlignment autoOptionalMemberAlignment =
                new AutoOptionalMemberAlignment(null, 0x3334);
        final int bitPosition = 1;
        assertEquals(WITHOUT_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE + bitPosition,
                autoOptionalMemberAlignment.initializeOffsets(bitPosition));
    }

    @Test
    public void writeWithOptional() throws IOException, ZserioError
    {
        final int autoOptionalField = 0x9ADB;
        final int field = 0x8ACD;
        final AutoOptionalMemberAlignment autoOptionalMemberAlignment =
                new AutoOptionalMemberAlignment(autoOptionalField, field);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        autoOptionalMemberAlignment.write(writer);

        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final AutoOptionalMemberAlignment readAutoOptionalMemberAlignment =
                new AutoOptionalMemberAlignment(reader);
        checkAutoOptionalMemberAlignment(readAutoOptionalMemberAlignment, autoOptionalField, field);
        assertTrue(autoOptionalMemberAlignment.equals(readAutoOptionalMemberAlignment));
    }

    @Test
    public void writeWithoutOptional() throws IOException, ZserioError
    {
        final int field = 0x7ACF;
        final AutoOptionalMemberAlignment autoOptionalMemberAlignment =
                new AutoOptionalMemberAlignment(null, field);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        autoOptionalMemberAlignment.write(writer);

        final ByteArrayBitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final AutoOptionalMemberAlignment readAutoOptionalMemberAlignment =
                new AutoOptionalMemberAlignment(reader);
        checkAutoOptionalMemberAlignment(readAutoOptionalMemberAlignment, null, field);
        assertTrue(autoOptionalMemberAlignment.equals(readAutoOptionalMemberAlignment));
    }

    private BitBuffer writeAutoOptionalMemberAlignmentToBitBuffer(Integer autoOptionalField, int field)
            throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
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

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private void checkAutoOptionalMemberAlignment(
            AutoOptionalMemberAlignment autoOptionalMemberAlignment, Integer autoOptionalField, int field)
    {
        if (autoOptionalField != null)
        {
            assertEquals(autoOptionalField, autoOptionalMemberAlignment.getAutoOptionalField());
            assertTrue(autoOptionalMemberAlignment.isAutoOptionalFieldUsed());
        }
        else
        {
            assertFalse(autoOptionalMemberAlignment.isAutoOptionalFieldUsed());
        }
        assertEquals(field, (int)autoOptionalMemberAlignment.getField());
    }

    private static final int WITH_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE = 96;
    private static final int WITHOUT_AUTO_OPTIONAL_MEMBER_ALIGNMENT_BIT_SIZE = 33;
}
