package union_types;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import union_types.empty_union_with_parameter.EmptyUnionWithParameter;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class EmptyUnionWithParameterTest
{
    @Before
    public void setUp() throws IOException
    {
        if (!testFile.exists())
            assertTrue(testFile.createNewFile());
    }

    @Test
    public void emptyConstructor()
    {
        final EmptyUnionWithParameter emptyUnionWithParameter =
                new EmptyUnionWithParameter((short)1);
        assertEquals(1, emptyUnionWithParameter.getParam());
    }

    @Test
    public void fileConstructor() throws IOException, ZserioError
    {
        final short param = 1;
        final EmptyUnionWithParameter emptyUnionWithParameter =
                new EmptyUnionWithParameter(testFile, param);
        assertEquals(param, emptyUnionWithParameter.getParam());
        assertEquals(0, emptyUnionWithParameter.bitSizeOf());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException
    {
        final short param = 1;
        final BitStreamReader reader = new ByteArrayBitStreamReader(new byte[0]);

        final EmptyUnionWithParameter emptyUnionWithParameter =
                new EmptyUnionWithParameter(reader, param);
        assertEquals(param, emptyUnionWithParameter.getParam());
        assertEquals(0, emptyUnionWithParameter.bitSizeOf());
    }

    @Test
    public void getParam()
    {
        final short param = 1;
        final EmptyUnionWithParameter emptyUnionWithParameter = new EmptyUnionWithParameter(param);
        assertEquals(param, emptyUnionWithParameter.getParam());
    }

    @Test
    public void bitSizeOf()
    {
        final EmptyUnionWithParameter emptyUnionWithParameter =
                new EmptyUnionWithParameter((short)1);
        assertEquals(0, emptyUnionWithParameter.bitSizeOf(1));
    }

    @Test
    public void initializeOffsets()
    {
        final int bitPosition = 1;

        final EmptyUnionWithParameter emptyUnionWithParameter =
                new EmptyUnionWithParameter((short)1);
        assertEquals(bitPosition, emptyUnionWithParameter.initializeOffsets(bitPosition));
    }

    @Test
    public void equals()
    {
        final EmptyUnionWithParameter emptyUnionWithParameter1 =
                new EmptyUnionWithParameter((short)1);
        final EmptyUnionWithParameter emptyUnionWithParameter2 =
                new EmptyUnionWithParameter((short)1);
        final EmptyUnionWithParameter emptyUnionWithParameter3 =
                new EmptyUnionWithParameter((short)0);
        assertTrue(emptyUnionWithParameter1.equals(emptyUnionWithParameter2));
        assertFalse(emptyUnionWithParameter1.equals(emptyUnionWithParameter3));
    }

    @Test
    public void hashCodeMethod()
    {
        final EmptyUnionWithParameter emptyUnionWithParameter1 =
                new EmptyUnionWithParameter((short)1);
        final EmptyUnionWithParameter emptyUnionWithParameter2 =
                new EmptyUnionWithParameter((short)1);
        final EmptyUnionWithParameter emptyUnionWithParameter3 =
                new EmptyUnionWithParameter((short)0);
        assertEquals(emptyUnionWithParameter1.hashCode(), emptyUnionWithParameter2.hashCode());
        assertTrue(emptyUnionWithParameter1.hashCode() != emptyUnionWithParameter3.hashCode());
    }

    @Test
    public void read() throws IOException
    {
        final short param = 1;
        final BitStreamReader reader = new ByteArrayBitStreamReader(new byte[0]);

        final EmptyUnionWithParameter emptyUnionWithParameter =
                new EmptyUnionWithParameter(param);
        emptyUnionWithParameter.read(reader);
        assertEquals(param, emptyUnionWithParameter.getParam());
        assertEquals(0, emptyUnionWithParameter.bitSizeOf());
    }

    @Test
    public void fileWrite() throws IOException
    {
        final short param = 1;
        final EmptyUnionWithParameter emptyUnionWithParameter = new EmptyUnionWithParameter(param);
        emptyUnionWithParameter.write(testFile);
        EmptyUnionWithParameter readEmptyUnionWithParameter =
                new EmptyUnionWithParameter(testFile, param);
        assertEquals(emptyUnionWithParameter, readEmptyUnionWithParameter);
    }

    @Test
    public void write() throws IOException
    {
        final short param = 1;
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        final EmptyUnionWithParameter emptyUnionWithParameter =
                new EmptyUnionWithParameter(param);
        emptyUnionWithParameter.write(writer);
        byte bytes[] = writer.toByteArray();
        assertEquals(0, bytes.length);
        BitStreamReader reader = new ByteArrayBitStreamReader(bytes);
        EmptyUnionWithParameter readEmptyUnionWithParameter =
                new EmptyUnionWithParameter(reader, param);
        assertEquals(emptyUnionWithParameter, readEmptyUnionWithParameter);
    }

    private final File testFile = new File("test.bin");
};
