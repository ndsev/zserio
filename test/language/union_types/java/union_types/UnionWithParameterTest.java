package union_types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import union_types.union_with_parameter.TestUnion;

public class UnionWithParameterTest
{
    @Test
    public void constructorWithParameter() throws ZserioError, IOException
    {
        TestUnion testUnion = new TestUnion(true);
        assertTrue(testUnion.getCase1Allowed());

        testUnion.setCase1Field(11);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        testUnion.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        TestUnion readTestUnion = new TestUnion(reader, true);
        assertEquals(testUnion.getCase1Allowed(), readTestUnion.getCase1Allowed());
        assertEquals(testUnion.getCase1Field(), readTestUnion.getCase1Field());
    }

    @Test
    public void constructorWithParameterCase1Forbidden() throws ZserioError, IOException
    {
        TestUnion testUnion = new TestUnion(false);
        assertFalse(testUnion.getCase1Allowed());
        testUnion.setCase1Field(11);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertThrows(ZserioError.class, () -> testUnion.write(writer));
        writer.close();
    }

    @Test
    public void bitStreamReaderConstructor() throws ZserioError, IOException
    {
        TestUnion testUnion = new TestUnion(true);
        testUnion.setCase3Field((byte)-1);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        testUnion.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        TestUnion readTestUnion = new TestUnion(reader, true);
        assertEquals(testUnion.choiceTag(), readTestUnion.choiceTag());
        assertEquals(testUnion.getCase3Field(), readTestUnion.getCase3Field());
        assertEquals(-1, readTestUnion.getCase3Field());
    }
}
