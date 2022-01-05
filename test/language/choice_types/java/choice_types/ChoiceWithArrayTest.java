package choice_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import choice_types.choice_with_array.TestChoice;
import choice_types.choice_with_array.Data8;


public class ChoiceWithArrayTest
{
    @Test
    public void array8()
    {
        final TestChoice testChoice = new TestChoice((byte)8);
        testChoice.setArray8(new Data8[4]);

        assertEquals(4, testChoice.getArray8().length);
    }

    @Test
    public void array16()
    {
        final TestChoice testChoice = new TestChoice((byte)16);
        testChoice.setArray16(new short[4]);

        assertEquals(4, testChoice.getArray16().length);
    }

    @Test
    public void writeReadFileArray8() throws IOException
    {
        final TestChoice testChoice = new TestChoice((byte)8);
        testChoice.setArray8(
                new Data8[]{new Data8((byte)1), new Data8((byte)2), new Data8((byte)3), new Data8((byte)4)}
        );
        final File file = new File(BLOB_NAME_BASE + "array8.blob");
        testChoice.write(file);

        final TestChoice readTestChoice = new TestChoice(file, (byte)8);
        assertEquals(testChoice, readTestChoice);
    }

    @Test
    public void writeReadFileArray16() throws IOException
    {
        final TestChoice testChoice = new TestChoice((byte)16);
        testChoice.setArray16(new short[]{10, 20, 30, 40, 50});
        final File file = new File(BLOB_NAME_BASE + "array16.blob");
        testChoice.write(file);

        final TestChoice readTestChoice = new TestChoice(file, (byte)16);
        assertEquals(testChoice, readTestChoice);
    }

    private static final String BLOB_NAME_BASE = "choice_with_array_";
}
