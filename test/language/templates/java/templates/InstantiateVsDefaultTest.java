package templates;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

import templates.instantiate_vs_default.InstantiateVsDefault;
import templates.instantiate_vs_default.TStr;
import templates.instantiate_vs_default.pkg.Test_uint32;

public class InstantiateVsDefaultTest
{
    @Test
    public void readWrite() throws IOException
    {
        final InstantiateVsDefault instantiateVsDefault = new InstantiateVsDefault();
        instantiateVsDefault.setTest32(new Test_uint32(13));
        instantiateVsDefault.setTestStr(new TStr("test"));

        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        instantiateVsDefault.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final InstantiateVsDefault readInstantiateVsDefault = new InstantiateVsDefault(reader);
        assertTrue(instantiateVsDefault.equals(readInstantiateVsDefault));
    }
}
