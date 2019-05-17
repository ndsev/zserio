package parameterized_types.subtyped_bitfield_param;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.FileBitStreamReader;

public class SubtypedBitfieldParamTest
{
    @Test
    public void fileWrite() throws IOException
    {
        final SubtypedBitfieldParamHolder subtypedBitfieldParamHolder = createSubtypedBitfieldParamHolder();
        final File file = new File("test1.bin");
        subtypedBitfieldParamHolder.write(file);
        checkSubtypedBitfieldParamHolderInFile(file, subtypedBitfieldParamHolder);
        final SubtypedBitfieldParamHolder readSubtypedBitfieldParamHolder =
                new SubtypedBitfieldParamHolder(file);
        assertEquals(subtypedBitfieldParamHolder, readSubtypedBitfieldParamHolder);
    }

    private SubtypedBitfieldParamHolder createSubtypedBitfieldParamHolder()
    {
        final SubtypedBitfieldParam subtypedBitfieldParam = new SubtypedBitfieldParam(SUBTYPED_BITFIELD_PARAM,
                SUBTYPED_BITFIELD_PARAM_VALUE, SUBTYPED_BITFIELD_PARAM_EXTRA_VALUE);

        return new SubtypedBitfieldParamHolder(subtypedBitfieldParam);
    }

    private void checkSubtypedBitfieldParamHolderInFile(File file, SubtypedBitfieldParamHolder
            subtypedBitfieldParamHolder) throws IOException
    {
        final BitStreamReader stream = new FileBitStreamReader(file);

        final SubtypedBitfieldParam subtypedBitfieldParam = 
                subtypedBitfieldParamHolder.getSubtypedBitfieldParam();
        assertEquals(subtypedBitfieldParam.getParam(), SUBTYPED_BITFIELD_PARAM);
        assertEquals(subtypedBitfieldParam.getValue(), stream.readUnsignedShort());
        assertEquals((long)subtypedBitfieldParam.getExtraValue(), stream.readUnsignedInt());

        stream.close();
    }

    static final byte SUBTYPED_BITFIELD_PARAM = 11;
    static final int SUBTYPED_BITFIELD_PARAM_VALUE = 0x0BED;
    static final long SUBTYPED_BITFIELD_PARAM_EXTRA_VALUE = 0x0BEDDEAD;
}
