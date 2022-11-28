package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.struct_templated_type_argument.StructTemplatedTypeArgument;
import templates.struct_templated_type_argument.ParamHolder_uint32;
import templates.struct_templated_type_argument.Parameterized_uint32;

public class StructTemplatedTypeArgumentTest
{
    @Test
    public void readWrite() throws IOException
    {
        final StructTemplatedTypeArgument structTemplatedTypeArgument = new StructTemplatedTypeArgument();
        final ParamHolder_uint32 paramHolder = new ParamHolder_uint32(42);
        structTemplatedTypeArgument.setParamHolder(paramHolder);
        final Parameterized_uint32 parameterized = new Parameterized_uint32(paramHolder);
        parameterized.setDescription("description");
        parameterized.setId(13);
        structTemplatedTypeArgument.setParameterized(parameterized);

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        structTemplatedTypeArgument.initializeOffsets(writer.getBitPosition());
        structTemplatedTypeArgument.write(writer);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final StructTemplatedTypeArgument readStructTemplatedTypeArgument = new StructTemplatedTypeArgument(reader);
        assertTrue(structTemplatedTypeArgument.equals(readStructTemplatedTypeArgument));
    }
}
