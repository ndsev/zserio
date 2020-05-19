package offsets;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import offsets.nested_offset.NestedOffset;
import offsets.nested_offset.NestedOffsetArrayStructure;
import offsets.nested_offset.NestedOffsetChoice;
import offsets.nested_offset.NestedOffsetStructure;
import offsets.nested_offset.NestedOffsetUnion;

import zserio.runtime.ZserioError;
import zserio.runtime.array.ObjectArray;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class NestedOffsetTest
{
    @Test
    public void read() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = false;
        final File file = new File("test.bin");
        writeNestedOffsetToFile(file, writeWrongOffsets);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final NestedOffset nestedOffset = new NestedOffset(stream);
        stream.close();
        checkNestedOffset(nestedOffset);
    }

    @Test(expected=ZserioError.class)
    public void readWrongOffsets() throws IOException, ZserioError
    {
        final boolean writeWrongOffsets = true;
        final File file = new File("test.bin");
        writeNestedOffsetToFile(file, writeWrongOffsets);
        final BitStreamReader stream = new FileBitStreamReader(file);
        new NestedOffset(stream);
        stream.close();
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
    public void write() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = true;
        final NestedOffset nestedOffset = createNestedOffset(createWrongOffsets);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        nestedOffset.write(writer);
        writer.close();
        checkNestedOffset(nestedOffset);
        final NestedOffset readNestedOffset = new NestedOffset(file);
        checkNestedOffset(readNestedOffset);
        assertTrue(nestedOffset.equals(readNestedOffset));
    }

    @Test
    public void writeWithPosition() throws IOException, ZserioError
    {
        final boolean createWrongOffsets = true;
        final NestedOffset nestedOffset = createNestedOffset(createWrongOffsets);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        final int bitPosition = 2;
        writer.writeBits(0, bitPosition);
        nestedOffset.write(writer);
        writer.close();
        checkNestedOffset(nestedOffset);
    }

    @Test(expected=ZserioError.class)
    public void writeWrongOffsets() throws ZserioError, IOException
    {
        final boolean createWrongOffsets = true;
        final NestedOffset nestedOffset = createNestedOffset(createWrongOffsets);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        nestedOffset.write(writer, false);
        writer.close();
    }

    private void writeNestedOffsetToFile(File file, boolean writeWrongOffsets) throws IOException
    {
        final FileBitStreamWriter writer = new FileBitStreamWriter(file);

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

        writer.close();
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

        final ObjectArray<NestedOffsetStructure> nestedOffsetStructureList =
                nestedOffsetArrayStructure.getNestedOffsetStructureList();
        assertEquals(NUM_ELEMENTS, nestedOffsetStructureList.length());
        for (short i = 0; i < NUM_ELEMENTS; ++i)
        {
            final NestedOffsetStructure nestedOffsetStructure = nestedOffsetStructureList.elementAt(i);
            assertEquals(FIRST_DATA_OFFSET + i * 8L, nestedOffsetStructure.getDataOffset());
            assertEquals(i, nestedOffsetStructure.getData());
        }

        assertEquals(TERMINATOR_VALUE, nestedOffset.getTerminator());
    }

    private NestedOffset createNestedOffset(boolean createWrongOffsets)
    {
        final List<NestedOffsetStructure> nestedOffsetStructureList = new ArrayList<NestedOffsetStructure>();
        for (short i = 0; i < NUM_ELEMENTS; ++i)
        {
            final long dataOffset = (createWrongOffsets) ? WRONG_DATA_OFFSET : FIRST_DATA_OFFSET + i * 8L;
            nestedOffsetStructureList.add(new NestedOffsetStructure(dataOffset, i));
        }

        final NestedOffsetArrayStructure nestedOffsetArrayStructure = new NestedOffsetArrayStructure(
                NUM_ELEMENTS, new ObjectArray<NestedOffsetStructure>(nestedOffsetStructureList));

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
