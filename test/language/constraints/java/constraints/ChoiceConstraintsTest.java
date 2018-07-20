package constraints;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import org.junit.Test;

import constraints.choice_constraints.ChoiceConstraints;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class ChoiceConstraintsTest
{
    @Test
    public void readCorrectConstraints() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        final boolean selector = true;
        final short value8 = VALUE8_CORRECT_CONSTRAINT;
        writeChoiceConstraintsToFile(file, selector, value8, 0);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final ChoiceConstraints choiceConstraints = new ChoiceConstraints(stream, selector);
        stream.close();
        assertEquals(selector, choiceConstraints.getSelector());
        assertEquals(value8, choiceConstraints.getValue8());
    }

    @Test(expected=ZserioError.class)
    public void readWrongValue8Constraint() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        final boolean selector = true;
        final short value8 = VALUE8_WRONG_CONSTRAINT;
        writeChoiceConstraintsToFile(file, selector, value8, 0);
        final BitStreamReader stream = new FileBitStreamReader(file);
        new ChoiceConstraints(stream, selector);
        stream.close();
    }

    @Test(expected=ZserioError.class)
    public void readWrongValue16Constraint() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        final boolean selector = false;
        final int value16 = VALUE16_WRONG_CONSTRAINT;
        writeChoiceConstraintsToFile(file, selector, (short)0, value16);
        final BitStreamReader stream = new FileBitStreamReader(file);
        new ChoiceConstraints(stream, selector);
        stream.close();
    }

    @Test
    public void writeCorrectChoiceConstraints() throws IOException, ZserioError
    {
        final boolean selector = false;
        final int value16 = VALUE16_CORRECT_CONSTRAINT;
        final ChoiceConstraints choiceConstraints = new ChoiceConstraints(selector);
        choiceConstraints.setValue16(value16);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        choiceConstraints.write(writer);
        writer.close();
        final ChoiceConstraints readChoiceConstraints = new ChoiceConstraints(file, selector);
        assertEquals(selector, readChoiceConstraints.getSelector());
        assertEquals(value16, readChoiceConstraints.getValue16());
        assertTrue(choiceConstraints.equals(readChoiceConstraints));
    }

    @Test(expected=ZserioError.class)
    public void writeWrongValue8Constraint() throws IOException, ZserioError
    {
        final boolean selector = true;
        final short value8 = VALUE8_WRONG_CONSTRAINT;
        final ChoiceConstraints choiceConstraints = new ChoiceConstraints(selector);
        choiceConstraints.setValue8(value8);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        choiceConstraints.write(writer);
        writer.close();
    }

    @Test(expected=ZserioError.class)
    public void writeWrongValue16Constraint() throws IOException, ZserioError
    {
        final boolean selector = false;
        final short value16 = VALUE16_WRONG_CONSTRAINT;
        final ChoiceConstraints choiceConstraints = new ChoiceConstraints(selector);
        choiceConstraints.setValue16(value16);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        choiceConstraints.write(writer);
        writer.close();
    }

    private void writeChoiceConstraintsToFile(File file, boolean selector, short value8, int value16)
            throws IOException
    {
        final FileImageOutputStream stream = new FileImageOutputStream(file);

        if (selector)
            stream.writeBits(value8, 8);
        else
            stream.writeBits(value16, 16);

        stream.close();
    }

    private static final short VALUE8_CORRECT_CONSTRAINT = 1;
    private static final short VALUE8_WRONG_CONSTRAINT = 0;

    private static final short VALUE16_CORRECT_CONSTRAINT = 256;
    private static final short VALUE16_WRONG_CONSTRAINT = 255;
}
