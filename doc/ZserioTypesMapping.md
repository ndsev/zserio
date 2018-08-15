# Zserio Types Mapping

This document contains mapping tables from zserio types to supported programming language types.

Currently supported code generators and runtimes are:
- **Java**
- **C++**

## Base Types

Zserio type     | Java type              | C++ type
--------------- |----------------------- |-------------
uint8           | `short`                | `uint8_t`
uint16          | `int`                  | `uint16_t`
uint32          | `long`                 | `uint32_t`
uint64          | `java.math.BigInteger` | `uint64_t`
int8            | `byte`                 | `int8_t`
int16           | `short`                | `int16_t`
int32           | `int`                  | `int32_t`
int64           | `long`                 | `int64_t`
bit:1...bit:7   | `byte`                 | `uint8_t`
bit:8           | `short`                | `uint8_t`
bit:9...bit:15  | `short`                | `uint16_t`
bit:16          | `int`                  | `uint16_t`
bit:17...bit:31 | `int`                  | `uint32_t`
bit:32          | `long`                 | `uint32_t`
bit:33...bit:63 | `long`                 | `uint64_t`
int:1...int:8   | `byte`                 | `int8_t`
int:9...int:16  | `short`                | `int16_t`
int:17...int:32 | `int`                  | `int32_t`
int:33...int:64 | `long`                 | `int64_t`
float16         | `float`                | `float`
float32         | `float`                | `float`
float64         | `double`               | `double`
varint16        | `short`                | `int16_t`
varint32        | `int`                  | `int32_t`
varint64        | `long`                 | `int64_t`
varint          | `long`                 | `int64_t`
varuint16       | `short`                | `uint16_t`
varuint32       | `int`                  | `uint32_t`
varuint64       | `long`                 | `uint64_t`
varuint         | `java.math.BigInteger` | `uint64_t`
bool            | `boolean`              | `bool`
string          | `String`               | `std::string`

## Array Types

Zserio element type | Java type                                 | C++ type
------------------- | ----------------------------------------- | ---------------------------
uint8               | `zserio.runtime.array.UnsignedByteArray`  | `zserio::UnsignedByteArray`
uint16              | `zserio.runtime.array.UnsignedShortArray` | `zserio::UnsignedShortArray`
uint32              | `zserio.runtime.array.UnsignedIntArray`   | `zserio::UnsignedIntArray`
uint64              | `zserio.runtime.array.BigIntegerArray`    | `zserio::UnsignedLongArray`
int8                | `zserio.runtime.array.ByteArray`          | `zserio::ByteArray`
int16               | `zserio.runtime.array.ShortArray`         | `zserio::ShortArray`
int32               | `zserio.runtime.array.IntArray`           | `zserio::IntArray`
int64               | `zserio.runtime.array.LongArray`          | `zserio::LongArray`
bit:1...bit:8       | `zserio.runtime.array.UnsignedByteArray`  | `zserio::UnsignedByteArray`
bit:9...bit:16      | `zserio.runtime.array.UnsignedShortArray` | `zserio::UnsignedShortArray`
bit:17...bit:32     | `zserio.runtime.array.UnsignedIntArray`   | `zserio::UnsignedIntArray`
bit:33...bit:63     | `zserio.runtime.array.UnsignedLongArray`  | `zserio::UnsignedLongArray`
int:1...int:8       | `zserio.runtime.array.ByteArray`          | `zserio::ByteArray`
int:9...int:16      | `zserio.runtime.array.ShortArray`         | `zserio::ShortArray`
int:17...int:32     | `zserio.runtime.array.IntArray`           | `zserio::IntArray`
int:33...int:64     | `zserio.runtime.array.LongArray`          | `zserio::LongArray`
float16             | `zserio.runtime.array.Float16Array`       | `zserio::Float16Array`
float32             | `zserio.runtime.array.Float32Array`       | `zserio::Float32Array`
float64             | `zserio.runtime.array.Float64Array`       | `zserio::Float64Array`
varint16            | `zserio.runtime.array.VarInt16Array`      | `zserio::VarInt16Array`
varint32            | `zserio.runtime.array.VarInt32Array`      | `zserio::VarInt32Array`
varint64            | `zserio.runtime.array.VarInt64Array`      | `zserio::VarInt64Array`
varint              | `zserio.runtime.array.VarIntArray`        | `zserio::VarIntArray`
varuint16           | `zserio.runtime.array.VarUInt16Array`     | `zserio::VarUInt16Array`
varuint32           | `zserio.runtime.array.VarUInt32Array`     | `zserio::VarUInt32Array`
varuint64           | `zserio.runtime.array.VarUInt64Array`     | `zserio::VarUInt64Array`
varuint             | `zserio.runtime.array.VarUIntArray`       | `zserio::VarUIntArray`
bool                | `zserio.runtime.array.BoolArray`          | `zserio::BoolArray`
string              | `zserio.runtime.array.StringArray`        | `zserio::StringArray`
struct              | `zserio.runtime.array.ObjectArray`        | `zserio::ObjectArray`
choice              | `zserio.runtime.array.ObjectArray`        | `zserio::ObjectArray`
