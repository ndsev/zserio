package constraints;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import org.junit.Test;

import constraints.array_lengthof_constraint.ArrayLengthofConstraint;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;
import zserio.runtime.array.UnsignedIntArray;

public class ArrayLengthofConstraintTest
{
    @Test
    public void readCorrectLength() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        writeArrayLengthofConstraintToFile(file, CORRECT_LENGTH);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final ArrayLengthofConstraint arrayLengthofConstraint = new ArrayLengthofConstraint(stream);
        stream.close();
        assertEquals(CORRECT_LENGTH, arrayLengthofConstraint.getArray().length());
    }

    @Test(expected=ZserioError.class)
    public void readWrongLengthLess() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        writeArrayLengthofConstraintToFile(file, WRONG_LENGTH_LESS);
        final BitStreamReader stream = new FileBitStreamReader(file);
        new ArrayLengthofConstraint(stream);
        stream.close();
    }

    @Test(expected=ZserioError.class)
    public void readWrongLengthGreater() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        writeArrayLengthofConstraintToFile(file, WRONG_LENGTH_GREATER);
        final BitStreamReader stream = new FileBitStreamReader(file);
        new ArrayLengthofConstraint(stream);
        stream.close();
    }

    @Test
    public void writeCorrectLength() throws IOException, ZserioError
    {
        final ArrayLengthofConstraint arrayLengthofConstraint = new ArrayLengthofConstraint();
        UnsignedIntArray array = new UnsignedIntArray(CORRECT_LENGTH);
        for (int i = 0; i < CORRECT_LENGTH; ++i)
            array.setElementAt(i, i);
        arrayLengthofConstraint.setArray(array);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        arrayLengthofConstraint.write(writer);
        writer.close();
        final ArrayLengthofConstraint readArrayLengthofConstraint = new ArrayLengthofConstraint(file);
        assertEquals(CORRECT_LENGTH, readArrayLengthofConstraint.getArray().length());
        assertTrue(arrayLengthofConstraint.equals(readArrayLengthofConstraint));
    }

    @Test(expected=ZserioError.class)
    public void writeWrongLengthLess() throws IOException, ZserioError
    {
        final ArrayLengthofConstraint arrayLengthofConstraint = new ArrayLengthofConstraint();
        UnsignedIntArray array = new UnsignedIntArray(WRONG_LENGTH_LESS);
        for (int i = 0; i < WRONG_LENGTH_LESS; ++i)
            array.setElementAt(i, i);
        arrayLengthofConstraint.setArray(array);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        arrayLengthofConstraint.write(writer);
        writer.close();
    }

    @Test(expected=ZserioError.class)
    public void writeWrongLengthGreater() throws IOException, ZserioError
    {
        final ArrayLengthofConstraint arrayLengthofConstraint = new ArrayLengthofConstraint();
        UnsignedIntArray array = new UnsignedIntArray(WRONG_LENGTH_GREATER);
        for (int i = 0; i < WRONG_LENGTH_GREATER; ++i)
            array.setElementAt(i, i);
        arrayLengthofConstraint.setArray(array);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        arrayLengthofConstraint.write(writer);
        writer.close();
    }

    private void writeArrayLengthofConstraintToFile(File file, int length)
            throws IOException
    {
        final FileImageOutputStream stream = new FileImageOutputStream(file);

        stream.writeBits(length, 8); // all lengths in this test fits in a single byte

        for (int i = 0; i < length; ++i)
            stream.writeBits(i, 32);

        stream.close();
    }

    private static final int CORRECT_LENGTH = 6;
    private static final int WRONG_LENGTH_LESS = 3;
    private static final int WRONG_LENGTH_GREATER = 12;
}
