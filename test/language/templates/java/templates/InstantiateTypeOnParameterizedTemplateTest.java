package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.instantiate_type_on_parameterized_template.InstantiateTypeOnParameterizedTemplate;
import templates.instantiate_type_on_parameterized_template.Parameterized;
import templates.instantiate_type_on_parameterized_template.TestP;

public class InstantiateTypeOnParameterizedTemplateTest
{
    @Test
    public void readWrite() throws IOException
    {
        final long[] array = new long[] {13, 42};
        final Parameterized parameterized = new Parameterized(2, array);
        final InstantiateTypeOnParameterizedTemplate instantiateTypeOnParameterizedTemplate =
                new InstantiateTypeOnParameterizedTemplate(2, new TestP(2, parameterized));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiateTypeOnParameterizedTemplate.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        InstantiateTypeOnParameterizedTemplate readInstantiateTypeOnParameterizedTemplate =
                new InstantiateTypeOnParameterizedTemplate(reader);
        assertTrue(instantiateTypeOnParameterizedTemplate.equals(readInstantiateTypeOnParameterizedTemplate));
    }
}
