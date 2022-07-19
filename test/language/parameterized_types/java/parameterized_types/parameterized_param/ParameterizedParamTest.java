package parameterized_types.parameterized_param;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.FileBitStreamReader;

public class ParameterizedParamTest
{
    @Test
    public void fileWrite() throws IOException
    {
        final ParameterizedParamHolder parameterizedParamHolder = createParameterizedParamHolder();
        final File file = new File("test1.bin");
        parameterizedParamHolder.write(file);
        checkParameterizedParamHolderInFile(file, parameterizedParamHolder);
        final ParameterizedParamHolder readParameterizedParamHolder =
                new ParameterizedParamHolder(file);
        assertEquals(parameterizedParamHolder, readParameterizedParamHolder);
    }

    private ParameterizedParamHolder createParameterizedParamHolder()
    {
        final Param param = new Param(PARAMETER, PARAM_VALUE, PARAM_EXTRA_VALUE);
        final ParameterizedParam parameterizedParam = new ParameterizedParam(param, PARAMETERIZED_PARAM_VALUE,
                PARAMETERIZED_PARAM_EXTRA_VALUE);

        return new ParameterizedParamHolder(PARAMETER, param, parameterizedParam);
    }

    private void checkParameterizedParamHolderInFile(File file, ParameterizedParamHolder
            parameterizedParamHolder) throws IOException
    {
        final BitStreamReader stream = new FileBitStreamReader(file);

        assertEquals(parameterizedParamHolder.getParameter(), stream.readUnsignedShort());

        final Param param = parameterizedParamHolder.getParam();
        assertEquals(param.getParameter(), PARAMETER);
        assertEquals(param.getValue(), stream.readUnsignedShort());
        assertEquals(param.getExtraValue(), stream.readUnsignedInt());

        final ParameterizedParam parameterizedParam = parameterizedParamHolder.getParameterizedParam();
        assertEquals(parameterizedParam.getParam(), param);
        assertEquals(parameterizedParam.getValue(), stream.readUnsignedShort());
        assertEquals(parameterizedParam.getExtraValue(), stream.readUnsignedInt());

        stream.close();
    }

    static final int PARAMETER = 11;
    static final int PARAM_VALUE = 0x0BED;
    static final long PARAM_EXTRA_VALUE = 0x0BEDDEAD;
    static final int PARAMETERIZED_PARAM_VALUE = 0x0BAD;
    static final long PARAMETERIZED_PARAM_EXTRA_VALUE = 0x0BADDEAD;
}
