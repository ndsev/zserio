package zserio.runtime.typeinfo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TypeInfoUtilTest
{
    @Test
    public void isCompound()
    {
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.BOOL));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.INT8));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.INT16));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.INT32));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.INT64));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.UINT8));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.UINT16));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.UINT32));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.UINT64));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.VARINT16));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.VARINT32));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.VARINT64));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.VARINT));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.VARUINT16));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.VARUINT32));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.VARUINT64));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.VARUINT));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.VARSIZE));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.FIXED_SIGNED_BITFIELD));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.FIXED_UNSIGNED_BITFIELD));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.DYNAMIC_SIGNED_BITFIELD));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.DYNAMIC_UNSIGNED_BITFIELD));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.FLOAT16));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.FLOAT32));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.FLOAT64));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.STRING));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.EXTERN));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.ENUM));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.BITMASK));
        assertEquals(true, TypeInfoUtil.isCompound(SchemaType.STRUCT));
        assertEquals(true, TypeInfoUtil.isCompound(SchemaType.CHOICE));
        assertEquals(true, TypeInfoUtil.isCompound(SchemaType.UNION));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.SQL_TABLE));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.SQL_DATABASE));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.SERVICE));
        assertEquals(false, TypeInfoUtil.isCompound(SchemaType.PUBSUB));
    }

    @Test
    public void hasChoice()
    {
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.BOOL));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.INT8));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.INT16));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.INT32));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.INT64));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.UINT8));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.UINT16));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.UINT32));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.UINT64));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.VARINT16));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.VARINT32));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.VARINT64));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.VARINT));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.VARUINT16));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.VARUINT32));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.VARUINT64));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.VARUINT));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.VARSIZE));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.FIXED_SIGNED_BITFIELD));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.FIXED_UNSIGNED_BITFIELD));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.DYNAMIC_SIGNED_BITFIELD));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.DYNAMIC_UNSIGNED_BITFIELD));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.FLOAT16));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.FLOAT32));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.FLOAT64));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.STRING));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.EXTERN));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.ENUM));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.BITMASK));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.STRUCT));
        assertEquals(true, TypeInfoUtil.hasChoice(SchemaType.CHOICE));
        assertEquals(true, TypeInfoUtil.hasChoice(SchemaType.UNION));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.SQL_TABLE));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.SQL_DATABASE));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.SERVICE));
        assertEquals(false, TypeInfoUtil.hasChoice(SchemaType.PUBSUB));
    }

    @Test
    public void isFixedSize()
    {
        assertEquals(true, TypeInfoUtil.isFixedSize(SchemaType.BOOL));
        assertEquals(true, TypeInfoUtil.isFixedSize(SchemaType.INT8));
        assertEquals(true, TypeInfoUtil.isFixedSize(SchemaType.INT16));
        assertEquals(true, TypeInfoUtil.isFixedSize(SchemaType.INT32));
        assertEquals(true, TypeInfoUtil.isFixedSize(SchemaType.INT64));
        assertEquals(true, TypeInfoUtil.isFixedSize(SchemaType.UINT8));
        assertEquals(true, TypeInfoUtil.isFixedSize(SchemaType.UINT16));
        assertEquals(true, TypeInfoUtil.isFixedSize(SchemaType.UINT32));
        assertEquals(true, TypeInfoUtil.isFixedSize(SchemaType.UINT64));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.VARINT16));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.VARINT32));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.VARINT64));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.VARINT));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.VARUINT16));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.VARUINT32));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.VARUINT64));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.VARUINT));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.VARSIZE));
        assertEquals(true, TypeInfoUtil.isFixedSize(SchemaType.FIXED_SIGNED_BITFIELD));
        assertEquals(true, TypeInfoUtil.isFixedSize(SchemaType.FIXED_UNSIGNED_BITFIELD));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.DYNAMIC_SIGNED_BITFIELD));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.DYNAMIC_UNSIGNED_BITFIELD));
        assertEquals(true, TypeInfoUtil.isFixedSize(SchemaType.FLOAT16));
        assertEquals(true, TypeInfoUtil.isFixedSize(SchemaType.FLOAT32));
        assertEquals(true, TypeInfoUtil.isFixedSize(SchemaType.FLOAT64));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.STRING));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.EXTERN));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.ENUM));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.BITMASK));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.STRUCT));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.CHOICE));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.UNION));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.SQL_TABLE));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.SQL_DATABASE));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.SERVICE));
        assertEquals(false, TypeInfoUtil.isFixedSize(SchemaType.PUBSUB));
    }

    @Test
    public void isIntegral()
    {
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.BOOL));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.INT8));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.INT16));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.INT32));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.INT64));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.UINT8));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.UINT16));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.UINT32));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.UINT64));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.VARINT16));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.VARINT32));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.VARINT64));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.VARINT));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.VARUINT16));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.VARUINT32));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.VARUINT64));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.VARUINT));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.VARSIZE));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.FIXED_SIGNED_BITFIELD));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.FIXED_UNSIGNED_BITFIELD));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.DYNAMIC_SIGNED_BITFIELD));
        assertEquals(true, TypeInfoUtil.isIntegral(SchemaType.DYNAMIC_UNSIGNED_BITFIELD));
        assertEquals(false, TypeInfoUtil.isIntegral(SchemaType.FLOAT16));
        assertEquals(false, TypeInfoUtil.isIntegral(SchemaType.FLOAT32));
        assertEquals(false, TypeInfoUtil.isIntegral(SchemaType.FLOAT64));
        assertEquals(false, TypeInfoUtil.isIntegral(SchemaType.STRING));
        assertEquals(false, TypeInfoUtil.isIntegral(SchemaType.EXTERN));
        assertEquals(false, TypeInfoUtil.isIntegral(SchemaType.ENUM));
        assertEquals(false, TypeInfoUtil.isIntegral(SchemaType.BITMASK));
        assertEquals(false, TypeInfoUtil.isIntegral(SchemaType.STRUCT));
        assertEquals(false, TypeInfoUtil.isIntegral(SchemaType.CHOICE));
        assertEquals(false, TypeInfoUtil.isIntegral(SchemaType.UNION));
        assertEquals(false, TypeInfoUtil.isIntegral(SchemaType.SQL_TABLE));
        assertEquals(false, TypeInfoUtil.isIntegral(SchemaType.SQL_DATABASE));
        assertEquals(false, TypeInfoUtil.isIntegral(SchemaType.SERVICE));
        assertEquals(false, TypeInfoUtil.isIntegral(SchemaType.PUBSUB));
    }

    @Test
    public void isSigned()
    {
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.BOOL));
        assertEquals(true, TypeInfoUtil.isSigned(SchemaType.INT8));
        assertEquals(true, TypeInfoUtil.isSigned(SchemaType.INT16));
        assertEquals(true, TypeInfoUtil.isSigned(SchemaType.INT32));
        assertEquals(true, TypeInfoUtil.isSigned(SchemaType.INT64));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.UINT8));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.UINT16));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.UINT32));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.UINT64));
        assertEquals(true, TypeInfoUtil.isSigned(SchemaType.VARINT16));
        assertEquals(true, TypeInfoUtil.isSigned(SchemaType.VARINT32));
        assertEquals(true, TypeInfoUtil.isSigned(SchemaType.VARINT64));
        assertEquals(true, TypeInfoUtil.isSigned(SchemaType.VARINT));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.VARUINT16));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.VARUINT32));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.VARUINT64));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.VARUINT));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.VARSIZE));
        assertEquals(true, TypeInfoUtil.isSigned(SchemaType.FIXED_SIGNED_BITFIELD));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.FIXED_UNSIGNED_BITFIELD));
        assertEquals(true, TypeInfoUtil.isSigned(SchemaType.DYNAMIC_SIGNED_BITFIELD));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.DYNAMIC_UNSIGNED_BITFIELD));
        assertEquals(true, TypeInfoUtil.isSigned(SchemaType.FLOAT16));
        assertEquals(true, TypeInfoUtil.isSigned(SchemaType.FLOAT32));
        assertEquals(true, TypeInfoUtil.isSigned(SchemaType.FLOAT64));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.STRING));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.EXTERN));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.ENUM));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.BITMASK));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.STRUCT));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.CHOICE));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.UNION));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.SQL_TABLE));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.SQL_DATABASE));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.SERVICE));
        assertEquals(false, TypeInfoUtil.isSigned(SchemaType.PUBSUB));
    }

    @Test
    public void isFloatingPoint()
    {
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.BOOL));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.INT8));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.INT16));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.INT32));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.INT64));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.UINT8));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.UINT16));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.UINT32));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.UINT64));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.VARINT16));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.VARINT32));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.VARINT64));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.VARINT));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.VARUINT16));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.VARUINT32));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.VARUINT64));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.VARUINT));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.VARSIZE));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.FIXED_SIGNED_BITFIELD));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.FIXED_UNSIGNED_BITFIELD));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.DYNAMIC_SIGNED_BITFIELD));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.DYNAMIC_UNSIGNED_BITFIELD));
        assertEquals(true, TypeInfoUtil.isFloatingPoint(SchemaType.FLOAT16));
        assertEquals(true, TypeInfoUtil.isFloatingPoint(SchemaType.FLOAT32));
        assertEquals(true, TypeInfoUtil.isFloatingPoint(SchemaType.FLOAT64));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.STRING));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.EXTERN));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.ENUM));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.BITMASK));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.STRUCT));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.CHOICE));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.UNION));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.SQL_TABLE));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.SQL_DATABASE));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.SERVICE));
        assertEquals(false, TypeInfoUtil.isFloatingPoint(SchemaType.PUBSUB));
    }
};
