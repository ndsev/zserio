# zserio Base Types Mapping

This document contains mapping tables from zserio types to supported programming language types.

Currently supported code generators and runtimes are
- Java
- C++


zserio type | Java type | C++ type
------------|-----------|---------
uint8| short | uint8_t
uint16 | int | uint16_t
uint32 | long | uint32_t
uint64 | java.math.BigInteger | uint64_t
int8 | byte | int8_t
int16 | short | int16_t
int32 | int | int32_t
int64 | long | int64_t
bit:1...bit:7 | byte | uint8_t
bit:8 | short | uint8_t
bit:9...bit:15 | short | uint16_t
bit:16 | int | uint16_t
bit:17...bit:31 | int | uint32_t
bit:32 | long | uint32_t
bit:33...bit:63 | long | uint64_t
int:1...int:8 | byte | int8_t
int:9...int:16 | short | int16_t
int:17...int:32 | int | int32_t
int:33...int:64 | long | int64_t
float16 | float | float
varuint16 | short | uint16_t
varuint32 | int | uint32_t
varuint64 | long | uint64_t
varint16 | short | int16_t
varint32 | int | int32_t
varint64 | long | int64_t
bool | boolean | bool
string | String | std::string
