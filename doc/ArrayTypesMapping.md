# zserio Array Types Mapping

This document contains mapping tables from zserio types to supported programming language types.

Currently supported code generators and runtimes are
- Java
- C++

The following table shows the mapping of array types to the data types in the generated code:

zserio element type | Java type | C++ type
--------------------|-----------|---------
uint8 | zserio.runtime.array.UnsignedByteArray |zserio::UnsignedByteArray
uint16 |  zserio.runtime.array.UnsignedShortArray| zserio::UnsignedShortArray
uint32 |zserio.runtime.array.UnsignedIntArray |zserio::UnsignedIntArray
uint64| zserio.runtime.array.BigIntegerArray |zserio::UnsignedLongArray
int8 |zserio.runtime.array.ByteArray |zserio::ByteArray
int16| zserio.runtime.array.ShortArray |zserio::ShortArray
int32 |zserio.runtime.array.IntArray |zserio::IntArray
int64 |zserio.runtime.array.LongArray |zserio::LongArray
bit:1...bit:8| zserio.runtime.array.UnsignedByteArray |zserio::UnsignedByteArray
bit:9...bit:16| zserio.runtime.array.UnsignedShortArray |zserio::UnsignedShortArray
bit:17...bit:32 |zserio.runtime.array.UnsignedIntArray |zserio::UnsignedIntArray
bit:33...bit:63 |zserio.runtime.array.UnsignedLongArray |zserio::UnsignedLongArray
int:1...int:8 |zserio.runtime.array.ByteArray |zserio::ByteArray
int:9...int:16 |zserio.runtime.array.ShortArray |zserio::ShortArray
int:17...int:32 |zserio.runtime.array.IntArray |zserio::IntArray
int:33...int:64 |zserio.runtime.array.LongArray |zserio::LongArray
float16 |zserio.runtime.array.FloatArray |zserio::FloatArray
varuint16 |zserio.runtime.array.VarUInt16Array |zserio::VarUInt16Array
varuint32 |zserio.runtime.array.VarUInt32Array |zserio::VarUInt32Array
varuint64 |zserio.runtime.array.VarUInt64Array |zserio::VarUInt64Array
varint16 |zserio.runtime.array.VarInt16Array |zserio::VarInt16Array
varint32 |zserio.runtime.array.VarInt32Array |zserio::VarInt32Array
varint64 |zserio.runtime.array.VarInt64Array |zserio::VarInt64Array
bool| zserio.runtime.array.BoolArray |zserio::BoolArray
string |zserio.runtime.array.StringArray |zserio::StringArray
struct| zserio.runtime.array.ObjectArray |zserio::ObjectArray
choice | zserio.runtime.array.ObjectArray | zserio::ObjectArray
