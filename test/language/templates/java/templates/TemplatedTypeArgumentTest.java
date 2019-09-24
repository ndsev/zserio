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

import templates.templated_type_argument.TemplatedTypeArgument;
import templates.templated_type_argument.ParamHolder_uint32;
import templates.templated_type_argument.Parameterized_uint32;

public class TemplatedTypeArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final TemplatedTypeArgument templatedTypeArgument = new TemplatedTypeArgument();
        final ParamHolder_uint32 paramHolder = new ParamHolder_uint32(42);
        templatedTypeArgument.setParamHolder(paramHolder);
        final Parameterized_uint32 parameterized = new Parameterized_uint32(paramHolder);
        parameterized.setDescription("description");
        parameterized.setId(13);
        templatedTypeArgument.setParameterized(parameterized);

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        templatedTypeArgument.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final TemplatedTypeArgument readTemplatedTypeArgument = new TemplatedTypeArgument(reader);
        reader.close();
        assertTrue(templatedTypeArgument.equals(readTemplatedTypeArgument));
    }

    private static final File TEST_FILE = new File("test.bin");
}
