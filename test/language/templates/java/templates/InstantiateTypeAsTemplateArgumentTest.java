package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.instantiate_type_as_template_argument.InstantiateTypeAsTemplateArgument;
import templates.instantiate_type_as_template_argument.Other_Str;
import templates.instantiate_type_as_template_argument.Str;

public class InstantiateTypeAsTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateTypeAsTemplateArgument instantiateTypeAsTemplateArgument =
                new InstantiateTypeAsTemplateArgument(new Other_Str(new Str("test")));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiateTypeAsTemplateArgument.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        InstantiateTypeAsTemplateArgument readInstantiateTypeAsTemplateArgument =
                new InstantiateTypeAsTemplateArgument(reader);
        assertTrue(instantiateTypeAsTemplateArgument.equals(readInstantiateTypeAsTemplateArgument));
    }
}
