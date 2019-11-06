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

import templates.instantiate_nested_template.InstantiateNestedTemplate;
import templates.instantiate_nested_template.TStr;
import templates.instantiate_nested_template.NStr;

public class InstantiateNestedTemplateTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateNestedTemplate instantiateNestedTemplate = new InstantiateNestedTemplate();
        instantiateNestedTemplate.setTest(new TStr(new NStr("test")));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiateNestedTemplate.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final InstantiateNestedTemplate readInstantiateNestedTemplate = new InstantiateNestedTemplate(reader);
        reader.close();
        assertTrue(instantiateNestedTemplate.equals(readInstantiateNestedTemplate));
    }

    private static final File TEST_FILE = new File("test.bin");
}
