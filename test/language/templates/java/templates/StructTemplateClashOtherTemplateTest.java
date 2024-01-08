package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.struct_template_clash_other_template.A_uint32;
import templates.struct_template_clash_other_template.InstantiationNameClashOtherTemplate;
import templates.struct_template_clash_other_template.Test_A_uint32_5D68B0C2;
import templates.struct_template_clash_other_template.Test_A_uint32_FA82A3B7;

public class StructTemplateClashOtherTemplateTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiationNameClashOtherTemplate instantiationNameClashOtherTemplate =
                new InstantiationNameClashOtherTemplate(
                        new Test_A_uint32_FA82A3B7(42), new Test_A_uint32_5D68B0C2(new A_uint32(10)));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiationNameClashOtherTemplate.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final InstantiationNameClashOtherTemplate readInstantiationNameClashOtherTemplate =
                new InstantiationNameClashOtherTemplate(reader);
        assertTrue(instantiationNameClashOtherTemplate.equals(readInstantiationNameClashOtherTemplate));
    }
}
