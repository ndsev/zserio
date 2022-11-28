package structure_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import structure_types.empty_structure.EmptyStructure;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class EmptyStructureTest
{
    @Test
    public void emptyConstructor()
    {
        final EmptyStructure emptyStructure = new EmptyStructure();
        assertEquals(0, emptyStructure.bitSizeOf());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException
    {
        final BitStreamReader reader = new ByteArrayBitStreamReader(new byte[0]);

        final EmptyStructure emptyStructure = new EmptyStructure(reader);
        assertEquals(0, emptyStructure.bitSizeOf());
    }

    @Test
    public void bitSizeOf()
    {
        final int bitPosition = 1;
        final EmptyStructure emptyStructure = new EmptyStructure();
        assertEquals(0, emptyStructure.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets()
    {
        final int bitPosition = 1;
        final EmptyStructure emptyStructure = new EmptyStructure();
        assertEquals(bitPosition, emptyStructure.initializeOffsets(bitPosition));
    }

    @Test
    public void equals()
    {
        final EmptyStructure emptyStructure1 = new EmptyStructure();
        final EmptyStructure emptyStructure2 = new EmptyStructure();
        assertTrue(emptyStructure1.equals(emptyStructure2));
    }

    @Test
    public void hashCodeMethod()
    {
        final EmptyStructure emptyStructure1 = new EmptyStructure();
        final EmptyStructure emptyStructure2 = new EmptyStructure();
        assertEquals(emptyStructure1.hashCode(), emptyStructure2.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(23, emptyStructure1.hashCode());
    }

    @Test
    public void read() throws IOException
    {
        final BitStreamReader reader = new ByteArrayBitStreamReader(new byte[0], 0);

        final EmptyStructure emptyStructure = new EmptyStructure();
        emptyStructure.read(reader);
        assertEquals(0, emptyStructure.bitSizeOf());
    }

    @Test
    public void write() throws IOException
    {
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        final EmptyStructure emptyStructure = new EmptyStructure();
        emptyStructure.write(writer);
        byte bytes[] = writer.toByteArray();
        assertEquals(0, bytes.length);
        BitStreamReader reader = new ByteArrayBitStreamReader(bytes);
        EmptyStructure readEmptyStructure = new EmptyStructure(reader);
        assertEquals(emptyStructure, readEmptyStructure);
    }
};
