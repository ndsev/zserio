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

import templates.struct_long_template_argument_clash.StructLongTemplateArgumentClash;
import templates.struct_long_template_argument_clash.ThisIsVeryVeryVeryLongNamedStructure;
import templates.struct_long_template_argument_clash.ThisIsVeryVeryVeryLongNamedStructure_;
import templates.struct_long_template_argument_clash.
        TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVeryVeryVeryLongNamedStruct__619A1B35;
import templates.struct_long_template_argument_clash.
        TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVeryVeryVeryLongNamedStruct__1B45EF08;

public class StructLongTemplateArgumentClashTest
{
    @Test
    public void readWrite() throws IOException
    {
        final TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVeryVeryVeryLongNamedStruct__619A1B35 t1 =
                new TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVeryVeryVeryLongNamedStruct__619A1B35(
                        new ThisIsVeryVeryVeryLongNamedStructure("StringT"),
                        new ThisIsVeryVeryVeryLongNamedStructure("StringU"),
                        new ThisIsVeryVeryVeryLongNamedStructure("StringV"));
        final TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVeryVeryVeryLongNamedStruct__1B45EF08 t2 =
                new TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVeryVeryVeryLongNamedStruct__1B45EF08(
                        new ThisIsVeryVeryVeryLongNamedStructure("StringT"),
                        new ThisIsVeryVeryVeryLongNamedStructure("StringU"),
                        new ThisIsVeryVeryVeryLongNamedStructure_(42));
        final StructLongTemplateArgumentClash structLongTemplateArgumentClash =
                new StructLongTemplateArgumentClash(t1, t2);

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        structLongTemplateArgumentClash.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final StructLongTemplateArgumentClash readStructLongTemplateArgumentClash =
                new StructLongTemplateArgumentClash(reader);
        reader.close();
        assertTrue(structLongTemplateArgumentClash.equals(readStructLongTemplateArgumentClash));
    }

    private static final File TEST_FILE = new File("test.bin");
}
