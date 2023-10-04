package enumeration_types.multiple_removed_enum_items;

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

public class MultipleRemovedEnumItemsTest
{
    @Test
    public void constructor()
    {
        final Traffic trafficNone = Traffic.NONE;
        assertEquals(Traffic.NONE, trafficNone);

        final Traffic trafficHeavy = Traffic.ZSERIO_REMOVED_HEAVY;
        assertEquals(Traffic.ZSERIO_REMOVED_HEAVY, trafficHeavy);

        final Traffic trafficLight = Traffic.ZSERIO_REMOVED_LIGHT;
        assertEquals(Traffic.ZSERIO_REMOVED_LIGHT, trafficLight);

        final Traffic trafficMid = Traffic.ZSERIO_REMOVED_MID;
        assertEquals(Traffic.ZSERIO_REMOVED_MID, trafficMid);
    }

    @Test
    public void getValue()
    {
        assertEquals(NONE_VALUE, Traffic.NONE.getValue());
        assertEquals(HEAVY_VALUE, Traffic.ZSERIO_REMOVED_HEAVY.getValue());
        assertEquals(LIGHT_VALUE, Traffic.ZSERIO_REMOVED_LIGHT.getValue());
        assertEquals(MID_VALUE, Traffic.ZSERIO_REMOVED_MID.getValue());
    }

    @Test
    public void getGenericValue()
    {
        assertEquals(Short.valueOf(NONE_VALUE), Traffic.NONE.getGenericValue());
        assertEquals(Short.valueOf(HEAVY_VALUE), Traffic.ZSERIO_REMOVED_HEAVY.getGenericValue());
        assertEquals(Short.valueOf(LIGHT_VALUE), Traffic.ZSERIO_REMOVED_LIGHT.getGenericValue());
        assertEquals(Short.valueOf(MID_VALUE), Traffic.ZSERIO_REMOVED_MID.getGenericValue());
    }

    @Test
    public void calcHashCode()
    {
        // use hardcoded values to check that the hash code is stable
        assertEquals(1703, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, Traffic.NONE));
        assertEquals(1704, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, Traffic.ZSERIO_REMOVED_HEAVY));
        assertEquals(1705, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, Traffic.ZSERIO_REMOVED_LIGHT));
        assertEquals(1706, HashCodeUtil.calcHashCode(HashCodeUtil.HASH_SEED, Traffic.ZSERIO_REMOVED_MID));
    }

    @Test
    public void bitSizeOf()
    {
        assertEquals(TRAFFIC_BIT_SIZE, Traffic.NONE.bitSizeOf());
        assertEquals(TRAFFIC_BIT_SIZE, Traffic.ZSERIO_REMOVED_HEAVY.bitSizeOf());
        assertEquals(TRAFFIC_BIT_SIZE, Traffic.ZSERIO_REMOVED_LIGHT.bitSizeOf());
        assertEquals(TRAFFIC_BIT_SIZE, Traffic.ZSERIO_REMOVED_MID.bitSizeOf());
    }

    @Test
    public void read() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        writer.writeBits(NONE_VALUE, 8);
        writer.writeBits(HEAVY_VALUE, 8);
        writer.writeBits(LIGHT_VALUE, 8);
        writer.writeBits(MID_VALUE, 8);

        final BitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        assertEquals(Traffic.NONE, Traffic.readEnum(reader));
        assertEquals(Traffic.ZSERIO_REMOVED_HEAVY, Traffic.readEnum(reader));
        assertEquals(Traffic.ZSERIO_REMOVED_LIGHT, Traffic.readEnum(reader));
        assertEquals(Traffic.ZSERIO_REMOVED_MID, Traffic.readEnum(reader));
    }

    @Test
    public void write() throws IOException
    {
        final ByteArrayBitStreamWriter writer = new ByteArrayBitStreamWriter();
        assertDoesNotThrow(() -> Traffic.NONE.write(writer));

        final ByteArrayBitStreamReader reader = new ByteArrayBitStreamReader(
                writer.toByteArray(), writer.getBitPosition());
        assertEquals(Traffic.NONE, Traffic.readEnum(reader));

        assertThrows(ZserioError.class, () -> Traffic.ZSERIO_REMOVED_HEAVY.write(writer));
        assertThrows(ZserioError.class, () -> Traffic.ZSERIO_REMOVED_LIGHT.write(writer));
        assertThrows(ZserioError.class, () -> Traffic.ZSERIO_REMOVED_MID.write(writer));
    }

    @Test
    public void valueToEnum()
    {
        assertEquals(Traffic.NONE, Traffic.toEnum(NONE_VALUE));
        assertEquals(Traffic.ZSERIO_REMOVED_HEAVY, Traffic.toEnum(HEAVY_VALUE));
        assertEquals(Traffic.ZSERIO_REMOVED_LIGHT, Traffic.toEnum(LIGHT_VALUE));
        assertEquals(Traffic.ZSERIO_REMOVED_MID, Traffic.toEnum(MID_VALUE));
    }

    @Test
    public void stringToEnum()
    {
        assertEquals(Traffic.NONE, Traffic.toEnum("NONE"));
        assertEquals(Traffic.ZSERIO_REMOVED_HEAVY, Traffic.toEnum("ZSERIO_REMOVED_HEAVY"));
        assertEquals(Traffic.ZSERIO_REMOVED_LIGHT, Traffic.toEnum("ZSERIO_REMOVED_LIGHT"));
        assertEquals(Traffic.ZSERIO_REMOVED_MID, Traffic.toEnum("ZSERIO_REMOVED_MID"));
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
        assertTrue(enumItems.get(2).isRemoved());

        assertEquals("MID", enumItems.get(3).getSchemaName());
        assertEquals(BigInteger.valueOf(MID_VALUE), enumItems.get(3).getValue());
        assertFalse(enumItems.get(3).isDeprecated());
        assertTrue(enumItems.get(3).isRemoved());
    }

    private static final short NONE_VALUE = (short)1;
    private static final short HEAVY_VALUE = (short)2;
    private static final short LIGHT_VALUE = (short)3;
    private static final short MID_VALUE = (short)4;

    private static final long TRAFFIC_BIT_SIZE = 8;
}
