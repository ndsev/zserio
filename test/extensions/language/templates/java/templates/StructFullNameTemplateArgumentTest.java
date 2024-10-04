package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.struct_full_name_template_argument.Storage;
import templates.struct_full_name_template_argument.StructFullNameTemplateArgument;
import templates.struct_full_name_template_argument.TemplatedStruct_Storage_A3A4B101;
import templates.struct_full_name_template_argument.TemplatedStruct_Storage_C76E422F;

public class StructFullNameTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final StructFullNameTemplateArgument structFullNameTemplateArgument =
                new StructFullNameTemplateArgument();
        structFullNameTemplateArgument.setStructExternal(new TemplatedStruct_Storage_C76E422F(
                new templates.struct_full_name_template_argument.import_storage.Storage((long)42)));
        structFullNameTemplateArgument.setStructInternal(
                new TemplatedStruct_Storage_A3A4B101(new Storage("string")));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        structFullNameTemplateArgument.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final StructFullNameTemplateArgument readStructFullNameTemplateArgument =
                new StructFullNameTemplateArgument(reader);
        assertTrue(structFullNameTemplateArgument.equals(readStructFullNameTemplateArgument));
    }
}
