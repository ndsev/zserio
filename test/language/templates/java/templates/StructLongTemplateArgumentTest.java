package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.struct_long_template_argument.StructLongTemplateArgument;
import templates.struct_long_template_argument.ThisIsVeryVeryVeryLongNamedStructure;
import templates.struct_long_template_argument.TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVery_;

public class StructLongTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVery_ templ =
                new TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVery_(
                        new ThisIsVeryVeryVeryLongNamedStructure("StringT"),
                        new ThisIsVeryVeryVeryLongNamedStructure("StringU"),
                        new ThisIsVeryVeryVeryLongNamedStructure("StringV"));
        final StructLongTemplateArgument structLongTemplateArgument = new StructLongTemplateArgument(templ);

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        structLongTemplateArgument.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final StructLongTemplateArgument readStructLongTemplateArgument =
                new StructLongTemplateArgument(reader);
        assertTrue(structLongTemplateArgument.equals(readStructLongTemplateArgument));
    }
}
