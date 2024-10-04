package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.struct_long_template_argument_clash.StructLongTemplateArgumentClash;
import templates.struct_long_template_argument_clash.TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_Th_1B45EF08;
import templates.struct_long_template_argument_clash.TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_Th_619A1B35;
import templates.struct_long_template_argument_clash.ThisIsVeryVeryVeryLongNamedStructure;
import templates.struct_long_template_argument_clash.ThisIsVeryVeryVeryLongNamedStructure_;

public class StructLongTemplateArgumentClashTest
{
    @Test
    public void readWrite() throws IOException
    {
        final TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_Th_619A1B35 t1 =
                new TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_Th_619A1B35(
                        new ThisIsVeryVeryVeryLongNamedStructure("StringT"),
                        new ThisIsVeryVeryVeryLongNamedStructure("StringU"),
                        new ThisIsVeryVeryVeryLongNamedStructure("StringV"));
        final TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_Th_1B45EF08 t2 =
                new TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_Th_1B45EF08(
                        new ThisIsVeryVeryVeryLongNamedStructure("StringT"),
                        new ThisIsVeryVeryVeryLongNamedStructure("StringU"),
                        new ThisIsVeryVeryVeryLongNamedStructure_(42));
        final StructLongTemplateArgumentClash structLongTemplateArgumentClash =
                new StructLongTemplateArgumentClash(t1, t2);

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        structLongTemplateArgumentClash.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final StructLongTemplateArgumentClash readStructLongTemplateArgumentClash =
                new StructLongTemplateArgumentClash(reader);
        assertTrue(structLongTemplateArgumentClash.equals(readStructLongTemplateArgumentClash));
    }
}
