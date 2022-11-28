package array_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import array_types.auto_array_bitfield_param.ParameterizedBitfieldLength;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

public class AutoArrayBitfieldParamTest
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

    @Test
    public void writeRead() throws IOException
    {
        final ParameterizedBitfieldLength parameterizedBitfieldLength = createParameterizedBitfieldLength();
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        parameterizedBitfieldLength.write(writer);

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(writer.toByteArray(),
                writer.getBitPosition());
        checkParameterizedBitfieldLengthInStream(reader, parameterizedBitfieldLength);
        reader.setBitPosition(0);
        final ParameterizedBitfieldLength readParameterizedBitfieldLength =
                new ParameterizedBitfieldLength(reader, NUM_BITS_PARAM);
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

    private void checkParameterizedBitfieldLengthInStream(ByteArrayBitStreamReader reader,
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
