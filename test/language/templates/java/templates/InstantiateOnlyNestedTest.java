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

import templates.instantiate_only_nested.InstantiateOnlyNested;
import templates.instantiate_only_nested.N32;
import templates.instantiate_only_nested.pkg.Test_uint32;

public class InstantiateOnlyNestedTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateOnlyNested instantiateOnlyNested = new InstantiateOnlyNested();
        instantiateOnlyNested.setTest32(new Test_uint32(new N32(13)));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiateOnlyNested.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final InstantiateOnlyNested readInstantiateOnlyNested = new InstantiateOnlyNested(reader);
        reader.close();
        assertTrue(instantiateOnlyNested.equals(readInstantiateOnlyNested));
    }

    private static final File TEST_FILE = new File("test.bin");
}
