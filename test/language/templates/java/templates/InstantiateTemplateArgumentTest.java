package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.instantiate_template_argument.InstantiateTemplateArgument;
import templates.instantiate_template_argument.Other_Str;
import templates.instantiate_template_argument.Str;

public class InstantiateTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateTemplateArgument instantiateTemplateArgument =
                new InstantiateTemplateArgument(new Other_Str(new Str("test")));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiateTemplateArgument.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        InstantiateTemplateArgument readInstantiateTemplateArgument = new InstantiateTemplateArgument(reader);
        assertTrue(instantiateTemplateArgument.equals(readInstantiateTemplateArgument));
    }
}
