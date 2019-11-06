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

import templates.instantiate_template_argument.InstantiateTemplateArgument;
import templates.instantiate_template_argument.Other_Str;
import templates.instantiate_template_argument.Str;

public class InstantiateTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateTemplateArgument instantiateTemplateArgument =
                new InstantiateTemplateArgument(new Other_Str(new Str("test")));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiateTemplateArgument.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        InstantiateTemplateArgument readInstantiateTemplateArgument =
                new InstantiateTemplateArgument(reader);
        reader.close();
        assertTrue(instantiateTemplateArgument.equals(readInstantiateTemplateArgument));
    }

    private static final File TEST_FILE = new File("test.bin");
}
