package builtin_types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import java.math.BigInteger;

import builtin_types.bitfield_uint64_length.Container;
import zserio.runtime.io.SerializeUtil;

public class BitFieldUInt64LengthTest
{
    @Test
    public void bitSizeOf()
    {
        final Container container = new Container();
        final BigInteger bitFieldLength = BigInteger.valueOf(33);
        container.setLength(bitFieldLength);
        container.setUnsignedBitField(UNSIGNED_BIT_FIELD_VALUE);
        container.setSignedBitField(SIGNED_BIT_FIELD_VALUE);

        final int expectedBitSizeOfContainer = 64 + 33 + 33;
        assertEquals(expectedBitSizeOfContainer, container.bitSizeOf());
    }

    @Test
    public void readWrite() throws IOException
    {
        final Container container = new Container();
        final BigInteger bitFieldLength = BigInteger.valueOf(33);
        container.setLength(bitFieldLength);
        container.setUnsignedBitField(UNSIGNED_BIT_FIELD_VALUE);
        container.setSignedBitField(SIGNED_BIT_FIELD_VALUE);
        SerializeUtil.serializeToFile(container, TEST_FILE);
        final Container readContainer = SerializeUtil.deserializeFromFile(Container.class, TEST_FILE);
        assertEquals(container, readContainer);
    }

    private static final File TEST_FILE = new File("bit_field_uint64_length.blob");

    private static final BigInteger UNSIGNED_BIT_FIELD_VALUE = BigInteger.valueOf(0xFFFFFFFFL + 1L);
    private static final long SIGNED_BIT_FIELD_VALUE = Integer.MAX_VALUE + (long)1;
}
