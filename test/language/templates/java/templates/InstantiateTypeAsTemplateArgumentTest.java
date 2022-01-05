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

import templates.instantiate_type_as_template_argument.InstantiateTypeAsTemplateArgument;
import templates.instantiate_type_as_template_argument.Other_Str;
import templates.instantiate_type_as_template_argument.Str;

public class InstantiateTypeAsTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateTypeAsTemplateArgument instantiateTypeAsTemplateArgument =
                new InstantiateTypeAsTemplateArgument(new Other_Str(new Str("test")));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        instantiateTypeAsTemplateArgument.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        InstantiateTypeAsTemplateArgument readInstantiateTypeAsTemplateArgument =
                new InstantiateTypeAsTemplateArgument(reader);
        reader.close();
        assertTrue(instantiateTypeAsTemplateArgument.equals(readInstantiateTypeAsTemplateArgument));
    }

    private static final File TEST_FILE = new File("test.bin");
}
