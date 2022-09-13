package choice_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import choice_types.empty_choice_with_default.EmptyChoiceWithDefault;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class EmptyChoiceWithDefaultTest
{
    @BeforeEach
    public void setUp() throws IOException
    {
        if (!testFile.exists())
            assertTrue(testFile.createNewFile());
    }

    @Test
    public void selectorConstructor()
    {
        final EmptyChoiceWithDefault emptyChoiceWithDefault = new EmptyChoiceWithDefault((short)1);
        assertEquals(1, emptyChoiceWithDefault.getSelector());
    }

    @Test
    public void fileConstructor() throws IOException, ZserioError
    {
        final short selector = 1;
        final EmptyChoiceWithDefault emptyChoiceWithDefault = new EmptyChoiceWithDefault(testFile, selector);
        assertEquals(selector, emptyChoiceWithDefault.getSelector());
        assertEquals(0, emptyChoiceWithDefault.bitSizeOf());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException
    {
        final short selector = 1;
        final BitStreamReader reader = new ByteArrayBitStreamReader(new byte[0]);

        final EmptyChoiceWithDefault emptyChoiceWithDefault = new EmptyChoiceWithDefault(reader, selector);
        assertEquals(selector, emptyChoiceWithDefault.getSelector());
        assertEquals(0, emptyChoiceWithDefault.bitSizeOf());
    }

    @Test
    public void choiceTag()
    {
        EmptyChoiceWithDefault emptyChoiceWithDefault = new EmptyChoiceWithDefault((short)0);
        assertEquals(EmptyChoiceWithDefault.UNDEFINED_CHOICE, emptyChoiceWithDefault.choiceTag());

        emptyChoiceWithDefault = new EmptyChoiceWithDefault((short)1);
        assertEquals(EmptyChoiceWithDefault.UNDEFINED_CHOICE, emptyChoiceWithDefault.choiceTag());
    }

    @Test
    public void getSelector()
    {
        final short selector = 1;
        final EmptyChoiceWithDefault emptyChoiceWithDefault = new EmptyChoiceWithDefault(selector);
        assertEquals(selector, emptyChoiceWithDefault.getSelector());
    }

    @Test
    public void bitSizeOf()
    {
        final EmptyChoiceWithDefault emptyChoiceWithDefault = new EmptyChoiceWithDefault((short)1);
        assertEquals(0, emptyChoiceWithDefault.bitSizeOf(1));
    }

    @Test
    public void initializeOffsets()
    {
        final int bitPosition = 1;

        final EmptyChoiceWithDefault emptyChoiceWithDefault = new EmptyChoiceWithDefault((short)1);
        assertEquals(bitPosition, emptyChoiceWithDefault.initializeOffsets(bitPosition));
    }

    @Test
    public void equals()
    {
        final EmptyChoiceWithDefault emptyChoiceWithDefault1 = new EmptyChoiceWithDefault((short)1);
        final EmptyChoiceWithDefault emptyChoiceWithDefault2 = new EmptyChoiceWithDefault((short)1);
        final EmptyChoiceWithDefault emptyChoiceWithDefault3 = new EmptyChoiceWithDefault((short)0);
        assertTrue(emptyChoiceWithDefault1.equals(emptyChoiceWithDefault2));
        assertFalse(emptyChoiceWithDefault1.equals(emptyChoiceWithDefault3));
    }

    @Test
    public void hashCodeMethod()
    {
        final EmptyChoiceWithDefault emptyChoiceWithDefault1 = new EmptyChoiceWithDefault((short)1);
        final EmptyChoiceWithDefault emptyChoiceWithDefault2 = new EmptyChoiceWithDefault((short)1);
        final EmptyChoiceWithDefault emptyChoiceWithDefault3 = new EmptyChoiceWithDefault((short)0);
        assertEquals(emptyChoiceWithDefault1.hashCode(), emptyChoiceWithDefault2.hashCode());
        assertTrue(emptyChoiceWithDefault1.hashCode() != emptyChoiceWithDefault3.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(852, emptyChoiceWithDefault1.hashCode());
        assertEquals(851, emptyChoiceWithDefault3.hashCode());
    }

    @Test
    public void read() throws IOException
    {
        final short selector = 1;
        final BitStreamReader reader = new ByteArrayBitStreamReader(new byte[0]);

        final EmptyChoiceWithDefault emptyChoiceWithDefault = new EmptyChoiceWithDefault(selector);
        emptyChoiceWithDefault.read(reader);
        assertEquals(selector, emptyChoiceWithDefault.getSelector());
        assertEquals(0, emptyChoiceWithDefault.bitSizeOf());
    }

    @Test
    public void fileWrite() throws IOException
    {
        final short selector = 1;
        final EmptyChoiceWithDefault emptyChoiceWithDefault = new EmptyChoiceWithDefault(selector);
        emptyChoiceWithDefault.write(testFile);
        EmptyChoiceWithDefault readEmptyChoiceWithDefault = new EmptyChoiceWithDefault(testFile, selector);
        assertEquals(emptyChoiceWithDefault, readEmptyChoiceWithDefault);
    }

    @Test
    public void write() throws IOException
    {
        final short selector = 1;
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        final EmptyChoiceWithDefault emptyChoiceWithDefault = new EmptyChoiceWithDefault(selector);
        emptyChoiceWithDefault.write(writer);
        byte bytes[] = writer.toByteArray();
        assertEquals(0, bytes.length);
        BitStreamReader reader = new ByteArrayBitStreamReader(bytes);
        EmptyChoiceWithDefault readEmptyChoiceWithDefault = new EmptyChoiceWithDefault(reader, selector);
        assertEquals(emptyChoiceWithDefault, readEmptyChoiceWithDefault);
    }

    private final File testFile = new File("test.bin");
};
