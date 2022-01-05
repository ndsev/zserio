package union_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import union_types.union_with_parameter.TestUnion;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class UnionWithParameterTest
{
    @Test
    public void constructorWithParameter() throws ZserioError, IOException
    {
        TestUnion testUnion = new TestUnion(true);
        assertTrue(testUnion.getCase1Allowed());

        testUnion.setCase1Field(11);
        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        testUnion.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);
        TestUnion readTestUnion = new TestUnion(reader, true);
        reader.close();
        assertEquals(testUnion.getCase1Allowed(), readTestUnion.getCase1Allowed());
        assertEquals(testUnion.getCase1Field(), readTestUnion.getCase1Field());
    }

    @Test
    public void constructorWithParameterCase1Forbidden() throws ZserioError, IOException
    {
        TestUnion testUnion = new TestUnion(false);
        assertFalse(testUnion.getCase1Allowed());
        testUnion.setCase1Field(11);
        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        assertThrows(ZserioError.class, () -> testUnion.write(writer));
        writer.close();
    }

    @Test
    public void bitStreamReaderConstructor() throws ZserioError, IOException
    {
        TestUnion testUnion = new TestUnion(true);
        testUnion.setCase3Field((byte)-1);
        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        testUnion.write(writer);
        writer.close();

        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);
        TestUnion readTestUnion = new TestUnion(reader, true);
        reader.close();
        assertEquals(testUnion.choiceTag(), readTestUnion.choiceTag());
        assertEquals(testUnion.getCase3Field(), readTestUnion.getCase3Field());
        assertEquals(-1, readTestUnion.getCase3Field());
    }

    private static final File TEST_FILE = new File("test.bin");
}
