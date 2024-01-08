package union_types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import union_types.empty_union.EmptyUnion;

public class EmptyUnionTest
{
    @Test
    public void emptyConstructor()
    {
        final EmptyUnion emptyUnion = new EmptyUnion();
        assertEquals(0, emptyUnion.bitSizeOf());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException
    {
        final BitStreamReader reader = new ByteArrayBitStreamReader(new byte[0]);

        final EmptyUnion emptyUnion = new EmptyUnion(reader);
        assertEquals(0, emptyUnion.bitSizeOf());
    }

    @Test
    public void bitSizeOf()
    {
        final int bitPosition = 1;
        final EmptyUnion emptyUnion = new EmptyUnion();
        assertEquals(0, emptyUnion.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets()
    {
        final int bitPosition = 1;
        final EmptyUnion emptyUnion = new EmptyUnion();
        assertEquals(bitPosition, emptyUnion.initializeOffsets(bitPosition));
    }

    @Test
    public void equals()
    {
        final EmptyUnion emptyUnion1 = new EmptyUnion();
        final EmptyUnion emptyUnion2 = new EmptyUnion();
        assertTrue(emptyUnion1.equals(emptyUnion2));
    }

    @Test
    public void hashCodeMethod()
    {
        final EmptyUnion emptyUnion1 = new EmptyUnion();
        final EmptyUnion emptyUnion2 = new EmptyUnion();
        assertEquals(emptyUnion1.hashCode(), emptyUnion2.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(850, emptyUnion1.hashCode());
    }

    @Test
    public void read() throws IOException
    {
        final BitStreamReader reader = new ByteArrayBitStreamReader(new byte[0], 0);

        final EmptyUnion emptyUnion = new EmptyUnion();
        emptyUnion.read(reader);
        assertEquals(0, emptyUnion.bitSizeOf());
    }

    @Test
    public void write() throws IOException
    {
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        final EmptyUnion emptyUnion = new EmptyUnion();
        emptyUnion.write(writer);
        byte bytes[] = writer.toByteArray();
        assertEquals(0, bytes.length);
        BitStreamReader reader = new ByteArrayBitStreamReader(bytes);
        EmptyUnion readEmptyUnion = new EmptyUnion(reader);
        assertEquals(emptyUnion, readEmptyUnion);
    }
};
