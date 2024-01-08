package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.struct_templated_template_argument.Compound_uint32;
import templates.struct_templated_template_argument.Field_Compound_uint32;
import templates.struct_templated_template_argument.StructTemplatedTemplateArgument;

public class StructTemplatedTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final StructTemplatedTemplateArgument structTemplatedTemplateArgument =
                new StructTemplatedTemplateArgument();
        structTemplatedTemplateArgument.setCompoundField(new Field_Compound_uint32(new Compound_uint32(42)));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        structTemplatedTemplateArgument.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        StructTemplatedTemplateArgument readStructTemplatedTemplateArgument =
                new StructTemplatedTemplateArgument(reader);
        assertTrue(structTemplatedTemplateArgument.equals(readStructTemplatedTemplateArgument));
    }
}
