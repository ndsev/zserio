package choice_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import choice_types.empty_choice_with_default.EmptyChoiceWithDefault;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class EmptyChoiceWithDefaultTest
{
    @Test
    public void selectorConstructor()
    {
        final EmptyChoiceWithDefault emptyChoiceWithDefault = new EmptyChoiceWithDefault((short)1);
        assertEquals(1, emptyChoiceWithDefault.getSelector());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException
    {
        final short selector = 1;
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(new byte[0]);

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
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(new byte[0]);

        final EmptyChoiceWithDefault emptyChoiceWithDefault = new EmptyChoiceWithDefault(selector);
        emptyChoiceWithDefault.read(reader);
        assertEquals(selector, emptyChoiceWithDefault.getSelector());
        assertEquals(0, emptyChoiceWithDefault.bitSizeOf());
    }

    @Test
    public void write() throws IOException
    {
        final short selector = 1;
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        final EmptyChoiceWithDefault emptyChoiceWithDefault = new EmptyChoiceWithDefault(selector);
        emptyChoiceWithDefault.write(writer);
        byte bytes[] = writer.toByteArray();
        assertEquals(0, bytes.length);
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bytes, writer.getBitPosition());
        EmptyChoiceWithDefault readEmptyChoiceWithDefault = new EmptyChoiceWithDefault(reader, selector);
        assertEquals(emptyChoiceWithDefault, readEmptyChoiceWithDefault);
    }
};
