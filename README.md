<img src="doc/long.png" height="100">
-----

No time to read? Go to the [Quick Start](#quick-start)

In for the numbers? Head over to [benchmarks](benchmarks/README.md)

------

## Serialization framework

The zserio serialization framework allows you to serialize data in a compact and efficient way.

The key features include

- compactness (smaller than most other serializers)
- advanced schema definition options
- cross-platform
- multiple programming languages


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

To be able to serialize data with zserio, you have to follow these basic steps:

1. Write the schema definition
2. Compile the schema and generate code
3. Set up your development environment with the zserio runtime
4. Serialize/deserialize using the generated code

This Quick Start tutorial features code generation in C++. Go to the [zserio JAVA tutorial](doc/JavaTutorial.md) if you are interested in hands-on JAVA with zserio.

### Installation & Prerequisites

Before we start, make sure you have the following components installed:

- JAVA JRE
- CMake

The easiest way of compiling the schema is to download the latest build of the zserio compiler from [Releases](https://github.com/welovemaps/zserio/releases).

If you want to  build from source, please follow the [Build Instructions for zserio Compiler](doc/zserio-compiler.md).

### Writing a schema

Open up your favorite text editor and start writing your schema. We will use the example from above plus some additional structures to showcase some of zserio's features.

```
package tutorial;

struct Employee
{
  uint8           age : age <= 65; // max age is 65
  string          name;
  uint16          salary;
  optional uint16 bonus;
  Title           title;

  // if employee is a team lead, list the team members

  Employee        teamMember[] if title == Title.TEAM_LEAD;
}

enum uint8 Title
{
  DEVELOPER = 0,
  TEAM_LEAD = 1,
  CTO       = 2,
}
```
We have added some of zserio's features above. Let's quickly take a look:

- **constraints**

  Although the `uint8` of field `age` would allow values up to 255, we limit the use already in the schema definition by using a [Constraint](doc/ZserioLanguageOverview/CompoundTypes.md#constraints)

- **optional fields**

  The `bonus` field is prefixed with the keyword `optional` which will add a 1-bit bool before that field which indicates whether the field exists. If it is not set then only one bit will be added to the byte stream.

- **conditions**

    We add a list of employees only if the employee is a team lead.

For more details on the features of zserio head over to the [zserio language overview](doc/ZserioLanguageOverview/ZserioLanguageOverview.md).


We now save the file to disk as `tutorial.zs`. Please note that the filename has to be equivalent to the package name inside the zserio file and that the zserio compiler only accepts file extension *zs*.

### Compiling and generating code

We now are ready to compile the schema with the zserio compiler. The zserio compiler checks the schema file and its [imported files](doc/ZserioLanguageOverview/PackagesAndImports.md) and reports errors and warnings.
In addition the zserio compiler generates code for the supported languages and may generate HTML documentation. See the [zserio compiler User Guide](doc/zserioCompilerUserGuide.md) for details.

So let's generate some C++ code:

```
java -jar zserio.jar -cpp src -withWriterCode
```
This command generates C++ code and puts it in the src folder. The -withWriterCode

## Features

- optional elements
- constraints
- default values
- parameters
- alignments
- offsets
- arrays with indexed offsets

## Documentation

Documentation of the schema language can be found in the [Zserio Language Overview](doc/ZserioLanguageOverview/ZserioLanguageOverview.md).

Check out the [Quick Reference](doc/ZserioLanguageOverview/ZserioQuickReference.md) and the mappings for JAVA and C++ in the doc folder.
