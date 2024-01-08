package constraints;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

import constraints.structure_constraints.BasicColor;
import constraints.structure_constraints.ExtendedColor;
import constraints.structure_constraints.StructureConstraints;

public class StructureConstraintsTest
{
    @Test
    public void readConstructorCorrectConstraints() throws IOException, ZserioError
    {
        final BitBuffer bitBuffer =
                writeStructureConstraintsToBitBuffer(BasicColor.BLACK, BasicColor.WHITE, ExtendedColor.PURPLE);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final StructureConstraints structureConstraints = new StructureConstraints(reader);
        assertEquals(BasicColor.BLACK, structureConstraints.getBlackColor());
        assertEquals(BasicColor.WHITE, structureConstraints.getWhiteColor());
        assertEquals(ExtendedColor.PURPLE, structureConstraints.getPurpleColor());
    }

    @Test
    public void readConstructorWrongBlackConstraint() throws IOException, ZserioError
    {
        final BitBuffer bitBuffer =
                writeStructureConstraintsToBitBuffer(BasicColor.RED, BasicColor.WHITE, ExtendedColor.PURPLE);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        assertThrows(ZserioError.class, () -> new StructureConstraints(reader));
    }

    @Test
    public void readConstructorWrongWhiteConstraint() throws IOException, ZserioError
    {
        final BitBuffer bitBuffer =
                writeStructureConstraintsToBitBuffer(BasicColor.BLACK, BasicColor.RED, ExtendedColor.PURPLE);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        assertThrows(ZserioError.class, () -> new StructureConstraints(reader));
    }

    @Test
    public void readConstructorWrongPurpleConstraint() throws IOException, ZserioError
    {
        final BitBuffer bitBuffer =
                writeStructureConstraintsToBitBuffer(BasicColor.BLACK, BasicColor.WHITE, ExtendedColor.LIME);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        assertThrows(ZserioError.class, () -> new StructureConstraints(reader));
    }

    @Test
    public void writeReadCorrectStructureConstraints() throws IOException, ZserioError
    {
        final StructureConstraints structureConstraints =
                new StructureConstraints(BasicColor.BLACK, BasicColor.WHITE, true, ExtendedColor.PURPLE);
        final BitBuffer bitBuffer = SerializeUtil.serialize(structureConstraints);
        final StructureConstraints readStructureConstraints =
                SerializeUtil.deserialize(StructureConstraints.class, bitBuffer);
        assertEquals(BasicColor.BLACK, readStructureConstraints.getBlackColor());
        assertEquals(BasicColor.WHITE, readStructureConstraints.getWhiteColor());
        assertEquals(ExtendedColor.PURPLE, readStructureConstraints.getPurpleColor());
        assertTrue(structureConstraints.equals(readStructureConstraints));
    }

    @Test
    public void writeWrongBlackConstraint() throws IOException, ZserioError
    {
        final StructureConstraints structureConstraints =
                new StructureConstraints(BasicColor.RED, BasicColor.WHITE, true, ExtendedColor.PURPLE);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> structureConstraints.write(writer));
    }

    @Test
    public void writeWrongPurpleConstraint() throws IOException, ZserioError
    {
        final StructureConstraints structureConstraints =
                new StructureConstraints(BasicColor.BLACK, BasicColor.WHITE, true, ExtendedColor.LIME);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> structureConstraints.write(writer));
    }

    @Test
    public void writeWrongWhiteConstraint() throws IOException, ZserioError
    {
        final StructureConstraints structureConstraints =
                new StructureConstraints(BasicColor.BLACK, BasicColor.RED, true, ExtendedColor.PURPLE);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> structureConstraints.write(writer));
    }

    private BitBuffer writeStructureConstraintsToBitBuffer(
            BasicColor blackColor, BasicColor whiteColor, ExtendedColor purpleColor) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBits(blackColor.getValue(), 8);
            writer.writeBool(true);
            writer.writeBits(whiteColor.getValue(), 8);
            writer.writeBool(true);
            writer.writeBits(purpleColor.getValue(), 16);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }
}
