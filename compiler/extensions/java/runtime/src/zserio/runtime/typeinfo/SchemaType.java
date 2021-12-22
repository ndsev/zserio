package zserio.runtime.typeinfo;

/**
 * Enumeration which specifies zserio type used in type information.
 */
public enum SchemaType
{
    /** Zserio bool type */
    BOOL,
    /** Zserio int8 type. */
    INT8,
    /** Zserio int16 type. */
    INT16,
    /** Zserio int32 type. */
    INT32,
    /** Zserio int64 type. */
    INT64,
    /** Zserio uint8 type. */
    UINT8,
    /** Zserio uint16 type. */
    UINT16,
    /** Zserio uint32 type. */
    UINT32,
    /** Zserio uint64 type. */
    UINT64,
    /** Zserio varint16 type. */
    VARINT16,
    /** Zserio varint32 type. */
    VARINT32,
    /** Zserio varint64 type. */
    VARINT64,
    /** Zserio varint type. */
    VARINT,
    /** Zserio varuint16 type. */
    VARUINT16,
    /** Zserio varuint32 type. */
    VARUINT32,
    /** Zserio varuint64 type. */
    VARUINT64,
    /** Zserio varuint type. */
    VARUINT,
    /** Zserio varsize type. */
    VARSIZE,
    /** Zserio fixed signed bitfield type. */
    FIXED_SIGNED_BITFIELD,
    /** Zserio fixed unsigned bitfield type. */
    FIXED_UNSIGNED_BITFIELD,
    /** Zserio dynamic signed bitfield type. */
    DYNAMIC_SIGNED_BITFIELD,
    /** Zserio dynamic unsigned bitfield type. */
    DYNAMIC_UNSIGNED_BITFIELD,
    /** Zserio float16 type. */
    FLOAT16,
    /** Zserio float32 type. */
    FLOAT32,
    /** Zserio float64 type. */
    FLOAT64,
    /** Zserio string type. */
    STRING,
    /** Zserio extern type. */
    EXTERN,
    /** Zserio enumeration type. */
    ENUM,
    /** Zserio bitmask type. */
    BITMASK,
    /** Zserio structure type. */
    STRUCT,
    /** Zserio choice type. */
    CHOICE,
    /** Zserio union type. */
    UNION,
    /** Zserio SQL table type. */
    SQL_TABLE,
    /** Zserio SQL database type. */
    SQL_DATABASE,
    /** Zserio service type. */
    SERVICE,
    /** Zserio pubsub type. */
    PUBSUB
}
