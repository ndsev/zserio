package choice_types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import choice_types.empty_choice_with_case.EmptyChoiceWithCase;

public class EmptyChoiceWithCaseTest
{
    @Test
    public void selectorConstructor()
    {
        final EmptyChoiceWithCase emptyChoiceWithCase = new EmptyChoiceWithCase((short)1);
        assertEquals(1, emptyChoiceWithCase.getSelector());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException
    {
        final short selector = 1;
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(new byte[0]);

        final EmptyChoiceWithCase emptyChoiceWithCase = new EmptyChoiceWithCase(reader, selector);
        assertEquals(selector, emptyChoiceWithCase.getSelector());
        assertEquals(0, emptyChoiceWithCase.bitSizeOf());
    }

    @Test
    public void choiceTag()
    {
        EmptyChoiceWithCase emptyChoiceWithCase = new EmptyChoiceWithCase((short)0);
        assertEquals(EmptyChoiceWithCase.UNDEFINED_CHOICE, emptyChoiceWithCase.choiceTag());

        emptyChoiceWithCase = new EmptyChoiceWithCase((short)1);
        assertEquals(EmptyChoiceWithCase.UNDEFINED_CHOICE, emptyChoiceWithCase.choiceTag());
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

        // use hardcoded values to check that the hash code is stable
        assertEquals(852, emptyChoiceWithCase1.hashCode());
        assertEquals(851, emptyChoiceWithCase3.hashCode());
    }

    @Test
    public void read() throws IOException
    {
        final short selector = 1;
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(new byte[0]);

        final EmptyChoiceWithCase emptyChoiceWithCase = new EmptyChoiceWithCase(selector);
        emptyChoiceWithCase.read(reader);
        assertEquals(selector, emptyChoiceWithCase.getSelector());
        assertEquals(0, emptyChoiceWithCase.bitSizeOf());
    }

    @Test
    public void write() throws IOException
    {
        final short selector = 1;
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        final EmptyChoiceWithCase emptyChoiceWithCase = new EmptyChoiceWithCase(selector);
        emptyChoiceWithCase.write(writer);
        byte bytes[] = writer.toByteArray();
        assertEquals(0, bytes.length);
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bytes, writer.getBitPosition());
        EmptyChoiceWithCase readEmptyChoiceWithCase = new EmptyChoiceWithCase(reader, selector);
        assertEquals(emptyChoiceWithCase, readEmptyChoiceWithCase);
    }
};
