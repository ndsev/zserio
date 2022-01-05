package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

import templates.struct_recursive_template.StructRecursiveTemplate;
import templates.struct_recursive_template.Compound_uint32;
import templates.struct_recursive_template.Compound_Compound_uint32;
import templates.struct_recursive_template.Compound_string;
import templates.struct_recursive_template.Compound_Compound_string;
import templates.struct_recursive_template.Compound_Compound_Compound_string;

public class StructRecursiveTemplateTest
{
    @Test
    public void readWrite() throws IOException
    {
        final StructRecursiveTemplate structRecursiveTemplate = new StructRecursiveTemplate();
        structRecursiveTemplate.setCompound1(new Compound_Compound_uint32(
                new Compound_uint32(42)));
        structRecursiveTemplate.setCompound2(new Compound_Compound_Compound_string(
                new Compound_Compound_string(new Compound_string("string"))));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        structRecursiveTemplate.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final StructRecursiveTemplate readStructRecursiveTemplate = new StructRecursiveTemplate(reader);
        reader.close();
        assertTrue(structRecursiveTemplate.equals(readStructRecursiveTemplate));
    }

    private static final File TEST_FILE = new File("test.bin");
}
