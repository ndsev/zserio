package enumeration_types.removed_enum_item;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import zserio.runtime.HashCodeUtil;
import zserio.runtime.io.BitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamReader;
import zserio.runtime.io.ByteArrayBitStreamWriter;
import zserio.runtime.typeinfo.ItemInfo;
import zserio.runtime.typeinfo.TypeInfo;
import zserio.runtime.ZserioError;

public class RemovedEnumItemTest
{
    @Test
    public void constructor()
    {
        final Traffic traffic = Traffic.ZSERIO_REMOVED_HEAVY;
        assertEquals(Traffic.ZSERIO_REMOVED_HEAVY, traffic);
    }

    @Test
    public void getValue()
    {
        final Traffic traffic = Traffic.ZSERIO_REMOVED_HEAVY;
        assertEquals(HEAVY_VALUE, traffic.getValue());
    }

    @Test
    public void getGenericValue()
    {
        final Traffic traffic = Traffic.ZSERIO_REMOVED_HEAVY;
        assertEquals(Short.valueOf(HEAVY_VALUE), traffic.getGenericValue());
    }

    @Test
    public void calcHashCode()
    {
        // use hardcoded values to check that the hash code is stable
        assertEquals(1703, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, Traffic.NONE));
        assertEquals(1704, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, Traffic.ZSERIO_REMOVED_HEAVY));
        assertEquals(1705, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, Traffic.LIGHT));
        assertEquals(1706, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, Traffic.MID));
    }

    @Test
    public void bitSizeOf()
    {
        final Traffic traffic = Traffic.ZSERIO_REMOVED_HEAVY;
        assertEquals(TRAFFIC_BIT_SIZE, traffic.bitSizeOf());
    }

    @Test
    public void read() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeBits(HEAVY_VALUE, 8);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        final Traffic traffic = Traffic.readEnum(reader);
        assertEquals(Traffic.ZSERIO_REMOVED_HEAVY, traffic);
    }

    @Test
    public void write() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertDoesNotThrow(() -> Traffic.NONE.write(writer));
        assertDoesNotThrow(() -> Traffic.LIGHT.write(writer));
        assertDoesNotThrow(() -> Traffic.MID.write(writer));

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        assertEquals(Traffic.NONE, Traffic.readEnum(reader));
        assertEquals(Traffic.LIGHT, Traffic.readEnum(reader));
        assertEquals(Traffic.MID, Traffic.readEnum(reader));

        final ZserioError thrown = assertThrows(ZserioError.class,
                () -> Traffic.ZSERIO_REMOVED_HEAVY.write(writer));
        assertEquals("Trying to write removed enumeration item 'ZSERIO_REMOVED_HEAVY'!",
                thrown.getMessage());
    }

    @Test
    public void valueToEnum()
    {
        assertEquals(Traffic.NONE, Traffic.toEnum(NONE_VALUE));
        assertEquals(Traffic.ZSERIO_REMOVED_HEAVY, Traffic.toEnum(HEAVY_VALUE));
        assertEquals(Traffic.LIGHT, Traffic.toEnum(LIGHT_VALUE));
        assertEquals(Traffic.MID, Traffic.toEnum(MID_VALUE));
    }

    @Test
    public void stringToEnum()
    {
        assertEquals(Traffic.NONE, Traffic.toEnum("NONE"));
        assertEquals(Traffic.ZSERIO_REMOVED_HEAVY, Traffic.toEnum("ZSERIO_REMOVED_HEAVY"));
        assertEquals(Traffic.LIGHT, Traffic.toEnum("LIGHT"));
        assertEquals(Traffic.MID, Traffic.toEnum("MID"));
    }

    @Test
    public void valueToEnumFailure()
    {
        assertThrows(IllegalArgumentException.class, () -> Traffic.toEnum((short)(MID_VALUE + 1)));
    }

    @Test
    public void stringToEnumFailure()
    {
        assertThrows(IllegalArgumentException.class, () -> Traffic.toEnum("NONEXISTING"));
    }

    @Test
    public void enumTypeInfo()
    {
        final TypeInfo typeInfo = Traffic.typeInfo();

        final List<ItemInfo> enumItems = typeInfo.getEnumItems();
        assertEquals(4, enumItems.size());

        assertEquals("NONE", enumItems.get(0).getSchemaName());
        assertEquals(BigInteger.valueOf(NONE_VALUE), enumItems.get(0).getValue());
        assertFalse(enumItems.get(0).isDeprecated());
        assertFalse(enumItems.get(0).isRemoved());

        assertEquals("HEAVY", enumItems.get(1).getSchemaName());
        assertEquals(BigInteger.valueOf(HEAVY_VALUE), enumItems.get(1).getValue());
        assertFalse(enumItems.get(1).isDeprecated());
        assertTrue(enumItems.get(1).isRemoved());

        assertEquals("LIGHT", enumItems.get(2).getSchemaName());
        assertEquals(BigInteger.valueOf(LIGHT_VALUE), enumItems.get(2).getValue());
        assertFalse(enumItems.get(2).isDeprecated());
        assertFalse(enumItems.get(2).isRemoved());

        assertEquals("MID", enumItems.get(3).getSchemaName());
        assertEquals(BigInteger.valueOf(MID_VALUE), enumItems.get(3).getValue());
        assertFalse(enumItems.get(3).isDeprecated());
        assertFalse(enumItems.get(3).isRemoved());
    }

    private static final short NONE_VALUE = (short)1;
    private static final short HEAVY_VALUE = (short)2;
    private static final short LIGHT_VALUE = (short)3;
    private static final short MID_VALUE = (short)4;

    private static final long TRAFFIC_BIT_SIZE = 8;
}
