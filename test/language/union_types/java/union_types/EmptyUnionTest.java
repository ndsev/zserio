package union_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import union_types.empty_union.EmptyUnion;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class EmptyUnionTest
{
    @BeforeEach
    public void setUp() throws IOException
    {
        if (!testFile.exists())
            assertTrue(testFile.createNewFile());
    }

    @Test
    public void emptyConstructor()
    {
        final EmptyUnion emptyUnion = new EmptyUnion();
        assertEquals(0, emptyUnion.bitSizeOf());
    }

    @Test
    public void fileConstructor() throws IOException, ZserioError
    {
        final EmptyUnion emptyUnion = new EmptyUnion(testFile);
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
    }

    @Test
    public void read() throws IOException
    {
        final BitStreamReader reader = new ByteArrayBitStreamReader(new byte[0]);

        final EmptyUnion emptyUnion = new EmptyUnion();
        emptyUnion.read(reader);
        assertEquals(0, emptyUnion.bitSizeOf());
    }

    @Test
    public void fileWrite() throws IOException
    {
        final EmptyUnion emptyUnion = new EmptyUnion();
        emptyUnion.write(testFile);
        EmptyUnion readEmptyUnion = new EmptyUnion(testFile);
        assertEquals(emptyUnion, readEmptyUnion);
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

    private final File testFile = new File("test.bin");
};
