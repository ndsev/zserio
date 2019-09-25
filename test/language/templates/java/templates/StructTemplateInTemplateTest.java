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

import templates.struct_template_in_template.StructTemplateInTemplate;
import templates.struct_template_in_template.Field_uint32;
import templates.struct_template_in_template.Field_string;
import templates.struct_template_in_template.Compound_uint32;
import templates.struct_template_in_template.Compound_string;

public class StructTemplateInTemplateTest
{
    @Test
    public void readWrite() throws IOException
    {
        StructTemplateInTemplate structTemplateInTemplate = new StructTemplateInTemplate();
        structTemplateInTemplate.setUint32Field(new Field_uint32(new Compound_uint32(42)));
        structTemplateInTemplate.setStringField(new Field_string(new Compound_string("string")));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        structTemplateInTemplate.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        StructTemplateInTemplate readStructTemplateInTemplate = new StructTemplateInTemplate(reader);
        reader.close();
        assertTrue(structTemplateInTemplate.equals(readStructTemplateInTemplate));
    }

    private static final File TEST_FILE = new File("test.bin");
}
;
