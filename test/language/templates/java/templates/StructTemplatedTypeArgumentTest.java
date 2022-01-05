package templates;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import zserio.runtime.BitSizeOfCalculator;
import zserio.runtime.ZserioError;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.BitStreamWriter;
import zserio.runtime.io.FileBitStreamReader;
import zserio.runtime.io.FileBitStreamWriter;

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

        final BitStreamWriter writer = new FileBitStreamWriter(TEST_FILE);
        structTemplatedTypeArgument.write(writer);
        writer.close();
        final BitStreamReader reader = new FileBitStreamReader(TEST_FILE);

        final StructTemplatedTypeArgument readStructTemplatedTypeArgument = new StructTemplatedTypeArgument(reader);
        reader.close();
        assertTrue(structTemplatedTypeArgument.equals(readStructTemplatedTypeArgument));
    }

    private static final File TEST_FILE = new File("test.bin");
}
