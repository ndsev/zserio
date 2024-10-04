package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.instantiate_clash_other_template.InstantiateClashOtherTemplate;
import templates.instantiate_clash_other_template.Test_uint32_99604043;

public class InstantiateClashOtherTemplateTest
{
    @Test
    public void readWrite() throws IOException
    {
        final Test_uint32_99604043 test = new Test_uint32_99604043(13);
        final InstantiateClashOtherTemplate instantiateClashOtherTemplate =
                new InstantiateClashOtherTemplate(test);

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiateClashOtherTemplate.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final InstantiateClashOtherTemplate readInstantiateClashOtherTemplate =
                new InstantiateClashOtherTemplate(reader);
        assertTrue(instantiateClashOtherTemplate.equals(readInstantiateClashOtherTemplate));
    }
}
