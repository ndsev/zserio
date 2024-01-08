package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.instantiate_nested_template.InstantiateNestedTemplate;
import templates.instantiate_nested_template.NStr;
import templates.instantiate_nested_template.TStr;

public class InstantiateNestedTemplateTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateNestedTemplate instantiateNestedTemplate = new InstantiateNestedTemplate();
        instantiateNestedTemplate.setTest(new TStr(new NStr("test")));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiateNestedTemplate.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final InstantiateNestedTemplate readInstantiateNestedTemplate = new InstantiateNestedTemplate(reader);
        assertTrue(instantiateNestedTemplate.equals(readInstantiateNestedTemplate));
    }
}
