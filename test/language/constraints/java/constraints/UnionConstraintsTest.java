package constraints;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import constraints.union_constraints.UnionConstraints;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class UnionConstraintsTest
{
    @Test
    public void readCorrectConstraints() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        final short value8 = VALUE8_CORRECT_CONSTRAINT;
        writeUnionConstraintsToFile(file, value8);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final UnionConstraints unionConstraints = new UnionConstraints(stream);
        stream.close();
        assertEquals(UnionConstraints.CHOICE_value8, unionConstraints.choiceTag());
        assertEquals(value8, unionConstraints.getValue8());
    }

    @Test(expected=ZserioError.class)
    public void readWrongValue8Constraint() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        final short value8 = VALUE8_WRONG_CONSTRAINT;
        writeUnionConstraintsToFile(file,value8);
        final BitStreamReader stream = new FileBitStreamReader(file);
        new UnionConstraints(stream);
        stream.close();
    }

    @Test(expected=ZserioError.class)
    public void readWrongValue16Constraint() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        final int value16 = VALUE16_WRONG_CONSTRAINT;
        writeUnionConstraintsToFile(file, value16);
        final BitStreamReader stream = new FileBitStreamReader(file);
        new UnionConstraints(stream);
        stream.close();
    }

    @Test
    public void writeCorrectUnionConstraints() throws IOException, ZserioError
    {
        final int value16 = VALUE16_CORRECT_CONSTRAINT;
        final UnionConstraints unionConstraints = new UnionConstraints();
        unionConstraints.setValue16(value16);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        unionConstraints.write(writer);
        writer.close();
        final UnionConstraints readUnionConstraints = new UnionConstraints(file);
        assertEquals(UnionConstraints.CHOICE_value16, readUnionConstraints.choiceTag());
        assertEquals(value16, readUnionConstraints.getValue16());
        assertTrue(unionConstraints.equals(readUnionConstraints));
    }

    @Test(expected=ZserioError.class)
    public void writeWrongValue8Constraint() throws IOException, ZserioError
    {
        final short value8 = VALUE8_WRONG_CONSTRAINT;
        final UnionConstraints unionConstraints = new UnionConstraints();
        unionConstraints.setValue8(value8);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        unionConstraints.write(writer);
        writer.close();
    }

    @Test(expected=ZserioError.class)
    public void writeWrongValue16Constraint() throws IOException, ZserioError
    {
        final short value16 = VALUE16_WRONG_CONSTRAINT;
        final UnionConstraints unionConstraints = new UnionConstraints();
        unionConstraints.setValue16(value16);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        unionConstraints.write(writer);
        writer.close();
    }

    private void writeUnionConstraintsToFile(File file, short value8) throws IOException
    {
        final FileBitStreamWriter stream = new FileBitStreamWriter(file);

        stream.writeVarSize(UnionConstraints.CHOICE_value8);
        stream.writeBits(value8, 8);

        stream.close();
    }

    private void writeUnionConstraintsToFile(File file, int value16) throws IOException
    {
        final FileBitStreamWriter stream = new FileBitStreamWriter(file);

        stream.writeVarSize(UnionConstraints.CHOICE_value16);
        stream.writeBits(value16, 16);

        stream.close();
    }

    private static final short VALUE8_CORRECT_CONSTRAINT = 1;
    private static final short VALUE8_WRONG_CONSTRAINT = 0;

    private static final short VALUE16_CORRECT_CONSTRAINT = 256;
    private static final short VALUE16_WRONG_CONSTRAINT = 255;
}
