package parameterized_types.subtyped_bitfield_param;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.SerializeUtil;

public class SubtypedBitfieldParamTest
{
    @Test
    public void writeRead() throws IOException
    {
        final SubtypedBitfieldParamHolder subtypedBitfieldParamHolder = createSubtypedBitfieldParamHolder();
        final BitBuffer bitBuffer = SerializeUtil.serialize(subtypedBitfieldParamHolder);
        checkSubtypedBitfieldParamHolderInBitBuffer(bitBuffer, subtypedBitfieldParamHolder);
        final SubtypedBitfieldParamHolder readSubtypedBitfieldParamHolder =
                SerializeUtil.deserialize(SubtypedBitfieldParamHolder.class, bitBuffer);
        assertEquals(subtypedBitfieldParamHolder, readSubtypedBitfieldParamHolder);
    }

    private SubtypedBitfieldParamHolder createSubtypedBitfieldParamHolder()
    {
        final SubtypedBitfieldParam subtypedBitfieldParam = new SubtypedBitfieldParam(
                SUBTYPED_BITFIELD_PARAM, SUBTYPED_BITFIELD_PARAM_VALUE, SUBTYPED_BITFIELD_PARAM_EXTRA_VALUE);

        return new SubtypedBitfieldParamHolder(subtypedBitfieldParam);
    }

    private void checkSubtypedBitfieldParamHolderInBitBuffer(
            BitBuffer bitBuffer, SubtypedBitfieldParamHolder subtypedBitfieldParamHolder) throws IOException
    {
        try (final BitStreamReader stream = new ByteArrayBitStreamReader(bitBuffer))
        {
            final SubtypedBitfieldParam subtypedBitfieldParam =
                    subtypedBitfieldParamHolder.getSubtypedBitfieldParam();
            assertEquals(subtypedBitfieldParam.getParam(), SUBTYPED_BITFIELD_PARAM);
            assertEquals(subtypedBitfieldParam.getValue(), stream.readUnsignedShort());
            assertEquals((long)subtypedBitfieldParam.getExtraValue(), stream.readUnsignedInt());
        }
    }

    static final byte SUBTYPED_BITFIELD_PARAM = 11;
    static final int SUBTYPED_BITFIELD_PARAM_VALUE = 0x0BED;
    static final long SUBTYPED_BITFIELD_PARAM_EXTRA_VALUE = 0x0BEDDEAD;
}
