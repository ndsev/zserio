package json_param_cast;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.DebugStringUtil;
import zserio.runtime.ZserioError;

public class JsonParamCastTest
{
    @Test
    public void parameterCast() throws ZserioError
    {
        final String inJson = "{"
                + "\"int8Param\": { \"data\": 1 },"
                + "\"int16Param\": { \"data\": 2 },"
                + "\"int32Param\": { \"data\": 3 },"
                + "\"int64Param\": { \"data\": 4 },"
                + "\"float16Param\": { \"data\": 5 },"
                + "\"float32Param\": { \"data\": 6 },"
                + "\"float64Param\": { \"data\": 7 },"
                + "\"param\": { \"data\": 8 }"
                + "}";
        final Object object = DebugStringUtil.fromJsonString(Holder.class, inJson);
        assertTrue(object instanceof Holder);
        final Holder holder = (Holder)object;
        assertEquals(1, holder.getInt8Param().getData());
        assertEquals(2, holder.getInt16Param().getData());
        assertEquals(3, holder.getInt32Param().getData());
        assertEquals(4, holder.getInt64Param().getData());
        assertEquals(5, holder.getFloat16Param().getData());
        assertEquals(6, holder.getFloat32Param().getData());
        assertEquals(7, holder.getFloat64Param().getData());
        assertEquals(8, holder.getParam().getData());
    }

    @Test
    public void toJsonFile() throws IOException
    {
        Holder holder = new Holder(10, new Int8Param((byte)0, 1), new Int16Param((short)030, 2),
                new Int32Param(0x30, 3), new Int64Param(0x7, 4), new Float16Param(0.2f, 5),
                new Float32Param(0.2f, 6), new Float64Param(0.1, 7), new Int32Param(10, 8));
        DebugStringUtil.toJsonFile(holder, JSON_NAME_PARAM_CAST);

        final Object readObject = DebugStringUtil.fromJsonFile(Holder.class, JSON_NAME_PARAM_CAST);
        assertTrue(readObject instanceof Holder);
        assertEquals((Holder)readObject, holder);
    }

    static final String JSON_NAME_PARAM_CAST = "json_param_cast.json";
}
