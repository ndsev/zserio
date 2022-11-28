package array_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import zserio.runtime.io.SerializeUtil;
import array_types.packed_auto_array_bitfield_param.ParameterizedBitfieldLength;

public class PackedAutoArrayBitfieldParamTest
{
    @Test
    public void writeReadFile() throws IOException
    {
        final ParameterizedBitfieldLength parameterizedBitfieldLength = createParameterizedBitfieldLength();
        SerializeUtil.serializeToFile(parameterizedBitfieldLength, BLOB_NAME);
        final ParameterizedBitfieldLength readParameterizedBitfieldLength =
                SerializeUtil.deserializeFromFile(ParameterizedBitfieldLength.class, BLOB_NAME, NUM_BITS_PARAM);
        assertEquals(parameterizedBitfieldLength, readParameterizedBitfieldLength);
    }

    private ParameterizedBitfieldLength createParameterizedBitfieldLength()
    {
        final ParameterizedBitfieldLength parameterizedBitfieldLength =
                new ParameterizedBitfieldLength(NUM_BITS_PARAM);
        final short[] dynamicBitfieldArray = new short[DYNAMIC_BITFIELD_ARRAY_SIZE];
        for (short i = 0; i < DYNAMIC_BITFIELD_ARRAY_SIZE; ++i)
            dynamicBitfieldArray[i] = i;
        parameterizedBitfieldLength.setDynamicBitfieldArray(dynamicBitfieldArray);

        return parameterizedBitfieldLength;
    }

    private static final String BLOB_NAME = "packed_auto_array_bitfield_param.blob";
    private static final byte NUM_BITS_PARAM = 9;
    private static final int DYNAMIC_BITFIELD_ARRAY_SIZE = (1 << 9) - 1;
}
