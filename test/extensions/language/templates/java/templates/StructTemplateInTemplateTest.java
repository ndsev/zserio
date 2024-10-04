package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.struct_template_in_template.Compound_string;
import templates.struct_template_in_template.Compound_uint32;
import templates.struct_template_in_template.Field_string;
import templates.struct_template_in_template.Field_uint32;
import templates.struct_template_in_template.StructTemplateInTemplate;

public class StructTemplateInTemplateTest
{
    @Test
    public void readWrite() throws IOException
    {
        StructTemplateInTemplate structTemplateInTemplate = new StructTemplateInTemplate();
        structTemplateInTemplate.setUint32Field(new Field_uint32(new Compound_uint32(42)));
        structTemplateInTemplate.setStringField(new Field_string(new Compound_string("string")));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        structTemplateInTemplate.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        StructTemplateInTemplate readStructTemplateInTemplate = new StructTemplateInTemplate(reader);
        assertTrue(structTemplateInTemplate.equals(readStructTemplateInTemplate));
    }
}
