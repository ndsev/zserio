package parameterized_types.bytes_param;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.SerializeUtil;

public class BytesParamTest
{
    @Test
    public void writeRead() throws IOException
    {
        final byte[] bytesData = new byte[] {(byte)0xCA, (byte)0xFE};
        final BytesParam bytesParam = new BytesParam(bytesData, new Parameterized(bytesData, 13));

        assertTrue(bytesParam.getBytesField() == bytesParam.getParameterizedField().getParam());

        final BitBuffer bitBuffer = SerializeUtil.serialize(bytesParam);
        final BytesParam readBytesParam = SerializeUtil.deserialize(BytesParam.class, bitBuffer);
        assertEquals(bytesParam, readBytesParam);
    }
}
