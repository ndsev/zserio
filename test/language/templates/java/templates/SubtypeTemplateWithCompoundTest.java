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

import templates.subtype_template_with_compound.TestStructure_Compound_TemplateCompound_Compound;
import templates.subtype_template_with_compound.TemplateCompound_Compound;
import templates.subtype_template_with_compound.Compound;

public class SubtypeTemplateWithCompoundTest
{
    @Test
    public void readWrite() throws IOException
    {
        TestStructure_Compound_TemplateCompound_Compound subtypeTemplateWithCompound =
                new TestStructure_Compound_TemplateCompound_Compound();
        subtypeTemplateWithCompound.setValue1(new Compound(13));
        subtypeTemplateWithCompound.setValue2(new TemplateCompound_Compound(new Compound(42)));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        subtypeTemplateWithCompound.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        TestStructure_Compound_TemplateCompound_Compound readSubtypeTemplateWithCompound =
                new TestStructure_Compound_TemplateCompound_Compound(reader);
        reader.close();
        assertTrue(subtypeTemplateWithCompound.equals(readSubtypeTemplateWithCompound));
    }

    private static final File TEST_FILE = new File("test.bin");
}
