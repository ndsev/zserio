package templates;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.array.UnsignedIntArray;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import templates.instantiate_type_on_parameterized_template.InstantiateTypeOnParameterizedTemplate;
import templates.instantiate_type_on_parameterized_template.TestP;
import templates.instantiate_type_on_parameterized_template.Parameterized;

public class InstantiateTypeOnParameterizedTemplateTest
{
    @Test
    public void readWrite() throws IOException
    {
        final Parameterized parameterized = new Parameterized(2, new UnsignedIntArray(2));
        parameterized.getArray().setElementAt(13, 0);
        parameterized.getArray().setElementAt(42, 1);
        final InstantiateTypeOnParameterizedTemplate instantiateTypeOnParameterizedTemplate =
                new InstantiateTypeOnParameterizedTemplate(2, new TestP(2, parameterized));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiateTypeOnParameterizedTemplate.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        InstantiateTypeOnParameterizedTemplate readInstantiateTypeOnParameterizedTemplate =
                new InstantiateTypeOnParameterizedTemplate(reader);
        reader.close();
        assertTrue(instantiateTypeOnParameterizedTemplate.equals(readInstantiateTypeOnParameterizedTemplate));
    }

    private static final File TEST_FILE = new File("test.bin");
}
