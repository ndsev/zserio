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

import templates.template_in_template.TemplateInTemplate;
import templates.template_in_template.Field_uint32;
import templates.template_in_template.Field_string;
import templates.template_in_template.Compound_uint32;
import templates.template_in_template.Compound_string;

public class TemplateInTemplateTest
{
    @Test
    public void readWrite() throws IOException
    {
        TemplateInTemplate templateInTemplate = new TemplateInTemplate();
        templateInTemplate.setUint32Field(new Field_uint32(new Compound_uint32(42)));
        templateInTemplate.setStringField(new Field_string(new Compound_string("string")));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        templateInTemplate.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        TemplateInTemplate readTemplateInTemplate = new TemplateInTemplate(reader);
        reader.close();
        assertTrue(templateInTemplate.equals(readTemplateInTemplate));
    }

    private static final File TEST_FILE = new File("test.bin");
}
;
