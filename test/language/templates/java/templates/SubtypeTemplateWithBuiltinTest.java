package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.subtype_template_with_builtin.SubtypeTemplateWithBuiltin;
import templates.subtype_template_with_builtin.TestStructure_uint32;

public class SubtypeTemplateWithBuiltinTest
{
    @Test
    public void readWrite() throws IOException
    {
        SubtypeTemplateWithBuiltin subtypeTemplateWithBuiltin = new SubtypeTemplateWithBuiltin();
        subtypeTemplateWithBuiltin.setTest(new TestStructure_uint32(13));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        subtypeTemplateWithBuiltin.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        SubtypeTemplateWithBuiltin readSubtypeTemplateWithBuiltin = new SubtypeTemplateWithBuiltin(reader);
        assertTrue(subtypeTemplateWithBuiltin.equals(readSubtypeTemplateWithBuiltin));
    }
}
