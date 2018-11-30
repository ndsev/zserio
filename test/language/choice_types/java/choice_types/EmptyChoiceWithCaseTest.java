package choice_types;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import choice_types.empty_choice_with_case.EmptyChoiceWithCase;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class EmptyChoiceWithCaseTest
{
    @Before
    public void setUp() throws IOException
    {
        if (!testFile.exists())
            assertTrue(testFile.createNewFile());
    }

    @Test
    public void containerConstructor()
    {
        final EmptyChoiceWithCase emptyChoiceWithCase = new EmptyChoiceWithCase((short)1);
        assertEquals(1, emptyChoiceWithCase.getSelector());
    }

    @Test
    public void fileConstructor() throws IOException, ZserioError
    {
        final short selector = 1;
        final EmptyChoiceWithCase emptyChoiceWithCase = new EmptyChoiceWithCase(testFile, selector);
        assertEquals(selector, emptyChoiceWithCase.getSelector());
        assertEquals(0, emptyChoiceWithCase.bitSizeOf());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException
    {
        final short selector = 1;
        final BitStreamReader reader = new ByteArrayBitStreamReader(new byte[0]);

        final EmptyChoiceWithCase emptyChoiceWithCase = new EmptyChoiceWithCase(reader, selector);
        assertEquals(selector, emptyChoiceWithCase.getSelector());
        assertEquals(0, emptyChoiceWithCase.bitSizeOf());
    }

    @Test
    public void getSelector()
    {
        final short selector = 1;
        final EmptyChoiceWithCase emptyChoiceWithCase = new EmptyChoiceWithCase(selector);
        assertEquals(selector, emptyChoiceWithCase.getSelector());
    }

    @Test
    public void bitSizeOf()
    {
        final EmptyChoiceWithCase emptyChoiceWithCase = new EmptyChoiceWithCase((short)1);
        assertEquals(0, emptyChoiceWithCase.bitSizeOf(1));
    }

    @Test
    public void initializeOffsets()
    {
        final int bitPosition = 1;

        final EmptyChoiceWithCase emptyChoiceWithCase = new EmptyChoiceWithCase((short)1);
        assertEquals(bitPosition, emptyChoiceWithCase.initializeOffsets(bitPosition));
    }

    @Test
    public void equals()
    {
        final EmptyChoiceWithCase emptyChoiceWithCase1 = new EmptyChoiceWithCase((short)1);
        final EmptyChoiceWithCase emptyChoiceWithCase2 = new EmptyChoiceWithCase((short)1);
        final EmptyChoiceWithCase emptyChoiceWithCase3 = new EmptyChoiceWithCase((short)0);
        assertTrue(emptyChoiceWithCase1.equals(emptyChoiceWithCase2));
        assertFalse(emptyChoiceWithCase1.equals(emptyChoiceWithCase3));
    }

    @Test
    public void hashCodeMethod()
    {
        final EmptyChoiceWithCase emptyChoiceWithCase1 = new EmptyChoiceWithCase((short)1);
        final EmptyChoiceWithCase emptyChoiceWithCase2 = new EmptyChoiceWithCase((short)1);
        final EmptyChoiceWithCase emptyChoiceWithCase3 = new EmptyChoiceWithCase((short)0);
        assertEquals(emptyChoiceWithCase1.hashCode(), emptyChoiceWithCase2.hashCode());
        assertTrue(emptyChoiceWithCase1.hashCode() != emptyChoiceWithCase3.hashCode());
    }

    @Test
    public void read() throws IOException
    {
        final short selector = 1;
        final BitStreamReader reader = new ByteArrayBitStreamReader(new byte[0]);

        final EmptyChoiceWithCase emptyChoiceWithCase = new EmptyChoiceWithCase(selector);
        emptyChoiceWithCase.read(reader);
        assertEquals(selector, emptyChoiceWithCase.getSelector());
        assertEquals(0, emptyChoiceWithCase.bitSizeOf());
    }

    @Test
    public void fileWrite() throws IOException
    {
        final short selector = 1;
        final EmptyChoiceWithCase emptyChoiceWithCase = new EmptyChoiceWithCase(selector);
        emptyChoiceWithCase.write(testFile);
        EmptyChoiceWithCase readEmptyChoiceWithCase = new EmptyChoiceWithCase(testFile, selector);
        assertEquals(emptyChoiceWithCase, readEmptyChoiceWithCase);
    }

    @Test
    public void write() throws IOException
    {
        final short selector = 1;
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        final EmptyChoiceWithCase emptyChoiceWithCase = new EmptyChoiceWithCase(selector);
        emptyChoiceWithCase.write(writer);
        byte bytes[] = writer.toByteArray();
        assertEquals(0, bytes.length);
        BitStreamReader reader = new ByteArrayBitStreamReader(bytes);
        EmptyChoiceWithCase readEmptyChoiceWithCase = new EmptyChoiceWithCase(reader, selector);
        assertEquals(emptyChoiceWithCase, readEmptyChoiceWithCase);
    }

    private final File testFile = new File("test.bin");
};
