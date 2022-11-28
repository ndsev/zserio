package parameterized_types.dynamic_bitfield_param;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

public class DynamicBitfieldParamTest
{
    @Test
    public void writeRead() throws IOException
    {
        final DynamicBitfieldParamHolder dynamicBitfieldParamHolder = createDynamicBitfieldParamHolder();
        final BitBuffer bitBuffer = SerializeUtil.serialize(dynamicBitfieldParamHolder);
        checkDynamicBitfieldParamHolderInBitBuffer(bitBuffer, dynamicBitfieldParamHolder);
        final DynamicBitfieldParamHolder readDynamicBitfieldParamHolder =
                SerializeUtil.deserialize(DynamicBitfieldParamHolder.class, bitBuffer);
        assertEquals(dynamicBitfieldParamHolder, readDynamicBitfieldParamHolder);
    }

    private DynamicBitfieldParamHolder createDynamicBitfieldParamHolder()
    {
        final DynamicBitfieldParam dynamicBitfieldParam = new DynamicBitfieldParam(BITFIELD,
                DYNAMIC_BITFIELD_PARAM_VALUE, DYNAMIC_BITFIELD_EXTRA_VALUE);

        return new DynamicBitfieldParamHolder(LENGTH, BITFIELD, dynamicBitfieldParam);
    }

    private void checkDynamicBitfieldParamHolderInBitBuffer(BitBuffer bitBuffer,
            DynamicBitfieldParamHolder dynamicBitfieldParamHolder) throws IOException
    {
        try (final BitStreamReader reader = new ByteArrayBitStreamReader(bitBuffer))
        {
            assertEquals(dynamicBitfieldParamHolder.getLength(), reader.readBits(4));
            assertEquals(dynamicBitfieldParamHolder.getBitfield(), reader.readSignedBits(LENGTH));

            final DynamicBitfieldParam dynamicBitfieldParam = dynamicBitfieldParamHolder.getDynamicBitfieldParam();
            assertEquals(dynamicBitfieldParam.getParam(), BITFIELD);
            assertEquals(dynamicBitfieldParam.getValue(), reader.readBits(16));
            assertEquals(dynamicBitfieldParam.getExtraValue(), reader.readBits(32));
        }
    }

    static final byte LENGTH = 5;
    static final short BITFIELD = 11;
    static final int DYNAMIC_BITFIELD_PARAM_VALUE = 0x0BED;
    static final long DYNAMIC_BITFIELD_EXTRA_VALUE = 0x0BEDDEAD;
}
