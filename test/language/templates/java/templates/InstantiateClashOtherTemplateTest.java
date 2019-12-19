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

import templates.instantiate_clash_other_template.InstantiateClashOtherTemplate;
import templates.instantiate_clash_other_template.Test_uint32_99604043;

public class InstantiateClashOtherTemplateTest
{
    @Test
    public void readWrite() throws IOException
    {
        final Test_uint32_99604043 test = new Test_uint32_99604043(13); 
        final InstantiateClashOtherTemplate instantiateClashOtherTemplate =
                new InstantiateClashOtherTemplate(test);

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiateClashOtherTemplate.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final InstantiateClashOtherTemplate readInstantiateClashOtherTemplate =
                new InstantiateClashOtherTemplate(reader);
        reader.close();
        assertTrue(instantiateClashOtherTemplate.equals(readInstantiateClashOtherTemplate));
    }

    private static final File TEST_FILE = new File("test.bin");
}
