package constraints;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import constraints.union_constraints.UnionConstraints;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

public class UnionConstraintsTest
{
    @Test
    public void readCorrectConstraints() throws IOException, ZserioError
    {
        final short value8 = VALUE8_CORRECT_CONSTRAINT;
        final BitBuffer bitBuffer = writeUnionConstraintsToBitBuffer(value8);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final UnionConstraints unionConstraints = new UnionConstraints(reader);
        assertEquals(UnionConstraints.CHOICE_value8, unionConstraints.choiceTag());
        assertEquals(value8, unionConstraints.getValue8());
    }

    @Test
    public void readWrongValue8Constraint() throws IOException, ZserioError
    {
        final short value8 = VALUE8_WRONG_CONSTRAINT;
        final BitBuffer bitBuffer = writeUnionConstraintsToBitBuffer(value8);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        assertThrows(ZserioError.class, () -> new UnionConstraints(reader));
    }

    @Test
    public void readWrongValue16Constraint() throws IOException, ZserioError
    {
        final int value16 = VALUE16_WRONG_CONSTRAINT;
        final BitBuffer bitBuffer = writeUnionConstraintsToBitBuffer(value16);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        assertThrows(ZserioError.class, () -> new UnionConstraints(reader));
    }

    @Test
    public void writeReadCorrectUnionConstraints() throws IOException, ZserioError
    {
        final int value16 = VALUE16_CORRECT_CONSTRAINT;
        final UnionConstraints unionConstraints = new UnionConstraints();
        unionConstraints.setValue16(value16);
        final BitBuffer bitBuffer = SerializeUtil.serialize(unionConstraints);
        final UnionConstraints readUnionConstraints = SerializeUtil.deserialize(
                UnionConstraints.class, bitBuffer);
        assertEquals(UnionConstraints.CHOICE_value16, readUnionConstraints.choiceTag());
        assertEquals(value16, readUnionConstraints.getValue16());
        assertTrue(unionConstraints.equals(readUnionConstraints));
    }

    @Test
    public void writeWrongValue8Constraint() throws IOException, ZserioError
    {
        final short value8 = VALUE8_WRONG_CONSTRAINT;
        final UnionConstraints unionConstraints = new UnionConstraints();
        unionConstraints.setValue8(value8);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> unionConstraints.write(writer));
    }

    @Test
    public void writeWrongValue16Constraint() throws IOException, ZserioError
    {
        final short value16 = VALUE16_WRONG_CONSTRAINT;
        final UnionConstraints unionConstraints = new UnionConstraints();
        unionConstraints.setValue16(value16);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> unionConstraints.write(writer));
    }

    private BitBuffer writeUnionConstraintsToBitBuffer(short value8) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeVarSize(UnionConstraints.CHOICE_value8);
            writer.writeBits(value8, 8);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private BitBuffer writeUnionConstraintsToBitBuffer(int value16) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeVarSize(UnionConstraints.CHOICE_value16);
            writer.writeBits(value16, 16);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private static final short VALUE8_CORRECT_CONSTRAINT = 1;
    private static final short VALUE8_WRONG_CONSTRAINT = 0;

    private static final short VALUE16_CORRECT_CONSTRAINT = 256;
    private static final short VALUE16_WRONG_CONSTRAINT = 255;
}
