package union_types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import union_types.empty_union_with_parameter.EmptyUnionWithParameter;

public class EmptyUnionWithParameterTest
{
    @Test
    public void emptyConstructor()
    {
        final EmptyUnionWithParameter emptyUnionWithParameter = new EmptyUnionWithParameter((short)1);
        assertEquals(1, emptyUnionWithParameter.getParam());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException
    {
        final short param = 1;
        final BitStreamReader reader = new ByteArrayBitStreamReader(new byte[0]);

        final EmptyUnionWithParameter emptyUnionWithParameter = new EmptyUnionWithParameter(reader, param);
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
        final EmptyUnionWithParameter emptyUnionWithParameter = new EmptyUnionWithParameter((short)1);
        assertEquals(0, emptyUnionWithParameter.bitSizeOf(1));
    }

    @Test
    public void initializeOffsets()
    {
        final int bitPosition = 1;

        final EmptyUnionWithParameter emptyUnionWithParameter = new EmptyUnionWithParameter((short)1);
        assertEquals(bitPosition, emptyUnionWithParameter.initializeOffsets(bitPosition));
    }

    @Test
    public void equals()
    {
        final EmptyUnionWithParameter emptyUnionWithParameter1 = new EmptyUnionWithParameter((short)1);
        final EmptyUnionWithParameter emptyUnionWithParameter2 = new EmptyUnionWithParameter((short)1);
        final EmptyUnionWithParameter emptyUnionWithParameter3 = new EmptyUnionWithParameter((short)0);
        assertTrue(emptyUnionWithParameter1.equals(emptyUnionWithParameter2));
        assertFalse(emptyUnionWithParameter1.equals(emptyUnionWithParameter3));
    }

    @Test
    public void hashCodeMethod()
    {
        final EmptyUnionWithParameter emptyUnionWithParameter1 = new EmptyUnionWithParameter((short)1);
        final EmptyUnionWithParameter emptyUnionWithParameter2 = new EmptyUnionWithParameter((short)1);
        final EmptyUnionWithParameter emptyUnionWithParameter3 = new EmptyUnionWithParameter((short)0);
        assertEquals(emptyUnionWithParameter1.hashCode(), emptyUnionWithParameter2.hashCode());
        assertTrue(emptyUnionWithParameter1.hashCode() != emptyUnionWithParameter3.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(31523, emptyUnionWithParameter1.hashCode());
        assertEquals(31486, emptyUnionWithParameter3.hashCode());
    }

    @Test
    public void read() throws IOException
    {
        final short param = 1;
        final BitStreamReader reader = new ByteArrayBitStreamReader(new byte[0], 0);

        final EmptyUnionWithParameter emptyUnionWithParameter = new EmptyUnionWithParameter(param);
        emptyUnionWithParameter.read(reader);
        assertEquals(param, emptyUnionWithParameter.getParam());
        assertEquals(0, emptyUnionWithParameter.bitSizeOf());
    }

    @Test
    public void write() throws IOException
    {
        final short param = 1;
        ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        final EmptyUnionWithParameter emptyUnionWithParameter = new EmptyUnionWithParameter(param);
        emptyUnionWithParameter.write(writer);
        byte bytes[] = writer.toByteArray();
        assertEquals(0, bytes.length);
        BitStreamReader reader = new ByteArrayBitStreamReader(bytes);
        EmptyUnionWithParameter readEmptyUnionWithParameter = new EmptyUnionWithParameter(reader, param);
        assertEquals(emptyUnionWithParameter, readEmptyUnionWithParameter);
    }
};
