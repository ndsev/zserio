package constraints;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import constraints.structure_constraints.BasicColor;
import constraints.structure_constraints.StructureConstraints;
import constraints.structure_constraints.ExtendedColor;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class StructureConstraintsTest
{
    @Test
    public void readCorrectConstraints() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        writeStructureConstraintsToFile(file, BasicColor.BLACK, BasicColor.WHITE, ExtendedColor.PURPLE);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final StructureConstraints structureConstraints = new StructureConstraints(stream);
        stream.close();
        assertEquals(BasicColor.BLACK, structureConstraints.getBlackColor());
        assertEquals(BasicColor.WHITE, structureConstraints.getWhiteColor());
        assertEquals(ExtendedColor.PURPLE, structureConstraints.getPurpleColor());
    }

    @Test(expected=ZserioError.class)
    public void readWrongBlackConstraint() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        writeStructureConstraintsToFile(file, BasicColor.RED, BasicColor.WHITE, ExtendedColor.PURPLE);
        final BitStreamReader stream = new FileBitStreamReader(file);
        new StructureConstraints(stream);
        stream.close();
    }

    @Test(expected=ZserioError.class)
    public void readWrongWhiteConstraint() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        writeStructureConstraintsToFile(file, BasicColor.BLACK, BasicColor.RED, ExtendedColor.PURPLE);
        final BitStreamReader stream = new FileBitStreamReader(file);
        new StructureConstraints(stream);
        stream.close();
    }

    @Test(expected=ZserioError.class)
    public void readWrongPurpleConstraint() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        writeStructureConstraintsToFile(file, BasicColor.BLACK, BasicColor.WHITE, ExtendedColor.LIME);
        final BitStreamReader stream = new FileBitStreamReader(file);
        new StructureConstraints(stream);
        stream.close();
    }

    @Test
    public void writeCorrectStructureConstraints() throws IOException, ZserioError
    {
        final StructureConstraints structureConstraints =
                new StructureConstraints(BasicColor.BLACK, BasicColor.WHITE, true, ExtendedColor.PURPLE);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        structureConstraints.write(writer);
        writer.close();
        final StructureConstraints readStructureConstraints = new StructureConstraints(file);
        assertEquals(BasicColor.BLACK, readStructureConstraints.getBlackColor());
        assertEquals(BasicColor.WHITE, readStructureConstraints.getWhiteColor());
        assertEquals(ExtendedColor.PURPLE, readStructureConstraints.getPurpleColor());
        assertTrue(structureConstraints.equals(readStructureConstraints));
    }

    @Test(expected=ZserioError.class)
    public void writeWrongBlackConstraint() throws IOException, ZserioError
    {
        final StructureConstraints structureConstraints =
                new StructureConstraints(BasicColor.RED, BasicColor.WHITE, true, ExtendedColor.PURPLE);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        structureConstraints.write(writer);
        writer.close();
    }

    @Test(expected=ZserioError.class)
    public void writeWrongPurpleConstraint() throws IOException, ZserioError
    {
        final StructureConstraints structureConstraints =
                new StructureConstraints(BasicColor.BLACK, BasicColor.WHITE, true, ExtendedColor.LIME);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        structureConstraints.write(writer);
        writer.close();
    }

    @Test(expected=ZserioError.class)
    public void writeWrongWhiteConstraint() throws IOException, ZserioError
    {
        final StructureConstraints structureConstraints =
                new StructureConstraints(BasicColor.BLACK, BasicColor.RED, true, ExtendedColor.PURPLE);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        structureConstraints.write(writer);
        writer.close();
    }

    private void writeStructureConstraintsToFile(File file, BasicColor blackColor, BasicColor whiteColor,
            ExtendedColor purpleColor) throws IOException
    {
        final BitStreamWriter stream = new FileBitStreamWriter(file);

        stream.writeBits(blackColor.getValue(), 8);
        stream.writeBool(true);
        stream.writeBits(whiteColor.getValue(), 8);
        stream.writeBool(true);
        stream.writeBits(purpleColor.getValue(), 16);

        stream.close();
    }
}
