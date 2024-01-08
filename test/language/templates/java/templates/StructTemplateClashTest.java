package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.struct_template_clash.A;
import templates.struct_template_clash.A_B;
import templates.struct_template_clash.B_C;
import templates.struct_template_clash.C;
import templates.struct_template_clash.InstantiationNameClash;
import templates.struct_template_clash.Template_A_B_C_5EB4E3FC;
import templates.struct_template_clash.Template_A_B_C_7FE93D34;
import templates.struct_template_clash.TestStruct_uint32;

public class StructTemplateClashTest
{
    @Test
    public void readWrite() throws IOException
    {
        final TestStruct_uint32 testStruct_uint32 = new TestStruct_uint32(42,
                new Template_A_B_C_7FE93D34(new A_B(1), new C(true)),
                new Template_A_B_C_5EB4E3FC(new A(1), new B_C("string")));
        final InstantiationNameClash instantiationNameClash = new InstantiationNameClash(testStruct_uint32);

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiationNameClash.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final InstantiationNameClash readInstantiationNameClash = new InstantiationNameClash(reader);
        assertTrue(instantiationNameClash.equals(readInstantiationNameClash));
    }
}
