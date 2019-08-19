package parameterized_types.compound_and_field_with_same_param;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;

import org.junit.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.FileBitStreamReader;

// this test is mainly for C++, so just check that it compiles ok
public class CompoundAndFieldWithSameParamTest
{
    @Test
    public void bitStreamReaderConstructor() throws IOException
    {
        final File file = new File("test.bin");
        writeToFile(file);

        BitStreamReader stream = new FileBitStreamReader(file);
        final Compound compound = new Compound(stream, PARAM);
        assertEquals(FIELD1, compound.getField1().getValue());
        assertEquals(FIELD2, compound.getField2().getValue());

        stream = new FileBitStreamReader(file);
        final SameParamTest sameParamTest = new SameParamTest(stream);
        assertEquals(PARAM, sameParamTest.getCompound().getParam());
        assertEquals(FIELD1, sameParamTest.getCompound().getField1().getValue());
        assertEquals(FIELD2, sameParamTest.getCompound().getField2().getValue());
    }

    private void writeToFile(File file) throws IOException
    {
        final FileImageOutputStream stream = new FileImageOutputStream(file);
        stream.writeBits(FIELD1, 32);
        stream.writeBits(FIELD2, 32);
        stream.close();
    }

    private static final int PARAM = 10;
    private static final long FIELD1 = 1;
    private static final long FIELD2 = 9;
}
