package parameterized_types.param_with_optional;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;
import java.math.BigInteger;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

import parameterized_types.nested_parameterized_field.TopLevel;
import parameterized_types.nested_parameterized_field.ParamHolder;
import parameterized_types.nested_parameterized_field.Param;

public class NestedParameterizedFieldTest
{
    @Test
    public void bitSizeOf() throws IOException, ZserioError
    {
        final TopLevel topLevel = createTopLevel();
        final int bitPosition = 2;
        assertEquals(TOP_LEVEL_BIT_SIZE, topLevel.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets() throws IOException, ZserioError
    {
        final TopLevel topLevel = createTopLevel();
        final int bitPosition = 2;
        assertEquals(bitPosition + TOP_LEVEL_BIT_SIZE, topLevel.initializeOffsets(bitPosition));
    }

    @Test
    public void read() throws IOException, ZserioError
    {
        final BitBuffer buffer = writeTopLevelToBitBuffer();
        final TopLevel topLevel = SerializeUtil.deserialize(TopLevel.class, buffer);
        checkTopLevel(topLevel);
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final TopLevel topLevel = createTopLevel();
        final File file = new File(BLOB_NAME);
        SerializeUtil.serializeToFile(topLevel, file);

        final TopLevel readTopLevel = SerializeUtil.deserializeFromFile(TopLevel.class, file);
        checkTopLevel(readTopLevel);
    }

    private TopLevel createTopLevel()
    {
        final Param param = new Param(PARAMETER, VALUE, EXTRA_VALUE);
        final ParamHolder paramHolder = new ParamHolder(PARAMETER, param);

        return new TopLevel(paramHolder);
    }

    private BitBuffer writeTopLevelToBitBuffer() throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBits(PARAMETER, 16);
            writer.writeBits(VALUE, 16);
            writer.writeBits(EXTRA_VALUE, 32);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private void checkTopLevel(TopLevel topLevel)
    {
        assertEquals(PARAMETER, topLevel.getParamHolder().getParameter());
        assertEquals(VALUE, topLevel.getParamHolder().getParam().getValue());
        assertEquals(EXTRA_VALUE, topLevel.getParamHolder().getParam().getExtraValue());
    }

    private static final String BLOB_NAME = "nested_parameterized_field.blob";

    private static final int PARAMETER = 11;
    private static final int VALUE = 0xAB;
    private static final long EXTRA_VALUE = 0xDEAD;
    private static final int TOP_LEVEL_BIT_SIZE = 16 + 16 + 32;
}
