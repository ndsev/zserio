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

import templates.subtype_template_with_builtin.SubtypeTemplateWithBuiltin;
import templates.subtype_template_with_builtin.TestStructure_uint32;

public class SubtypeTemplateWithBuiltinTest
{
    @Test
    public void readWrite() throws IOException
    {
        SubtypeTemplateWithBuiltin subtypeTemplateWithBuiltin = new SubtypeTemplateWithBuiltin();
        subtypeTemplateWithBuiltin.setTest(new TestStructure_uint32(13));

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        subtypeTemplateWithBuiltin.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        SubtypeTemplateWithBuiltin readSubtypeTemplateWithBuiltin = new SubtypeTemplateWithBuiltin(reader);
        reader.close();
        assertTrue(subtypeTemplateWithBuiltin.equals(readSubtypeTemplateWithBuiltin));
    }

    private static final File TEST_FILE = new File("test.bin");
}
