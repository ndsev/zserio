package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import templates.instantiate_vs_default.InstantiateVsDefault;
import templates.instantiate_vs_default.pkg.Test_uint32;
import templates.instantiate_vs_default.TStr;

public class InstantiateVsDefaultTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateVsDefault instantiateVsDefault = new InstantiateVsDefault();
        instantiateVsDefault.setTest32(new Test_uint32(13));
        instantiateVsDefault.setTestStr(new TStr("test"));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiateVsDefault.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final InstantiateVsDefault readInstantiateVsDefault = new InstantiateVsDefault(reader);
        reader.close();
        assertTrue(instantiateVsDefault.equals(readInstantiateVsDefault));
    }

    private static final File TEST_FILE = new File("test.bin");
}
