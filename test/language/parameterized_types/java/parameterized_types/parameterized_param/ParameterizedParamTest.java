package parameterized_types.parameterized_param;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

public class ParameterizedParamTest
{
    @Test
    public void writeRead() throws IOException
    {
        final ParameterizedParamHolder parameterizedParamHolder = createParameterizedParamHolder();

        final BitBuffer bitBuffer = SerializeUtil.serialize(parameterizedParamHolder);
        checkParameterizedParamHolderInBitBuffer(bitBuffer, parameterizedParamHolder);
        final ParameterizedParamHolder readParameterizedParamHolder =
                SerializeUtil.deserialize(ParameterizedParamHolder.class, bitBuffer);
        assertEquals(parameterizedParamHolder, readParameterizedParamHolder);
    }

    private ParameterizedParamHolder createParameterizedParamHolder()
    {
        final Param param = new Param(PARAMETER, PARAM_VALUE, PARAM_EXTRA_VALUE);
        final ParameterizedParam parameterizedParam = new ParameterizedParam(param, PARAMETERIZED_PARAM_VALUE,
                PARAMETERIZED_PARAM_EXTRA_VALUE);

        return new ParameterizedParamHolder(PARAMETER, param, parameterizedParam);
    }

    private void checkParameterizedParamHolderInBitBuffer(BitBuffer bitBuffer, ParameterizedParamHolder
            parameterizedParamHolder) throws IOException
    {
        try (final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer))
        {
            assertEquals(parameterizedParamHolder.getParameter(), reader.readUnsignedShort());

            final Param param = parameterizedParamHolder.getParam();
            assertEquals(param.getParameter(), PARAMETER);
            assertEquals(param.getValue(), reader.readUnsignedShort());
            assertEquals(param.getExtraValue(), reader.readUnsignedInt());

            final ParameterizedParam parameterizedParam = parameterizedParamHolder.getParameterizedParam();
            assertEquals(parameterizedParam.getParam(), param);
            assertEquals(parameterizedParam.getValue(), reader.readUnsignedShort());
            assertEquals(parameterizedParam.getExtraValue(), reader.readUnsignedInt());
        }
    }

    static final int PARAMETER = 11;
    static final int PARAM_VALUE = 0x0BED;
    static final long PARAM_EXTRA_VALUE = 0x0BEDDEAD;
    static final int PARAMETERIZED_PARAM_VALUE = 0x0BAD;
    static final long PARAMETERIZED_PARAM_EXTRA_VALUE = 0x0BADDEAD;
}
