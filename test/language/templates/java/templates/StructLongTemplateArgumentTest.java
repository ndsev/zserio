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

import templates.struct_long_template_argument.StructLongTemplateArgument;
import templates.struct_long_template_argument.ThisIsVeryVeryVeryLongNamedStructure;
import templates.struct_long_template_argument.
        TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVeryVeryVeryLongNamedStruct_;

public class StructLongTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVeryVeryVeryLongNamedStruct_ templ =
                new TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVeryVeryVeryLongNamedStruct_(
                        new ThisIsVeryVeryVeryLongNamedStructure("StringT"),
                        new ThisIsVeryVeryVeryLongNamedStructure("StringU"),
                        new ThisIsVeryVeryVeryLongNamedStructure("StringV"));
        final StructLongTemplateArgument structLongTemplateArgument = new StructLongTemplateArgument(templ);

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        structLongTemplateArgument.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final StructLongTemplateArgument readStructLongTemplateArgument =
                new StructLongTemplateArgument(reader);
        reader.close();
        assertTrue(structLongTemplateArgument.equals(readStructLongTemplateArgument));
    }

    private static final File TEST_FILE = new File("test.bin");
}
