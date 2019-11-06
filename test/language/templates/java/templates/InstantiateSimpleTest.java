package templates;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import templates.instantiate_simple.InstantiateSimple;
import templates.instantiate_simple.U32;

public class InstantiateSimpleTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateSimple instantiateSimple = new InstantiateSimple();
        instantiateSimple.setTest(new U32(13));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiateSimple.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final InstantiateSimple readInstantiateSimple = new InstantiateSimple(reader);
        reader.close();
        assertTrue(instantiateSimple.equals(readInstantiateSimple));
    }

    private static final File TEST_FILE = new File("test.bin");
}
