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

import templates.struct_templated_template_argument.StructTemplatedTemplateArgument;
import templates.struct_templated_template_argument.Field_Compound_uint32;
import templates.struct_templated_template_argument.Compound_uint32;

public class StructTemplatedTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final StructTemplatedTemplateArgument structTemplatedTemplateArgument =
                new StructTemplatedTemplateArgument();
        structTemplatedTemplateArgument.setCompoundField(new Field_Compound_uint32(new Compound_uint32(42)));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        structTemplatedTemplateArgument.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        StructTemplatedTemplateArgument readStructTemplatedTemplateArgument =
                new StructTemplatedTemplateArgument(reader);
        reader.close();
        assertTrue(structTemplatedTemplateArgument.equals(readStructTemplatedTemplateArgument));
    }

    private static final File TEST_FILE = new File("test.bin");
}
;
