package offsets;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import offsets.nested_offset.NestedOffset;
import offsets.nested_offset.NestedOffsetArrayStructure;
import offsets.nested_offset.NestedOffsetChoice;
import offsets.nested_offset.NestedOffsetStructure;
import offsets.nested_offset.NestedOffsetUnion;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

public class NestedOffsetTest
{
    @Test
    public void readConstructor() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = false;
        final BitBuffer bitBuffer = writeNestedOffsetToBitBuffer(writeWrongOffsets);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final NestedOffset nestedOffset = new NestedOffset(reader);
        checkNestedOffset(nestedOffset);
    }

    @Test
    public void readConstructorWrongOffsets() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = true;
        final BitBuffer bitBuffer = writeNestedOffsetToBitBuffer(writeWrongOffsets);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        assertThrows(ZserioError.class, () -> new NestedOffset(reader));
    }

    @Test
    public void bitSizeOf()
    {
        final boolean createWrongOffsets = false;
        final NestedOffset nestedOffset = createNestedOffset(createWrongOffsets);
        assertEquals(NEST_OFFSET_BIT_SIZE, nestedOffset.bitSizeOf());
    }

    @Test
    public void bitSizeOfWithPosition()
    {
        final boolean createWrongOffsets = false;
        final NestedOffset nestedOffset = createNestedOffset(createWrongOffsets);
        final int bitPosition = 2;
        assertEquals(NEST_OFFSET_BIT_SIZE - bitPosition, nestedOffset.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets()
    {
        final boolean createWrongOffsets = true;
        final NestedOffset nestedOffset = createNestedOffset(createWrongOffsets);
        final int bitPosition = 0;
        assertEquals(NEST_OFFSET_BIT_SIZE, nestedOffset.initializeOffsets(bitPosition));
        checkNestedOffset(nestedOffset);
    }

    @Test
    public void initializeOffsetsWithPosition()
    {
        final boolean createWrongOffsets = true;
        final NestedOffset nestedOffset = createNestedOffset(createWrongOffsets);
        final int bitPosition = 2;
        assertEquals(NEST_OFFSET_BIT_SIZE , nestedOffset.initializeOffsets(bitPosition));
        checkNestedOffset(nestedOffset);
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = false;
        final NestedOffset nestedOffset = createNestedOffset(createWrongOffsets);
        final BitBuffer bitBuffer = SerializeUtil.serialize(nestedOffset);
        checkNestedOffset(nestedOffset);

        final NestedOffset readNestedOffset = SerializeUtil.deserialize(NestedOffset.class, bitBuffer);
        checkNestedOffset(readNestedOffset);
        assertTrue(nestedOffset.equals(readNestedOffset));
    }

    @Test
    public void writeReadWithPosition() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = true;
        final NestedOffset nestedOffset = createNestedOffset(createWrongOffsets);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        final int bitPosition = 2;
        writer.writeBits(0, bitPosition);
        nestedOffset.initializeOffsets(writer.getBitPosition());
        nestedOffset.write(writer);
        checkNestedOffset(nestedOffset);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        assertEquals(0, reader.readBits(bitPosition));
        final NestedOffset readNestedOffset = new NestedOffset(reader);
        checkNestedOffset(readNestedOffset);
        assertTrue(nestedOffset.equals(readNestedOffset));
    }

    @Test
    public void writeWrongOffsets() throws ZserioError, IOException
    {
        final boolean createWrongOffsets = true;
        final NestedOffset nestedOffset = createNestedOffset(createWrongOffsets);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> nestedOffset.write(writer));
        writer.close();
    }

    private BitBuffer writeNestedOffsetToBitBuffer(boolean writeWrongOffsets) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeUnsignedInt((writeWrongOffsets) ? WRONG_TERMINATOR_OFFSET : TERMINATOR_OFFSET);
            writer.writeBool(BOOL_VALUE);
            writer.writeVarSize(NestedOffsetUnion.CHOICE_nestedOffsetArrayStructure); // union's choice tag
            writer.writeUnsignedByte(NUM_ELEMENTS);
            for (short i = 0; i < NUM_ELEMENTS; ++i)
            {
                writer.writeUnsignedInt((writeWrongOffsets) ? WRONG_DATA_OFFSET : FIRST_DATA_OFFSET + i * 8L);
                writer.writeBits(0, (i == 0) ? 7 : 1);
                writer.writeBits(i, 31);
            }

            writer.alignTo(8);
            writer.writeBits(TERMINATOR_VALUE, 7);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private void checkNestedOffset(NestedOffset nestedOffset)
    {
        assertEquals(TERMINATOR_OFFSET, nestedOffset.getTerminatorOffset());
        assertEquals(BOOL_VALUE, nestedOffset.getBoolValue());

        final NestedOffsetChoice nestedOffsetChoice = nestedOffset.getNestedOffsetChoice();
        assertEquals(BOOL_VALUE, nestedOffsetChoice.getType());

        final NestedOffsetUnion nestedOffsetUnion = nestedOffsetChoice.getNestedOffsetUnion();
        assertEquals(NestedOffsetUnion.CHOICE_nestedOffsetArrayStructure, nestedOffsetUnion.choiceTag());

        final NestedOffsetArrayStructure nestedOffsetArrayStructure =
                nestedOffsetUnion.getNestedOffsetArrayStructure();
        assertEquals(NUM_ELEMENTS, nestedOffsetArrayStructure.getNumElements());

        final NestedOffsetStructure[] nestedOffsetStructureList =
                nestedOffsetArrayStructure.getNestedOffsetStructureList();
        assertEquals(NUM_ELEMENTS, nestedOffsetStructureList.length);
        for (short i = 0; i < NUM_ELEMENTS; ++i)
        {
            final NestedOffsetStructure nestedOffsetStructure = nestedOffsetStructureList[i];
            assertEquals(FIRST_DATA_OFFSET + i * 8L, nestedOffsetStructure.getDataOffset());
            assertEquals(i, nestedOffsetStructure.getData());
        }

        assertEquals(TERMINATOR_VALUE, nestedOffset.getTerminator());
    }

    private NestedOffset createNestedOffset(boolean createWrongOffsets)
    {
        final NestedOffsetStructure[] nestedOffsetStructureList = new NestedOffsetStructure[NUM_ELEMENTS];
        for (short i = 0; i < NUM_ELEMENTS; ++i)
        {
            final long dataOffset = (createWrongOffsets) ? WRONG_DATA_OFFSET : FIRST_DATA_OFFSET + i * 8L;
            nestedOffsetStructureList[i] = new NestedOffsetStructure(dataOffset, i);
        }

        final NestedOffsetArrayStructure nestedOffsetArrayStructure =
                new NestedOffsetArrayStructure(NUM_ELEMENTS, nestedOffsetStructureList);

        final NestedOffsetUnion nestedOffsetUnion = new NestedOffsetUnion();
        nestedOffsetUnion.setNestedOffsetArrayStructure(nestedOffsetArrayStructure);

        final NestedOffsetChoice nestedOffsetChoice = new NestedOffsetChoice(BOOL_VALUE);
        nestedOffsetChoice.setNestedOffsetUnion(nestedOffsetUnion);

        final long terminatorOffset = (createWrongOffsets) ? WRONG_TERMINATOR_OFFSET : TERMINATOR_OFFSET;
        final NestedOffset nestedOffset = new NestedOffset(terminatorOffset, BOOL_VALUE, nestedOffsetChoice,
                TERMINATOR_VALUE);

        return nestedOffset;
    }

    private static final boolean BOOL_VALUE = true;
    private static final short NUM_ELEMENTS = 2;

    private static final long WRONG_TERMINATOR_OFFSET = 0;
    private static final long TERMINATOR_OFFSET = 7 + NUM_ELEMENTS * 8;

    private static final long WRONG_DATA_OFFSET = 0;
    private static final long FIRST_DATA_OFFSET = 7 + 4;

    private static final byte TERMINATOR_VALUE = 0x45;

    private static final int NEST_OFFSET_BIT_SIZE = (int)(TERMINATOR_OFFSET * 8 + 7);
}
