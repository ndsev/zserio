package constraints;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import constraints.choice_constraints.ChoiceConstraints;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

public class ChoiceConstraintsTest
{
    @Test
    public void readConstructorCorrectConstraints() throws IOException, ZserioError
    {
        final boolean selector = true;
        final short value8 = VALUE8_CORRECT_CONSTRAINT;
        final BitBuffer bitBuffer = writeChoiceConstraintsToBitBuffer(selector, value8, 0);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        final ChoiceConstraints choiceConstraints = new ChoiceConstraints(reader, selector);
        assertEquals(selector, choiceConstraints.getSelector());
        assertEquals(value8, choiceConstraints.getValue8());
    }

    @Test
    public void readConstructorWrongValue8Constraint() throws IOException, ZserioError
    {
        final boolean selector = true;
        final short value8 = VALUE8_WRONG_CONSTRAINT;
        final BitBuffer bitBuffer = writeChoiceConstraintsToBitBuffer(selector, value8, 0);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        assertThrows(ZserioError.class, () -> new ChoiceConstraints(reader, selector));
    }

    @Test
    public void readConstructorWrongValue16Constraint() throws IOException, ZserioError
    {
        final boolean selector = false;
        final int value16 = VALUE16_WRONG_CONSTRAINT;
        final BitBuffer bitBuffer = writeChoiceConstraintsToBitBuffer(selector, (short)0, value16);
        final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer);
        assertThrows(ZserioError.class, () -> new ChoiceConstraints(reader, selector));
    }

    @Test
    public void writeReadCorrectChoiceConstraints() throws IOException, ZserioError
    {
        final boolean selector = false;
        final int value16 = VALUE16_CORRECT_CONSTRAINT;
        final ChoiceConstraints choiceConstraints = new ChoiceConstraints(selector);
        choiceConstraints.setValue16(value16);
        final BitBuffer bitBuffer = SerializeUtil.serialize(choiceConstraints);

        final ChoiceConstraints readChoiceConstraints = SerializeUtil.deserialize(
                ChoiceConstraints.class, bitBuffer, selector);
        assertEquals(selector, readChoiceConstraints.getSelector());
        assertEquals(value16, readChoiceConstraints.getValue16());
        assertTrue(choiceConstraints.equals(readChoiceConstraints));
    }

    @Test
    public void writeWrongValue8Constraint() throws IOException, ZserioError
    {
        final boolean selector = true;
        final short value8 = VALUE8_WRONG_CONSTRAINT;
        final ChoiceConstraints choiceConstraints = new ChoiceConstraints(selector);
        choiceConstraints.setValue8(value8);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> choiceConstraints.write(writer));
    }

    @Test
    public void writeWrongValue16Constraint() throws IOException, ZserioError
    {
        final boolean selector = false;
        final short value16 = VALUE16_WRONG_CONSTRAINT;
        final ChoiceConstraints choiceConstraints = new ChoiceConstraints(selector);
        choiceConstraints.setValue16(value16);
        final BitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> choiceConstraints.write(writer));
    }

    private BitBuffer writeChoiceConstraintsToBitBuffer(boolean selector, short value8, int value16)
            throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            if (selector)
                writer.writeBits(value8, 8);
            else
                writer.writeBits(value16, 16);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private static final short VALUE8_CORRECT_CONSTRAINT = 1;
    private static final short VALUE8_WRONG_CONSTRAINT = 0;

    private static final short VALUE16_CORRECT_CONSTRAINT = 256;
    private static final short VALUE16_WRONG_CONSTRAINT = 255;
}
