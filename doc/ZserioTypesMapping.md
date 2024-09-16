# Zserio Types Mapping

This document contains mapping tables from zserio types to supported programming language types.

Currently supported code generators and runtimes are:
- **Java**
- **C++**
- **Python**

## Base Types

Zserio type     | Java type                     | C++ type               | Python type        |
----------------|-------------------------------|------------------------|--------------------|
uint8           | `short`                       | `uint8_t`              | `int`              |
uint16          | `int`                         | `uint16_t`             | `int`              |
uint32          | `long`                        | `uint32_t`             | `int`              |
uint64          | `java.math.BigInteger`        | `uint64_t`             | `int`              |
int8            | `byte`                        | `int8_t`               | `int`              |
int16           | `short`                       | `int16_t`              | `int`              |
int32           | `int`                         | `int32_t`              | `int`              |
int64           | `long`                        | `int64_t`              | `int`              |
bit:1...bit:7   | `byte`                        | `uint8_t`              | `int`              |
bit:8           | `short`                       | `uint8_t`              | `int`              |
bit:9...bit:15  | `short`                       | `uint16_t`             | `int`              |
bit:16          | `int`                         | `uint16_t`             | `int`              |
bit:17...bit:31 | `int`                         | `uint32_t`             | `int`              |
bit:32          | `long`                        | `uint32_t`             | `int`              |
bit:33...bit:63 | `long`                        | `uint64_t`             | `int`              |
bit:64          | `java.math.BigInteger`        | `uint64_t`             | `int`              |
int:1...int:8   | `byte`                        | `int8_t`               | `int`              |
int:9...int:16  | `short`                       | `int16_t`              | `int`              |
int:17...int:32 | `int`                         | `int32_t`              | `int`              |
int:33...int:64 | `long`                        | `int64_t`              | `int`              |
float16         | `float`                       | `float`                | `float`            |
float32         | `float`                       | `float`                | `float`            |
float64         | `double`                      | `double`               | `float`            |
varint16        | `short`                       | `int16_t`              | `int`              |
varint32        | `int`                         | `int32_t`              | `int`              |
varint64        | `long`                        | `int64_t`              | `int`              |
varint          | `long`                        | `int64_t`              | `int`              |
varuint16       | `short`                       | `uint16_t`             | `int`              |
varuint32       | `int`                         | `uint32_t`             | `int`              |
varuint64       | `long`                        | `uint64_t`             | `int`              |
varuint         | `java.math.BigInteger`        | `uint64_t`             | `int`              |
varsize         | `int`                         | `uint32_t`             | `int`              |
bool            | `boolean`                     | `bool`                 | `bool`             |
string          | `String`                      | `std::string`          | `string`           |
extern          | `zserio.runtime.io.BitBuffer` | `zserio::BitBuffer`    | `zserio.BitBuffer` |
bytes           | `byte[]`                      | `std::vector<uint8_t>` | `bytearray`        |

## Array Types

> Java uses native arrays.

> C++ uses STL vector.

> Python uses native arrays.
