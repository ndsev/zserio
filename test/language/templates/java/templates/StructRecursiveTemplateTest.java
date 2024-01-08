package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.struct_recursive_template.Compound_Compound_Compound_string;
import templates.struct_recursive_template.Compound_Compound_string;
import templates.struct_recursive_template.Compound_Compound_uint32;
import templates.struct_recursive_template.Compound_string;
import templates.struct_recursive_template.Compound_uint32;
import templates.struct_recursive_template.StructRecursiveTemplate;

public class StructRecursiveTemplateTest
{
    @Test
    public void readWrite() throws IOException
    {
        final StructRecursiveTemplate structRecursiveTemplate = new StructRecursiveTemplate();
        structRecursiveTemplate.setCompound1(new Compound_Compound_uint32(new Compound_uint32(42)));
        structRecursiveTemplate.setCompound2(new Compound_Compound_Compound_string(
                new Compound_Compound_string(new Compound_string("string"))));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        structRecursiveTemplate.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final StructRecursiveTemplate readStructRecursiveTemplate = new StructRecursiveTemplate(reader);
        assertTrue(structRecursiveTemplate.equals(readStructRecursiveTemplate));
    }
}
