package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.struct_template_clash_other_type.InstantiationNameClashOtherType;
import templates.struct_template_clash_other_type.Test_uint32_99604043;

public class StructTemplateClashOtherTypeTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiationNameClashOtherType instantiationNameClashOtherType =
                new InstantiationNameClashOtherType(new Test_uint32_99604043(42));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiationNameClashOtherType.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final InstantiationNameClashOtherType readInstantiationNameClashOtherType =
                new InstantiationNameClashOtherType(reader);
        assertTrue(instantiationNameClashOtherType.equals(readInstantiationNameClashOtherType));
    }
}
