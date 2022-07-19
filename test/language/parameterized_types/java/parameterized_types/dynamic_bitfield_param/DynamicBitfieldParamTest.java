package parameterized_types.dynamic_bitfield_param;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.FileBitStreamReader;

public class DynamicBitfieldParamTest
{
    @Test
    public void fileWrite() throws IOException
    {
        final DynamicBitfieldParamHolder dynamicBitfieldParamHolder = createDynamicBitfieldParamHolder();
        final File file = new File("test1.bin");
        dynamicBitfieldParamHolder.write(file);
        checkDynamicBitfieldParamHolderInFile(file, dynamicBitfieldParamHolder);
        final DynamicBitfieldParamHolder readDynamicBitfieldParamHolder =
                new DynamicBitfieldParamHolder(file);
        assertEquals(dynamicBitfieldParamHolder, readDynamicBitfieldParamHolder);
    }

    private DynamicBitfieldParamHolder createDynamicBitfieldParamHolder()
    {
        final DynamicBitfieldParam dynamicBitfieldParam = new DynamicBitfieldParam(BITFIELD,
                DYNAMIC_BITFIELD_PARAM_VALUE, DYNAMIC_BITFIELD_EXTRA_VALUE);

        return new DynamicBitfieldParamHolder(LENGTH, BITFIELD, dynamicBitfieldParam);
    }

    private void checkDynamicBitfieldParamHolderInFile(File file, DynamicBitfieldParamHolder
            dynamicBitfieldParamHolder) throws IOException
    {
        final BitStreamReader stream = new FileBitStreamReader(file);

        assertEquals(dynamicBitfieldParamHolder.getLength(), stream.readBits(4));
        assertEquals(dynamicBitfieldParamHolder.getBitfield(), stream.readSignedBits(LENGTH));

        final DynamicBitfieldParam dynamicBitfieldParam = dynamicBitfieldParamHolder.getDynamicBitfieldParam();
        assertEquals(dynamicBitfieldParam.getParam(), BITFIELD);
        assertEquals(dynamicBitfieldParam.getValue(), stream.readUnsignedShort());
        assertEquals(dynamicBitfieldParam.getExtraValue(), stream.readUnsignedInt());

        stream.close();
    }

    static final byte LENGTH = 5;
    static final short BITFIELD = 11;
    static final int DYNAMIC_BITFIELD_PARAM_VALUE = 0x0BED;
    static final long DYNAMIC_BITFIELD_EXTRA_VALUE = 0x0BEDDEAD;
}
