package structure_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;

import structure_types.one_string_structure.OneStringStructure;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

public class OneStringStructureTest
{
    @Test
    public void emptyConstructor()
    {
        final OneStringStructure oneStringStructure = new OneStringStructure();
        assertEquals(null, oneStringStructure.getOneString());
    }

    @Test
    public void fileConstructor() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        writeOneStringStructureToFile(file, ONE_STRING);
        final OneStringStructure oneStringStructure = new OneStringStructure(file);
        assertEquals(ONE_STRING, oneStringStructure.getOneString());
    }

    @Test
    public void bitStreamReaderConstructor() throws IOException, ZserioError
    {
        final File file = new File("test.bin");
        writeOneStringStructureToFile(file, ONE_STRING);
        final BitStreamReader stream = new FileBitStreamReader(file);
        final OneStringStructure oneStringStructure = new OneStringStructure(stream);
        stream.close();
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

        oneStringStructure2.setOneString(ONE_STRING);
        assertEquals(oneStringStructure1.hashCode(), oneStringStructure2.hashCode());
    }

    @Test
    public void initializeOffsets()
    {
        final OneStringStructure oneStringStructure = new OneStringStructure(ONE_STRING);
        final int bitPosition = 1;
        assertEquals(ONE_STRING_STRUCTURE_BIT_SIZE + bitPosition, oneStringStructure.initializeOffsets(bitPosition));
    }

    @Test
    public void fileWrite() throws IOException, ZserioError
    {
        final OneStringStructure oneStringStructure = new OneStringStructure(ONE_STRING);
        final File file = new File(BLOB_NAME);
        oneStringStructure.write(file);
        final OneStringStructure readOneStringStructure = new OneStringStructure(file);
        assertEquals(ONE_STRING, readOneStringStructure.getOneString());
        assertTrue(oneStringStructure.equals(readOneStringStructure));
    }

    @Test
    public void bitStreamWriterWrite() throws IOException, ZserioError
    {
        final OneStringStructure oneStringStructure = new OneStringStructure(ONE_STRING);
        final File file = new File("test.bin");
        final BitStreamWriter writer = new FileBitStreamWriter(file);
        oneStringStructure.write(writer);
        writer.close();
        final OneStringStructure readOneStringStructure = new OneStringStructure(file);
        assertEquals(ONE_STRING, readOneStringStructure.getOneString());
        assertTrue(oneStringStructure.equals(readOneStringStructure));
    }

    private void writeOneStringStructureToFile(File file, String oneString) throws IOException
    {
        final FileImageOutputStream stream = new FileImageOutputStream(file);

        stream.writeBits(oneString.length(), 8);
        stream.writeBytes(oneString);

        stream.close();
    }

    private static final String BLOB_NAME = "one_string_structure.blob";
    private static final String ONE_STRING = "This is a string!";
    private static final int    ONE_STRING_STRUCTURE_BIT_SIZE = (1 + ONE_STRING.length()) * 8;
}
