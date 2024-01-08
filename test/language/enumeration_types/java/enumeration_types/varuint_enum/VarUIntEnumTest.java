package enumeration_types.varuint_enum;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;

import zserio.runtime.HashCodeUtil;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;

public class VarUIntEnumTest
{
    @Test
    public void constructor()
    {
        final DarkColor darkColor = DarkColor.DARK_RED;
        assertEquals(DarkColor.DARK_RED, darkColor);
    }

    @Test
    public void getValue()
    {
        final DarkColor darkColor = DarkColor.DARK_BLUE;
        assertEquals(DARK_BLUE_VALUE, darkColor.getValue());
    }

    @Test
    public void getGenericValue()
    {
        final DarkColor darkColor = DarkColor.DARK_GREEN;
        assertEquals(DARK_GREEN_VALUE, darkColor.getGenericValue());
    }

    @Test
    public void calcHashCode()
    {
        // use hardcoded values to check that the hash code is stable
        assertEquals(1702, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, DarkColor.NONE));
        assertEquals(1703, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, DarkColor.DARK_RED));
        assertEquals(1704, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, DarkColor.DARK_BLUE));
        assertEquals(1957, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, DarkColor.DARK_GREEN));
    }

    @Test
    public void bitSizeOf()
    {
        final DarkColor darkColor1 = DarkColor.NONE;
        assertEquals(DARK_COLOR_NONE_BITSIZEOF, darkColor1.bitSizeOf());

        final DarkColor darkColor2 = DarkColor.DARK_RED;
        assertEquals(DARK_COLOR_DARK_RED_BITSIZEOF, darkColor2.bitSizeOf());

        final DarkColor darkColor3 = DarkColor.DARK_BLUE;
        assertEquals(DARK_COLOR_DARK_BLUE_BITSIZEOF, darkColor3.bitSizeOf());

        final DarkColor darkColor4 = DarkColor.DARK_GREEN;
        assertEquals(DARK_COLOR_DARK_GREEN_BITSIZEOF, darkColor4.bitSizeOf());
    }

    @Test
    public void writeRead() throws IOException
    {
        final DarkColor darkColor = DarkColor.DARK_GREEN;
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        darkColor.write(writer);

        final BitStreamReader reader =
                new ByteArrayBitStreamReader(writer.toByteArray(), writer.getBitPosition());
        final BigInteger readColor = reader.readVarUInt();
        assertEquals(readColor, darkColor.getValue());
    }

    @Test
    public void valueToEnum()
    {
        DarkColor darkColor = DarkColor.toEnum(NONE_VALUE);
        assertEquals(DarkColor.NONE, darkColor);

        darkColor = DarkColor.toEnum(DARK_RED_VALUE);
        assertEquals(DarkColor.DARK_RED, darkColor);

        darkColor = DarkColor.toEnum(DARK_BLUE_VALUE);
        assertEquals(DarkColor.DARK_BLUE, darkColor);

        darkColor = DarkColor.toEnum(DARK_GREEN_VALUE);
        assertEquals(DarkColor.DARK_GREEN, darkColor);
    }

    @Test
    public void stringToEnum()
    {
        DarkColor darkColor = DarkColor.toEnum("NONE");
        assertEquals(DarkColor.NONE, darkColor);

        darkColor = DarkColor.toEnum("DARK_RED");
        assertEquals(DarkColor.DARK_RED, darkColor);

        darkColor = DarkColor.toEnum("DARK_BLUE");
        assertEquals(DarkColor.DARK_BLUE, darkColor);

        darkColor = DarkColor.toEnum("DARK_GREEN");
        assertEquals(DarkColor.DARK_GREEN, darkColor);
    }

    @Test
    public void valueToEnumFailure()
    {
        assertThrows(IllegalArgumentException.class, () -> DarkColor.toEnum(new BigInteger("3")));
    }

    @Test
    public void stringToEnumFailure()
    {
        assertThrows(IllegalArgumentException.class, () -> DarkColor.toEnum("NONEXISTING"));
    }

    private static int DARK_COLOR_NONE_BITSIZEOF = 8;
    private static int DARK_COLOR_DARK_RED_BITSIZEOF = 8;
    private static int DARK_COLOR_DARK_BLUE_BITSIZEOF = 8;
    private static int DARK_COLOR_DARK_GREEN_BITSIZEOF = 16;

    private static BigInteger NONE_VALUE = BigInteger.valueOf(0);
    private static BigInteger DARK_RED_VALUE = BigInteger.valueOf(1);
    private static BigInteger DARK_BLUE_VALUE = BigInteger.valueOf(2);
    private static BigInteger DARK_GREEN_VALUE = BigInteger.valueOf(255);
}
