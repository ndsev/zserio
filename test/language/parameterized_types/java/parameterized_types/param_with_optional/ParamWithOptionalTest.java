package parameterized_types.param_with_optional;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;
import java.math.BigInteger;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

import parameterized_types.param_with_optional.Holder;

public class ParamWithOptionalTest
{
    @Test
    public void bitSizeOf() throws IOException, ZserioError
    {
        final Holder holder = createHolder();
        final int bitPosition = 2;
        assertEquals(HOLDER_BIT_SIZE, holder.bitSizeOf(bitPosition));
    }

    @Test
    public void initializeOffsets() throws IOException, ZserioError
    {
        final Holder holder = createHolder();
        final int bitPosition = 2;
        assertEquals(bitPosition + HOLDER_BIT_SIZE, holder.initializeOffsets(bitPosition));
    }

    @Test
    public void read() throws IOException, ZserioError
    {
        final BitBuffer buffer = writeHolderToBitBuffer();
        final Holder holder = SerializeUtil.deserialize(Holder.class, buffer);
        checkHolder(holder);
    }

    @Test
    public void writeRead() throws IOException, ZserioError
    {
        final Holder holder = createHolder();
        final File file = new File(BLOB_NAME);
        SerializeUtil.serializeToFile(holder, file);

        final Holder readHolder = SerializeUtil.deserializeFromFile(Holder.class, file);
        checkHolder(readHolder);
    }

    private Holder createHolder()
    {
        final Param param = new Param(HAS_EXTRA, EXTRA_PARAM);
        final Value value = new Value(param, null, new ExtraValue(param.getExtraParam(), EXTRA_VALUE));

        return new Holder(param, value);
    }

    private BitBuffer writeHolderToBitBuffer() throws IOException
    {
        try (final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter())
        {
            writer.writeBool(HAS_EXTRA);
            writer.writeBits(EXTRA_PARAM, 7);
            writer.writeBigInteger(EXTRA_VALUE, 64);

            return new BitBuffer(writer.toByteArray(), writer.getBitPosition());
        }
    }

    private void checkHolder(Holder holder)
    {
        assertEquals(HAS_EXTRA, holder.getParam().getHasExtra());
        assertEquals(EXTRA_PARAM, holder.getParam().getExtraParam());
        assertEquals(EXTRA_VALUE, holder.getValue().getExtraValue().getValue());
    }

    private static final String BLOB_NAME = "param_with_optional.blob";

    private static final boolean HAS_EXTRA = true;
    private static final byte EXTRA_PARAM = (byte)0x00;
    private static final BigInteger EXTRA_VALUE = BigInteger.valueOf(0xDEAD);
    private static final int HOLDER_BIT_SIZE = 1 + 7 + 64;
}
