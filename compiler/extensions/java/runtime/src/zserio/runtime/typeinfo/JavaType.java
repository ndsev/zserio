package zserio.runtime.typeinfo;

/**
 * Enumeration which specifies Java type used in type information.
 */
public enum JavaType
{
    /** Java boolean type */
    BOOLEAN,
    /** Java byte type */
    BYTE,
    /** Java short type */
    SHORT,
    /** Java int type */
    INT,
    /** Java long type */
    LONG,
    /** Java BigInteger type */
    BIG_INTEGER,
    /** Java float type */
    FLOAT,
    /** Java double type */
    DOUBLE,
    /** Java bytes type (mapped to byte[]) */
    BYTES,
    /** Java String type */
    STRING,
    /** Java zserio.runtime.io.BitBuffer type */
    BIT_BUFFER,
    /** Java enumeration generated from zserio enumeration type */
    ENUM,
    /** Java object generated from zserio bitmask type */
    BITMASK,
    /** Java object generated from zserio structure type */
    STRUCT,
    /** Java object generated from zserio choice type */
    CHOICE,
    /** Java object generated from zserio union type */
    UNION,
    /** Java object generated from zserio SQL table type */
    SQL_TABLE,
    /** Java object generated from zserio SQL database type */
    SQL_DATABASE,
    /** Java object generated from zserio service type */
    SERVICE,
    /** Java object generated from zserio pubsub type */
    PUBSUB
}
