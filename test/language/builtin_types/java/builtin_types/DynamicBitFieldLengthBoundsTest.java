package builtin_types;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import zserio.runtime.ZserioError;
import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.io.SerializeUtil;

import builtin_types.dynamic_bitfield_length_bounds.Container;

public class DynamicBitFieldLengthBoundsTest
{
    @Test
    public void writeRead() throws IOException
    {
        final Container container = new Container(UNSIGNED_BIT_LENGTH, UNSIGNED_VALUE, UNSIGNED_BIG_BIT_LENGTH,
                UNSIGNED_BIG_VALUE, SIGNED_BIT_LENGTH, SIGNED_VALUE);

        final BitBuffer bitBuffer = SerializeUtil.serialize(container);
        final Container readContainer = SerializeUtil.deserialize(Container.class, bitBuffer);

        assertEquals(container, readContainer);
    }

    @Test
    public void unsignedBitLengthZero() throws IOException
    {
        final Container container = new Container((byte)0, UNSIGNED_VALUE, UNSIGNED_BIG_BIT_LENGTH,
                UNSIGNED_BIG_VALUE, SIGNED_BIT_LENGTH, SIGNED_VALUE);

        assertThrows(IllegalArgumentException.class, () -> SerializeUtil.serialize(container));

        final byte[] bytes = writeContainer(container);
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bytes);
        assertThrows(IllegalArgumentException.class, () -> new Container(reader));
    }

    @Test
    public void unsignedBitLengthZeroValueZero() throws IOException
    {
        final Container container = new Container((byte)0, (short)0, UNSIGNED_BIG_BIT_LENGTH,
                UNSIGNED_BIG_VALUE, SIGNED_BIT_LENGTH, SIGNED_VALUE);

        assertThrows(IllegalArgumentException.class, () -> SerializeUtil.serialize(container));

        final byte[] bytes = writeContainer(container);
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bytes);
        assertThrows(IllegalArgumentException.class, () -> new Container(reader));
    }

    @Test
    public void unsignedBigBitLengthZero() throws IOException
    {
        final Container container = new Container(UNSIGNED_BIT_LENGTH, UNSIGNED_VALUE, (short)0,
                UNSIGNED_BIG_VALUE, SIGNED_BIT_LENGTH, SIGNED_VALUE);

        assertThrows(IllegalArgumentException.class, () -> SerializeUtil.serialize(container));

        final byte[] bytes = writeContainer(container);
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bytes);
        assertThrows(IllegalArgumentException.class, () -> new Container(reader));
    }

    @Test
    public void unsignedBigBitLengthZeroValueZero() throws IOException
    {
        final Container container = new Container(UNSIGNED_BIT_LENGTH, UNSIGNED_VALUE, (short)0,
                BigInteger.ZERO, SIGNED_BIT_LENGTH, SIGNED_VALUE);

        assertThrows(IllegalArgumentException.class, () -> SerializeUtil.serialize(container));

        final byte[] bytes = writeContainer(container);
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bytes);
        assertThrows(IllegalArgumentException.class, () -> new Container(reader));
    }

    @Test
    public void unsignedBigBitLengthOverMax() throws IOException
    {
        final Container container = new Container(UNSIGNED_BIT_LENGTH, UNSIGNED_VALUE, (short)65,
                UNSIGNED_BIG_VALUE, SIGNED_BIT_LENGTH, SIGNED_VALUE);

        assertThrows(IllegalArgumentException.class, () -> SerializeUtil.serialize(container));

        final byte[] bytes = writeContainer(container);
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bytes);
        assertThrows(IllegalArgumentException.class, () -> new Container(reader));
    }

    @Test
    public void signedBitLengthZero() throws IOException
    {
        final Container container = new Container(UNSIGNED_BIT_LENGTH, UNSIGNED_VALUE, UNSIGNED_BIG_BIT_LENGTH,
                UNSIGNED_BIG_VALUE, BigInteger.valueOf(0), SIGNED_VALUE);

        assertThrows(IllegalArgumentException.class, () -> SerializeUtil.serialize(container));

        final byte[] bytes = writeContainer(container);
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bytes);
        assertThrows(IllegalArgumentException.class, () -> new Container(reader));
    }

    @Test
    public void signedBitLengthZeroValueZero() throws IOException
    {
        final Container container = new Container(UNSIGNED_BIT_LENGTH, UNSIGNED_VALUE, UNSIGNED_BIG_BIT_LENGTH,
                UNSIGNED_BIG_VALUE, BigInteger.valueOf(0), (long)0);

        assertThrows(IllegalArgumentException.class, () -> SerializeUtil.serialize(container));

        final byte[] bytes = writeContainer(container);
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bytes);
        assertThrows(IllegalArgumentException.class, () -> new Container(reader));
    }

    @Test
    public void signedBitLengthOverMax() throws IOException
    {
        final Container container = new Container(UNSIGNED_BIT_LENGTH, UNSIGNED_VALUE, UNSIGNED_BIG_BIT_LENGTH,
                UNSIGNED_BIG_VALUE, BigInteger.valueOf(65), SIGNED_VALUE);

        assertThrows(IllegalArgumentException.class, () -> SerializeUtil.serialize(container));

        final byte[] bytes = writeContainer(container);
        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(bytes);
        assertThrows(IllegalArgumentException.class, () -> new Container(reader));
    }

    byte[] writeContainer(Container container) throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();

        writer.writeBits(container.getUnsignedBitLength(), 4);
        if (container.getUnsignedBitLength() < 1)
            return writer.toByteArray();
        writer.writeBits(container.getUnsignedValue(), container.getUnsignedBitLength());
        writer.writeBits(container.getUnsignedBigBitLength(), 8);
        if (container.getUnsignedBigBitLength() < 1 || container.getUnsignedBigBitLength() > 64)
            return writer.toByteArray();
        writer.writeBigInteger(container.getUnsignedBigValue(), container.getUnsignedBigBitLength());
        writer.writeBigInteger(container.getSignedBitLength(), 64);
        if (container.getSignedBitLength().compareTo(BigInteger.ONE) == -1 ||
                container.getSignedBitLength().compareTo(BigInteger.valueOf(64)) == 1)
            return writer.toByteArray();
        writer.writeBits(container.getSignedValue(), container.getSignedBitLength().intValue());

        return writer.toByteArray();
    }

    private static final byte UNSIGNED_BIT_LENGTH = 15;
    private static final short UNSIGNED_VALUE = (1 << 15) - 1;
    private static final short UNSIGNED_BIG_BIT_LENGTH = 13;
    private static final BigInteger UNSIGNED_BIG_VALUE =
            BigInteger.ONE.shiftLeft(UNSIGNED_BIG_BIT_LENGTH).subtract(BigInteger.ONE);
    private static final BigInteger SIGNED_BIT_LENGTH = BigInteger.valueOf(7);
    private static final long SIGNED_VALUE = -(1L << (SIGNED_BIT_LENGTH.intValue() - 1));
}
