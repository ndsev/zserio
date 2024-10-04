package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.instantiate_with_instantiate_template_argument.Data32;
import templates.instantiate_with_instantiate_template_argument.Data8;
import templates.instantiate_with_instantiate_template_argument.InstantiateWithInstantiateTemplateArgument;
import templates.instantiate_with_instantiate_template_argument.Other32;
import templates.instantiate_with_instantiate_template_argument.Other8;

public class InstantiateWithInstantiateTemplateArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateWithInstantiateTemplateArgument instantiateWithInstantiateTemplateArgument =
                new InstantiateWithInstantiateTemplateArgument();
        instantiateWithInstantiateTemplateArgument.setOther8(new Other8(new Data8((byte)13)));
        instantiateWithInstantiateTemplateArgument.setOther32(new Other32(new Data32(0xCAFE)));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiateWithInstantiateTemplateArgument.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final InstantiateWithInstantiateTemplateArgument readInstantiateWithInstantiateTemplateArgument =
                new InstantiateWithInstantiateTemplateArgument(reader);
        assertTrue(instantiateWithInstantiateTemplateArgument.equals(
                readInstantiateWithInstantiateTemplateArgument));
    }
}
