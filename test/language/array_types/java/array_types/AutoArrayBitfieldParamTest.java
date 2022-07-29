package array_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.FileBitStreamReader;

import array_types.auto_array_bitfield_param.ParameterizedBitfieldLength;

public class AutoArrayBitfieldParamTest
{
    @Test
    public void fileWrite() throws IOException
    {
        final ParameterizedBitfieldLength parameterizedBitfieldLength = createParameterizedBitfieldLength();
        final File file = new File(BLOB_NAME);
        parameterizedBitfieldLength.write(file);
        final BitStreamReader reader = new FileBitStreamReader(file);
        checkParameterizedBitfieldLengthInStream(reader, parameterizedBitfieldLength);
        reader.close();
        final ParameterizedBitfieldLength readParameterizedBitfieldLength =
                new ParameterizedBitfieldLength(file, NUM_BITS_PARAM);
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

    private void checkParameterizedBitfieldLengthInStream(BitStreamReader reader,
            ParameterizedBitfieldLength parameterizedBitfieldLength) throws IOException
    {
        assertEquals(NUM_BITS_PARAM, parameterizedBitfieldLength.getNumBits());
        assertEquals(DYNAMIC_BITFIELD_ARRAY_SIZE, reader.readVarSize());
        for (short i = 0; i < DYNAMIC_BITFIELD_ARRAY_SIZE; ++i)
            assertEquals(i, reader.readBits(NUM_BITS_PARAM));
    }

    private static final String BLOB_NAME = "auto_array_bitfield_param.blob";
    private static final byte NUM_BITS_PARAM = 9;
    private static final int DYNAMIC_BITFIELD_ARRAY_SIZE = (1 << 9) - 1;
}
