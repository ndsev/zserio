package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.subtype_template_with_compound.Compound;
import templates.subtype_template_with_compound.TemplateCompound_Compound;
import templates.subtype_template_with_compound.TestStructure_Compound_TemplateCompound_Compound;

public class SubtypeTemplateWithCompoundTest
{
    @Test
    public void readWrite() throws IOException
    {
        TestStructure_Compound_TemplateCompound_Compound subtypeTemplateWithCompound =
                new TestStructure_Compound_TemplateCompound_Compound();
        subtypeTemplateWithCompound.setValue1(new Compound(13));
        subtypeTemplateWithCompound.setValue2(new TemplateCompound_Compound(new Compound(42)));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        subtypeTemplateWithCompound.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        TestStructure_Compound_TemplateCompound_Compound readSubtypeTemplateWithCompound =
                new TestStructure_Compound_TemplateCompound_Compound(reader);
        assertTrue(subtypeTemplateWithCompound.equals(readSubtypeTemplateWithCompound));
    }
}
