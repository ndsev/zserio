package parameterized_types.extern_param;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.SerializeUtil;

public class ExternParamTest
{
    @Test
    public void writeRead() throws IOException
    {
        final BitBuffer externData = new BitBuffer(new byte[] {(byte)0xCA, (byte)0xFE}, 15);
        final ExternParam externParam = new ExternParam(externData, new Parameterized(externData, 13));

        assertTrue(externParam.getExternField() == externParam.getParameterizedField().getParam());

        final BitBuffer bitBuffer = SerializeUtil.serialize(externParam);
        final ExternParam readExternParam = SerializeUtil.deserialize(ExternParam.class, bitBuffer);
        assertEquals(externParam, readExternParam);
    }
}
