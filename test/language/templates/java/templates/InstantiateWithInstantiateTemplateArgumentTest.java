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

import templates.instantiate_with_instantiate_template_argument.InstantiateWithInstantiateTemplateArgument;
import templates.instantiate_with_instantiate_template_argument.Other32;
import templates.instantiate_with_instantiate_template_argument.Other8;
import templates.instantiate_with_instantiate_template_argument.Data32;
import templates.instantiate_with_instantiate_template_argument.Data8;

public class InstantiateWithInstantiateTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateWithInstantiateTemplateArgument instantiateWithInstantiateTemplateArgument =
                new InstantiateWithInstantiateTemplateArgument();
        instantiateWithInstantiateTemplateArgument.setOther8(new Other8(new Data8((byte)13)));
        instantiateWithInstantiateTemplateArgument.setOther32(new Other32(new Data32(0xCAFE)));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiateWithInstantiateTemplateArgument.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final InstantiateWithInstantiateTemplateArgument readInstantiateWithInstantiateTemplateArgument =
                new InstantiateWithInstantiateTemplateArgument(reader);
        reader.close();
        assertTrue(instantiateWithInstantiateTemplateArgument.equals(
                readInstantiateWithInstantiateTemplateArgument));
    }

    private static final File TEST_FILE = new File("test.bin");
}
