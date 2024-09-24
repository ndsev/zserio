# C++ Generator for Zserio

Zserio extension generates C++ [serialization API](#serialization-api) from the Zserio schema together
with [additional API](#additional-api).

The generated code must be always linked with [C++ Runtime Library](https://zserio.org/doc/runtime/latest/cpp)
which provides functionality common for all generated code.

For a **quick start** see the [C++ Tutorial](https://github.com/ndsev/zserio-tutorial-cpp#zserio-c-quick-start-tutorial).

## Content

[Supported C++ Standards](#supported-c-standards)

[Supported Platforms](#supported-platforms)

[Supported Compilers](#supported-compilers)

[Serialization API](#serialization-api)

&nbsp; &nbsp; &nbsp; &nbsp; [Ordering Rules](#ordering-rules)

[Additional API](#additional-api)

&nbsp; &nbsp; &nbsp; &nbsp; [Range Check](#range-check)

&nbsp; &nbsp; &nbsp; &nbsp; [Validation](#validation)

&nbsp; &nbsp; &nbsp; &nbsp; [Code Comments](#code-comments)

&nbsp; &nbsp; &nbsp; &nbsp; [Type Information](#type-information)

&nbsp; &nbsp; &nbsp; &nbsp; [Reflections](#reflections)

&nbsp; &nbsp; &nbsp; &nbsp; [JSON Debug String](#json-debug-string)

&nbsp; &nbsp; &nbsp; &nbsp; [Polymorphic Allocators](#polymorphic-allocators)

[Using Zserio CMake Helper](#using-zserio-cmake-helper)

[Functional Safety](#functional-safety)

[Compatibility Check](#compatibility-check)

## Supported C++ Standards

Zserio C++ generator supports the C++11 standard which was published as ISO/IEC 14882:2011.

## Supported Platforms

Zserio C++ generator supports the following platforms:

- 64-bit Linux
- 32-bit Linux
- 64-bit Windows

## Supported Compilers

Zserio C++ generator supports the following C++ compilers:

- g++ 7.5.0
- clang 11.0.0
- MinGW 7.5.0
- MSVC 2017

Although newer C++ compilers are not tested, they should work as well as long as they are backward compatible.

## Serialization API

The serialization API provides the following features for all Zserio structures, choices and unions:

- Full [`std::allocator`](https://en.cppreference.com/w/cpp/memory/allocator) support in constructors.
- Serialization of all Zserio objects to the bit stream
  (method [`zserio::serialize()`](https://zserio.org/doc/runtime/latest/cpp/SerializeUtil_8h.html)).
- Deserialization of all Zserio objects from the bit stream
  (method [`zserio::deserialize()`](https://zserio.org/doc/runtime/latest/cpp/SerializeUtil_8h.html)).
- Getters and setters for all fields
- Method `bitSizeOf()` which calculates a number of bits needed for serialization of the Zserio object.
- Comparison operator which compares two Zserio objects field by field.
- Less than operator which compares two Zserio objects field by field using the
  [Ordering Rules](#ordering-rules).
- Method `hashCode()` which calculates a hash code of the Zserio object.

### Ordering Rules

Both C++ runtime and generator provide `operator<` (in addition to `operator==`) on all objects which can
occur in generated API. Thus it's possible to easily use generated objects in `std::set` or `std::map`.

In general, all compound objects are compared lexicographically (inspired by
[lexicographical_compare](https://en.cppreference.com/w/cpp/algorithm/lexicographical_compare)):

* Parameters are compared first in order of definition.
* Fields are compared:
   * In case of [structures](../../../doc/ZserioLanguageOverview.md#structure-type),
     all fields are compared in order of definition.
   * In case of [unions](../../../doc/ZserioLanguageOverview.md#union-type),
     the `choiceTag` is compared first and then the selected field is compared.
   * In case of [choices](../../../doc/ZserioLanguageOverview.md#choice-type),
     only the selected field is compared (if any).

Comparison of [optional fields](../../../doc/ZserioLanguageOverview.md#optional-members)
(kept in `InplaceOptionalHolder` or `HeapOptionalHolder`):

* When both fields are present, they are compared.
* Otherwise the missing field is less than the other field if and only if the other field is present.
* If both fields are missing, they are equal.

> Note that same rules applies for
  [extended fields](../../../doc/ZserioLanguageOverview.md#extended-members) and for fields in unions
  and choices, which are internally kept in `AnyHolder`.

Comparison of [arrays](../../../doc/ZserioLanguageOverview.md#array-types)
(`Array`) uses native comparison of the underlying `std::vector`.

Comparison of [`extern` fields](../../../doc/ZserioLanguageOverview.md#extern-type) (kept in `BitBuffer`):

* Compares byte by byte and follows the rules of
  [lexicographical compare](https://en.cppreference.com/w/cpp/algorithm/lexicographical_compare).
* The last byte is properly masked to use only the proper number of bits.

Comparison of [`bytes` fields](../../../doc/ZserioLanguageOverview.md#bytes-type) uses native comparison
on the underlying `std::vector`.

## Additional API

The following additional API features which are disabled by default, are available for users:

- [Range Check](#range-check) - Generation of code for the range checking for fields and parameters (integer types only).
- [Validation](#validation) - Generation of code which is used for SQLite databases validation.
- [Code Comments](#code-comments) - Generation of C++ doxygen comments in code.
- [Type Information](#type-information) - Generation of static information about Zserio objects like schema names, types, etc.
- [Reflections](#reflections) - Generation of code which supports generic access to any Zserio objects using reflections.
- [JSON Debug String](#json-debug-string) - Supports export/import of all Zserio objects to/from the JSON file.
- [Polymorphic Allocators](#polymorphic-allocators) - Generation of code which accepts polymorphic allocators instead of standard allocators.

All of these features can be enabled using command line options which are described in the
[Zserio User Guide](../../../doc/ZserioUserGuide.md#zserio-command-line-interface) document.

### Range Check

Because not all Zserio integer types can be directly mapped to the C++ types (e.g. `bit:4` is mapped to
`uint8_t`), it can be helpful to explicitly check values stored in C++ types for the correct ranges
(e.g to check if `uint8_t` value which holds `bit:4`, is from range `<0, 15>`). Such explicit checks allow
throwing exception with the detailed description of the Zserio field with wrong value.

The range check code is generated only in the `write()` method directly before the field is written to the
bit stream.

### Validation

The validation generates method `validate()` in all generated SQL databases. This method validates all
SQL tables which are present in the SQL database. The SQL table validation consists of the following steps:

- The check of the SQL table schema making sure that SQL table has the same schema as defined in Zserio.
- The check of all columns in all rows making sure that values stored in the SQL table columns are valid.

The check of all columns consists of the following steps:

- The check of the column type making sure that SQL column type is the same as defined in Zserio.
- The check of all blobs making sure that the blob is possible to parse successfully.
- The check of all integer types making sure that integer values are in the correct range as defined in Zserio.
- The check of all enumeration types making sure that enumeration values are valid as defined in Zserio.
- The check of all bitmask types making sure that bitmask values are valid as defined in Zserio.

### Code Comments

The code comments generate [Doxygen](https://www.doxygen.nl/index.html) comments for all generated Zserio
objects. Some comments available in Zserio schema are used as well.

### Type Information

The type information generates static method `typeInfo()` in all generated Zserio types (except of Zserio
subtypes). This method returns all static information of Zserio type which is available in the Zserio schema
(e.g. schema name, if field is optional, if field is array, etc...).

### Reflections

The reflections generate method `reflectable()` in the following Zserio types:

- structures
- choices
- unions
- bitmasks

The reflections generate as well method `enumReflectable()` for Zserio enums.

The reflection method returns pointer to the reflectable interface which allows application generic access to
the Zserio types (e.g. `getField()` method to get field value according to the schema name).

> Note that the reflections use type information, so type information must be enabled as well!

> Note that inspiration and more implementation details how to use reflections can be found in our
  [reflection test](../../../test/arguments/with_reflection_code/cpp/WithReflectionCodeTest.cpp).

### JSON Debug String

JSON debug string feature provides export and import to/from JSON string for all Zserio structures,
choices and unions:

- Export to the JSON string
  (method [`zserio::toJsonString()`](https://zserio.org/doc/runtime/latest/cpp/DebugStringUtil_8h.html)).
- Import from the JSON string
  (method [`zserio::fromJsonString()`](https://zserio.org/doc/runtime/latest/cpp/DebugStringUtil_8h.html)).

> Note that this feature is available only if type information and reflections are enabled!

### Polymorphic Allocators

By default, C++ generated objects use [`std::allocator`](https://en.cppreference.com/w/cpp/memory/allocator),
which doesn't allow any custom memory management. However, C++ generator supports as well
[`zserio::pmr::PolymorphicAllocator`](https://zserio.org/doc/runtime/latest/cpp/PolymorphicAllocator_8h.html),
which is inspired by the
[`std::pmr::polymorphic_allocator`](https://en.cppreference.com/w/cpp/memory/polymorphic_allocator) from C++17
standard.

To enable Zserio polymorphic allocators, it is necessary to specify command line
option `-setCppAllocator polymorphic`.

For detailed information about custom memory management see
[Custom Memory Management using Polymorphic Allocators](https://github.com/ndsev/zserio-tutorial-cpp/tree/master/pmr)
in [ZserioCppTutorial](https://github.com/ndsev/zserio-tutorial-cpp/).

## Using Zserio CMake Helper

Zserio provides [`zserio_compiler.cmake`](../../../cmake/zserio_compiler.cmake) helper, which defines custom function `zserio_generate_cpp`.
This function can be used for automatic generation of C++ sources from zserio schemas.

### Prerequisites

* CMake 3.15+
* Java must be available - the function calls `find_package(JAVA java)`
* `ZSERIO_JAR_FILE` must be defined either as an environment or CMake variable

### Usage

    zserio_generate_cpp(
        TARGET <target>
        [SRC_DIR <directory>]
        [MAIN_ZS <file>]
        [GEN_DIR <directory>]
        [EXTRA_ARGS <argument>...]
        [GENERATED_SOURCES_VAR <variable>]
        [OUTPUT_VAR <variable>]
        [ERROR_VAR <variable>]
        [RESULT_VAR <variable>]
        [FORCE_REGENERATION]
        [CLEAN_GEN_DIR])

### Arguments

`TARGET`

Target to which the generated sources will be assigned.

`SRC_DIR`

Source directory for zserio schemas. Optional, defaults to `CMAKE_CURRENT_SOURCE_DIR`.

`MAIN_ZS`

Main zserio schema. Optional if the MAIN_ZS file is specified as a source for the given `TARGET`.

`GEN_DIR`

Directory where the C++ sources will be generated.

`EXTRA_ARGS`

Extra arguments to be passed to the Zserio tool.

`GENERATED_SOURCES_VAR`

The variable will be set with a list of generated source files (full paths). Optional.

`OUTPUT_VAR`

The variable will be set with the contents of the standard output pipe. Optional.
If not set, the standard output pipe is printed.

`ERROR_VAR`

The variable will be set with the contents of the standard error pipe. Optional.
If not set, the standard error pipe is printed.

`RESULT_VAR`

The variable will be set to contain the result of the zserio generator. Optional.
If not set, a `FATAL_ERROR` is raised in case of the zserio generator error.

`FORCE_RECONFIGURE`

Forces regeneration every time the CMake configure is run.

`CLEAN_GEN_DIR`

Cleans `GEN_DIR` when generation in CMake configure-time is run.

> Note that `OUTPUT_VAR` and `ERROR_VAR` can be set to the same value and then both pipes will be merged.

> Note that `OUTPUT_VAR`, `ERROR_VAR` and `RESULT_VAR` are updated only when the generation is executed within
> the configure-time - i.e. for the first time or when zserio schema sources are changed, etc.
> See "[How if works](#how-it-works)" for more info.

### Example

    set(CMAKE_MODULE_PATH "${ZSERIO_RELEASE}/cmake")
    set(ZSERIO_JAR_FILE "${ZSERIO_RELEASE}/zserio.jar")
    include(zserio_compiler)

    add_library(sample_zs sample.zs)
    zserio_generate_cpp(
        TARGET sample_zs
        GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)

### How it works

First time the CMake configure is run, the sources are generated using `execute_process` directly in
configure-time and auxiliary information (timestamps, list of generated sources, etc.) is stored in the
CMake cache. The auxiliary info is used to define a custom command which uses the same zserio command line
as the original `execute_process` and thus allows to re-generate sources when it's needed - e.g. after the
clean step.

The custom command is sufficient as long as the generated sources remains unchanged. Otherwise the
`execute_process` must be re-run in configure-time to ensure that all generated sources are collected correctly.
This functionality is achieved using the auxiliary information mentioned above.

List of generated sources can change in following situations:

- ZSERIO_JAR_FILE is changed
- Zserio schema sources are changed
- EXTRA_ARGS are changed

## Functional Safety

Zserio's C++ support is designed with a strong focus on functional safety, aiming to ensure the reliability,
integrity, and robustness of the system while reducing the risk of software-induced hazards. This section
provides an overview of the functional safety measures implemented, highlighting development practices that
contribute to the framework's safety and trustworthiness.

### C++ Runtime Library

The following describes features which minimize the risk of Zserio C++ runtime library malfunctioning behavior:

- Supported compilers (minimum versions): gcc 7.5.0, Clang 11.0.0, MinGW 7.5.0, MSVC 2017
- Warnings are treated as errors for all supported compilers
- All features are properly tested by [unit tests](runtime/test/) for all supported compilers (>600 tests)
- Implemented automatic test coverage threshold check using [llvm-cov](https://llvm.org/docs/CommandGuide/llvm-cov.html) and Clang 14.0.6 (see
  [coverage report](https://zserio.org/doc/runtime/latest/cpp/coverage/clang/index.html) which fulfills a line coverage threshold of 99%)
- AddressSanitizer is run with no findings
- UndefinedBehaviourSanitizer is run with no findings
- C++ runtime library sources are checked by static analysis tool clang-tidy version 14.0.6
- C++ runtime library sources are checked by [SonarCloud](https://sonarcloud.io/summary/new_code?id=ndsev_zserio)

#### Clang-tidy Usage

Clang-tidy tool is run using [this configuration](runtime/ClangTidyConfig.txt).
The clang-tidy report from the latest C++ runtime library is available [here](https://zserio.org/doc/runtime/latest/cpp/clang-tidy/clang-tidy-report.txt).

Due to compatibility and functional safety considerations, zserio is constrained to utilize the C++11 standard.
Consequently, certain clang-tidy findings remain unresolved at present. This is mainly attributed to
zserio's C++ runtime library, which operates at a lower level and emulates standard abstractions like
std::span or std::string_view introduced in C++17.

Therefore all clang-tidy findings have been carefully checked and filtered out using definitions in clang-tidy
[suppression file](runtime/ClangTidySuppressions.txt).
This suppression file contains as well the brief reasoning why these findings were not fixed. This solution
with suppression file has been chosen not to pollute C++ runtime sources with `// NOLINT` comments and to
allow implementation of warnings-as-error feature. The clang-tidy suppression file is automatically used
during compilation using CMake (see [CMake runtime configuration](runtime/CMakeLists.txt)).

### C++ Generated Code

The following describes features which minimize the risk of Zserio C++ generated code malfunctioning behavior:

- Supported compilers (minimum versions): gcc 7.5.0, clang 11.0.0, MinGW 7.5.0, MSVC 2017
- Warnings are treated as errors for all supported compilers
- All zserio language features are properly tested by [unit tests](../../../test) for all supported compilers
  (>1700 tests)
- Unit tests check C++ code generated from small zserio schemas (>70 schemas)
- Generated sources are checked by static analysis tool clang-tidy version 14.0.6 using
  [this configuration](runtime/ClangTidyConfig.txt)
- Generated sources are checked by [SonarCloud](https://sonarcloud.io/summary/new_code?id=ndsev_zserio)

### Exceptions

In functional-critical systems, the primary use case of zserio involves reading data. The zserio C++ runtime
library, along with the generated C++ code, may throw a `zserio::CppRuntimeException` in rare circumstances.
These exceptions can occur during reading, writing, and within its reflection functionality. While there are
numerous possibilities for when the `zserio::CppRuntimeException` exception can be thrown, this section
focuses specifically on describing exceptions that may occur during reading.

#### Exceptions During Reading

The following table describes all possibilities when C++ generated code can throw
a `zserio::CppRuntimeException` during parsing of binary data:

| Module | Method | Exception Message | Description |
| ------ | ------ | ----------------- | ----------- |
| `BitStreamReader.cpp` | constructor | "BitStreamReader: Buffer size exceeded limit '[MAX_BUFFER_SIZE]' bytes!" | Throws if provided buffer is bigger that 536870908 bytes (cca 511MB) on 32-bit OS or 2**64/8-4 bytes on 64-bit OS. |
| `BitStreamReader.cpp` | constructor | "BitStreamReader: Wrong buffer bit size ('[BUFFER_SIZE]' < '[SPECIFIED_BYTE_SIZE]')!" | Throws if provided buffer is smaller than specified bit size. This could happen only in case of wrong arguments. |
| `BitStreamReader.cpp` | `throwNumBitsIsNotValid()` | "BitStreamReader: ReadBits #[NUM_BITS] is not valid, reading from stream failed!" | Throws if `readBits()`, `readSignedBits()`, `readBits64()` or `readSignedBits64()` has been called with wrong (too big) `numBits` argument. This could happen only in case of data inconsistency, e.g. if dynamic bit field has length bigger than 32 or 64 bits respectively. |
| `BitStreamReader.cpp` | `throwEof()` | "BitStreamReader: Reached eof(), reading from stream failed!" | Throws if the end of the underlying buffer has been reached (reading beyond stream). This can happen in two cases: either due to data inconsistency or if the buffer size is set to 0 and data reading is requested. Data inconsistency can occur, for example, if the defined array length is greater than the actual data stored in the stream. |
| `BitStreamReader.cpp` | `readVarSize()` | "BitStreamReader: Read value '[VARSIZE_VALUE]' is out of range for varsize type!" | Throws if `varsize` value stored in stream is bigger than 2147483647. This could happen only in case of data inconsistency when `varsize` value stored in the stream is wrong. |
| `OptionalHolder.h` | `throwNonPresentException()` | "Trying to access value of non-present optional field!" | Throws if optional value is not present during access. This could happen only in case of data inconsistency when optional field is not present in the stream and there is a reference to it in some expression. |
| Generated Sources | Object read constructor | "Read: Wrong offset for field [COMPOUND_NAME].[FIELD_NAME]: [STREAM_BYTE_POSITION] != [OFFSET_VALUE]!" | Throws in case of wrong offset. This could happen only in case of data inconsistency when offset value stored in the stream is wrong. |
| Generated Sources | Object read constructor | "Read: Constraint violated at [COMPOUND_NAME].[FIELD_NAME]!" | Throws in case of wrong constraint. This could happen only in case of data inconsistency when some constraint is violated. |
| Generated Sources | Object read constructor | "No match in choice [NAME]!" | Throws in case of wrong choice selector. This could happen only in case of data inconsistency when choice selector stored in the stream is wrong. |
| Generated Sources | Object read constructor | "No match in union [NAME]!" | Throws in case of wrong union tag. This could happen only in case of data inconsistency when union tag stored in the stream is wrong. |
| Generated Sources | Bitmask constructor | "Value for bitmask [NAME] out of bounds: [VALUE]!" | Throws if value stored in stream is bigger than bitmask upper bound. This could happen only in case of data inconsistency when bitmask value stored in the stream is wrong. |
| Generated Sources | `valueToEnum` | "Unknown value for enumeration [NAME]: [VALUE]!" | Throws in case of unknown enumeration value. This could happen only in case of data inconsistency when enumeration value stored in the stream is wrong. |

## Compatibility Check

C++ generator honors the `zserio_compatibility_version` specified in the schema. However note that only
the version specified in the root package of the schema is taken into account. The generator checks that
language features used in the schema are still encoded in a binary compatible way with the specified
compatibility version and fires an error when it detects any problem.

> Note: Binary encoding of packed arrays has been changed in version `2.5.0` and thus versions `2.4.x` are
binary incompatible with later versions.
