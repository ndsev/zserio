package structure_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import structure_types.one_string_structure.OneStringStructure;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

public class OneStringStructureTest
{
    @Test
    public void emptyConstructor()
    {
        final OneStringStructure oneStringStructure = new OneStringStructure();
        assertEquals(null, oneStringStructure.getOneString());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final BitBuffer bitBuffer = writeOneStringStructureToBitBuffer(ONE_STRING);
        final BitStreamReader stream = new ByteArrayBitStreamReader(bitBuffer);
        final OneStringStructure oneStringStructure = new OneStringStructure(stream);
        assertEquals(ONE_STRING, oneStringStructure.getOneString());
    }

    @Test
    public void fieldConstructor() throws IOException, ZserioError
    {
        final OneStringStructure oneStringStructure = new OneStringStructure(ONE_STRING);
        assertEquals(ONE_STRING, oneStringStructure.getOneString());
    }

    @Test
    public void bitSizeOf()
    {
        final OneStringStructure oneStringStructure = new OneStringStructure(ONE_STRING);
        assertEquals(ONE_STRING_STRUCTURE_BIT_SIZE, oneStringStructure.bitSizeOf());
    }

    @Test
    public void getSetOneString()
    {
        final OneStringStructure oneStringStructure = new OneStringStructure();
        oneStringStructure.setOneString(ONE_STRING);
        assertEquals(ONE_STRING, oneStringStructure.getOneString());
    }

    @Test
    public void equals()
    {
        final OneStringStructure oneStringStructure1 = new OneStringStructure();
        final OneStringStructure oneStringStructure2 = new OneStringStructure();
        assertTrue(oneStringStructure1.equals(oneStringStructure2));

        oneStringStructure1.setOneString(ONE_STRING);
        assertFalse(oneStringStructure1.equals(oneStringStructure2));

        oneStringStructure2.setOneString(ONE_STRING);
        assertTrue(oneStringStructure1.equals(oneStringStructure2));
    }

    @Test
    public void hashCodeMethod()
    {
        final OneStringStructure oneStringStructure1 = new OneStringStructure();
        final OneStringStructure oneStringStructure2 = new OneStringStructure();
        assertEquals(oneStringStructure1.hashCode(), oneStringStructure2.hashCode());

        oneStringStructure1.setOneString(ONE_STRING);
        assertTrue(oneStringStructure1.hashCode() != oneStringStructure2.hashCode());

        // use hardcoded values to check that the hash code is stable
        assertEquals(1773897624, oneStringStructure1.hashCode());
        oneStringStructure2.setOneString("");
        assertEquals(23, oneStringStructure2.hashCode());

        oneStringStructure2.setOneString(ONE_STRING);
        assertEquals(oneStringStructure1.hashCode(), oneStringStructure2.hashCode());
    }

    @Test
    public void initializeOffsets()
    {
        final OneStringStructure oneStringStructure = new OneStringStructure(ONE_STRING);
        final int bitPosition = 1;
        assertEquals(ONE_STRING_STRUCTURE_BIT_SIZE + bitPosition,
                oneStringStructure.initializeOffsets(bitPosition));
    }

    @Test
    public void writeReadFile() throws IOException, ZserioError
    {
        final OneStringStructure oneStringStructure = new OneStringStructure(ONE_STRING);

        SerializeUtil.serializeToFile(oneStringStructure, BLOB_NAME);

        final OneStringStructure readOneStringStructure = SerializeUtil.deserializeFromFile(
                OneStringStructure.class, BLOB_NAME);
        assertEquals(ONE_STRING, readOneStringStructure.getOneString());
        assertTrue(oneStringStructure.equals(readOneStringStructure));
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final OneStringStructure oneStringStructure = new OneStringStructure(ONE_STRING);
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        oneStringStructure.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final OneStringStructure readOneStringStructure = new OneStringStructure(reader);
        assertEquals(ONE_STRING, readOneStringStructure.getOneString());
        assertTrue(oneStringStructure.equals(readOneStringStructure));
    }

    private BitBuffer writeOneStringStructureToBitBuffer(String oneString) throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeString(oneString);
            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private static final String BLOB_NAME = "one_string_structure.blob";
    private static final String ONE_STRING = "This is a string!";
    private static final int    ONE_STRING_STRUCTURE_BIT_SIZE = (1 + ONE_STRING.length()) * 8;
}
