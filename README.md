![zserio](doc/long.png)

No time to read? Go to the [Quick Start](#quick-start)

In for the numbers? Head over to [benchmarks](benchmarks/README.md)

------

## Serialization framework

The zserio serialization framework allows you to serialize data in a compact and efficient way. It is

- compact (no wire format overhead)
- cross-platform
- cross-language

It can be retrofitted on top of almost any other serialization language or model, since it gives the developer powerful low-level access.

It features simple and compound data structures and provides advanced features for controlling at design time what writers will be able to fill in.

Although it does not have a wire format, we have added some convenience keywords lately that encapsulate some functionality. So one could say that zserio also has a "wire format LITE".

It is still missing some features that we will work on in the next months, which for example include definition of rpc service interfaces. We will not include yet another rpc framework, but will integrate with other rpc frameworks starting with  gRPC. So you will be able to use zserio for generating gRPC stubs and use zserio in gRPC for improved message sizes.

## Quick introduction

As we have stated earlier, zserio does not have any wire format. So basically: what you see is what you get. (please note that zserio uses network byte order in the serialized stream, but the generated code does take care of this)

```
package tutorial;

struct Employee
{
  uint8   age;
  string  name;
  uint16  salary;
  Title   title;
}

enum uint8 Title
{
  DEVELOPER = 0,
  TEAM_LEAD = 1,
  CTO       = 2,
}
```

So if we use the schema above and serialize one employee with

- age = 32
- name = John
- salary = 5000 $
- title = DEVELOPER

the resulting byte stream looks like this:

Byte position |Bit position | content / value | comment
----|-------|-------|-----|
0|0 - 7 | 32 (age)| uint8 is of fixed size 8 bit
1|8 - 15| 4 (string length)| string length is encoded in varuint64 field before actual string
2-4|16 - 31| John | UTF-8 encoded string
5-6|32 - 47| 5000 | `uint16` always uses 2 bytes
7 | 48 - 55| 0| enum is of size `uint8` so it uses 1 byte

Please note that in contrast to other serialization mechanisms zserio's variable integers do not provide the full range of values but rather stick to the indicated size. Example: a *varuint64* will be using max 8 bytes whilst not providing the full range of a *uint64_t*.

## Quick Start

Quick start including installation, generation of code and example application should be put here!
TODO

## Features

- optional elements
- constraints
- default values
- parameters
- alignments
- offsets
- arrays with indexed offsets

## Documentation

Documentation of the schema language can be found in the [zserio Language Overview](doc/zserioLanguageOverview.md).

Check out the [Quick Reference](doc/zserioQuickReference.md) and the mappings for JAVA and C++ in the doc folder.
