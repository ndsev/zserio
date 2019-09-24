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

import templates.recursive_template.RecursiveTemplate;
import templates.recursive_template.Compound_uint32;
import templates.recursive_template.Compound_Compound_uint32;
import templates.recursive_template.Compound_string;
import templates.recursive_template.Compound_Compound_string;
import templates.recursive_template.Compound_Compound_Compound_string;

public class RecursiveTemplateTest
{
    @Test
    public void readWrite() throws IOException
    {
        final RecursiveTemplate recursiveTemplate = new RecursiveTemplate();
        recursiveTemplate.setCompound1(new Compound_Compound_uint32(
                new Compound_uint32(42)));
        recursiveTemplate.setCompound2(new Compound_Compound_Compound_string(
                new Compound_Compound_string(new Compound_string("string"))));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        recursiveTemplate.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final RecursiveTemplate readRecursiveTemplate = new RecursiveTemplate(reader);
        reader.close();
        assertTrue(recursiveTemplate.equals(readRecursiveTemplate));
    }

    private static final File TEST_FILE = new File("test.bin");
}
