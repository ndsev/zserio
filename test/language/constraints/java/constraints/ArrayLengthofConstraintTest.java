package constraints;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import constraints.array_lengthof_constraint.ArrayLengthofConstraint;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

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
        assertEquals(CORRECT_LENGTH, arrayLengthofConstraint.getArray().length);
    }

    @Test
    public void readWrongLengthLess() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        writeArrayLengthofConstraintToFile(file, WRONG_LENGTH_LESS);
        final BitStreamReader stream = new FileBitStreamReader(file);
        assertThrows(ZserioError.class, () -> new ArrayLengthofConstraint(stream));
        stream.close();
    }

    @Test
    public void readWrongLengthGreater() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        writeArrayLengthofConstraintToFile(file, WRONG_LENGTH_GREATER);
        final BitStreamReader stream = new FileBitStreamReader(file);
        assertThrows(ZserioError.class, () -> new ArrayLengthofConstraint(stream));
        stream.close();
    }

    @Test
    public void writeCorrectLength() throws IOException, ZserioError
    {
        final ArrayLengthofConstraint arrayLengthofConstraint = new ArrayLengthofConstraint();
        final long[] array = new long[CORRECT_LENGTH];
        for (int i = 0; i < CORRECT_LENGTH; ++i)
            array[i] = i;
        arrayLengthofConstraint.setArray(array);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        arrayLengthofConstraint.write(writer);
        writer.close();
        final ArrayLengthofConstraint readArrayLengthofConstraint = new ArrayLengthofConstraint(file);
        assertEquals(CORRECT_LENGTH, readArrayLengthofConstraint.getArray().length);
        assertTrue(arrayLengthofConstraint.equals(readArrayLengthofConstraint));
    }

    @Test
    public void writeWrongLengthLess() throws IOException, ZserioError
    {
        final ArrayLengthofConstraint arrayLengthofConstraint = new ArrayLengthofConstraint();
        final long[] array = new long[WRONG_LENGTH_LESS];
        for (int i = 0; i < WRONG_LENGTH_LESS; ++i)
            array[i] = i;
        arrayLengthofConstraint.setArray(array);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        assertThrows(ZserioError.class, () -> arrayLengthofConstraint.write(writer));
        writer.close();
    }

    @Test
    public void writeWrongLengthGreater() throws IOException, ZserioError
    {
        final ArrayLengthofConstraint arrayLengthofConstraint = new ArrayLengthofConstraint();
        final long[] array = new long[WRONG_LENGTH_GREATER];
        for (int i = 0; i < WRONG_LENGTH_GREATER; ++i)
            array[i] = i;
        arrayLengthofConstraint.setArray(array);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        assertThrows(ZserioError.class, () -> arrayLengthofConstraint.write(writer));
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
