package structure_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import structure_types.empty_structure_with_parameter.EmptyStructureWithParameter;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class EmptyStructureWithParameterTest
{
    @BeforeEach
    public void setUp() throws IOException
    {
        if (!testFile.exists())
            assertTrue(testFile.createNewFile());
    }

    @Test
    public void paramConstructor()
    {
        final EmptyStructureWithParameter emptyStructureWithParameter =
                new EmptyStructureWithParameter((short)1);
        assertEquals(1, emptyStructureWithParameter.getParam());
    }

    @Test
    public void fileConstructor() throws IOException, ZserioError
    {
        final short param = 1;
        final EmptyStructureWithParameter emptyStructureWithParameter =
                new EmptyStructureWithParameter(testFile, param);
        assertEquals(param, emptyStructureWithParameter.getParam());
        assertEquals(0, emptyStructureWithParameter.bitSizeOf());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException
    {
        final short param = 1;
        final BitStreamReader reader = new ByteArrayBitStreamReader(new byte[0]);

        final EmptyStructureWithParameter emptyStructureWithParameter =
                new EmptyStructureWithParameter(reader, param);
        assertEquals(param, emptyStructureWithParameter.getParam());
        assertEquals(0, emptyStructureWithParameter.bitSizeOf());
    }

    @Test
    public void getParam()
    {
        final short param = 1;
        final EmptyStructureWithParameter emptyStructureWithParameter = new EmptyStructureWithParameter(param);
        assertEquals(param, emptyStructureWithParameter.getParam());
    }

    @Test
    public void bitSizeOf()
    {
        final EmptyStructureWithParameter emptyStructureWithParameter =
                new EmptyStructureWithParameter((short)1);
        assertEquals(0, emptyStructureWithParameter.bitSizeOf(1));
    }

    @Test
    public void initializeOffsets()
    {
        final int bitPosition = 1;

        final EmptyStructureWithParameter emptyStructureWithParameter =
                new EmptyStructureWithParameter((short)1);
        assertEquals(bitPosition, emptyStructureWithParameter.initializeOffsets(bitPosition));
    }

    @Test
    public void equals()
    {
        final EmptyStructureWithParameter emptyStructureWithParameter1 =
                new EmptyStructureWithParameter((short)1);
        final EmptyStructureWithParameter emptyStructureWithParameter2 =
                new EmptyStructureWithParameter((short)1);
        final EmptyStructureWithParameter emptyStructureWithParameter3 =
                new EmptyStructureWithParameter((short)0);
        assertTrue(emptyStructureWithParameter1.equals(emptyStructureWithParameter2));
        assertFalse(emptyStructureWithParameter1.equals(emptyStructureWithParameter3));
    }

    @Test
    public void hashCodeMethod()
    {
        final EmptyStructureWithParameter emptyStructureWithParameter1 =
                new EmptyStructureWithParameter((short)1);
        final EmptyStructureWithParameter emptyStructureWithParameter2 =
                new EmptyStructureWithParameter((short)1);
        final EmptyStructureWithParameter emptyStructureWithParameter3 =
                new EmptyStructureWithParameter((short)0);
        assertEquals(emptyStructureWithParameter1.hashCode(), emptyStructureWithParameter2.hashCode());
        assertTrue(emptyStructureWithParameter1.hashCode() != emptyStructureWithParameter3.hashCode());
    }

    @Test
    public void read() throws IOException
    {
        final short param = 1;
        final BitStreamReader reader = new ByteArrayBitStreamReader(new byte[0]);

        final EmptyStructureWithParameter emptyStructureWithParameter =
                new EmptyStructureWithParameter(param);
        emptyStructureWithParameter.read(reader);
        assertEquals(param, emptyStructureWithParameter.getParam());
        assertEquals(0, emptyStructureWithParameter.bitSizeOf());
    }

    @Test
    public void fileWrite() throws IOException
    {
        final short param = 1;
        final EmptyStructureWithParameter emptyStructureWithParameter = new EmptyStructureWithParameter(param);
        emptyStructureWithParameter.write(testFile);
        EmptyStructureWithParameter readEmptyStructureWithParameter =
                new EmptyStructureWithParameter(testFile, param);
        assertEquals(emptyStructureWithParameter, readEmptyStructureWithParameter);
    }

    @Test
    public void write() throws IOException
    {
        final short param = 1;
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        final EmptyStructureWithParameter emptyStructureWithParameter =
                new EmptyStructureWithParameter(param);
        emptyStructureWithParameter.write(writer);
        byte bytes[] = writer.toByteArray();
        assertEquals(0, bytes.length);
        BitStreamReader reader = new ByteArrayBitStreamReader(bytes);
        EmptyStructureWithParameter readEmptyStructureWithParameter =
                new EmptyStructureWithParameter(reader, param);
        assertEquals(emptyStructureWithParameter, readEmptyStructureWithParameter);
    }

    private final File testFile = new File("test.bin");
};
