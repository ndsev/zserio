package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.struct_full_and_short_template_argument.StructFullNameTemplateArgument;
import templates.struct_full_and_short_template_argument.templated_struct.Storage;
import templates.struct_full_and_short_template_argument.templated_struct.StructShortNameTemplateArgument;
import templates.struct_full_and_short_template_argument.templated_struct.TemplatedStruct_Storage;

public class StructFullAndShortTemplateArgumentTest
{
    @Test
    public void readWriteFull() throws IOException
    {
        final StructFullNameTemplateArgument structFullNameTemplateArgument =
                new StructFullNameTemplateArgument(new TemplatedStruct_Storage(new Storage("String")));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        structFullNameTemplateArgument.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final StructFullNameTemplateArgument readStructFullNameTemplateArgument =
                new StructFullNameTemplateArgument(reader);
        assertTrue(structFullNameTemplateArgument.equals(readStructFullNameTemplateArgument));
    }

    @Test
    public void readWriteShort() throws IOException
    {
        final StructShortNameTemplateArgument structShortNameTemplateArgument =
                new StructShortNameTemplateArgument(new TemplatedStruct_Storage(new Storage("String")));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        structShortNameTemplateArgument.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final StructShortNameTemplateArgument readStructShortNameTemplateArgument =
                new StructShortNameTemplateArgument(reader);
        assertTrue(structShortNameTemplateArgument.equals(readStructShortNameTemplateArgument));
    }
}
