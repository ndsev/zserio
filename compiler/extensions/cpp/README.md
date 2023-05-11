# C++ Generator for Zserio

Zserio extension which generates C++ serialization API from the Zserio schema.

For a **quick start** see the [C++ Tutorial](https://github.com/ndsev/zserio-tutorial-cpp#zserio-c-quick-start-tutorial).

For an **API documentation** see the [C++ Runtime Library](https://zserio.org/doc/runtime/latest/cpp).

## Functional Safety

Zserio's C++ support is designed with a strong focus on functional safety, aiming to ensure the reliability, integrity, and robustness of the system while reducing the risk of software-induced hazards. This section provides an overview of the functional safety measures implemented, highlighting development practices that contribute to the framework's safety and trustworthiness.

### C++ Runtime Library

The following describes features which minimize the risk of Zserio C++ runtime library malfunctioning behavior:

- Supported compilers (minimum versions): gcc 5.4.0, clang 8, MinGW 5.4.0, MSVC 2017
- Warnings are treaded as errors for all supported compilers
- All features are properly tested by unit tests for all supported compilers (>600 tests)
- Implemented automatic check of test coverage threshold with the for
  [clang](https://zserio.org/doc/runtime/latest/cpp/coverage/clang/index.html) builds (>98%)
- AddressSanitizer is run with no findings
- UndefinedBehaviourSanitizer is run with no findings
- C++ runtime library sources are checked by static analysis tool clang-tidy version 14

#### Clang-tidy Usage

Clang-tidy tool is run using [this configuration](https://github.com/ndsev/zserio/blob/master/compiler/extensions/cpp/runtime/ClangTidyConfig.txt).
The clang-tidy report from the latest C++ runtime library is available [here](https://zserio.org/doc/runtime/latest/cpp/clang-tidy/clang-tidy-report.txt).

Due to compatibility and functional safety considerations (as there are no available MISRA/AUTOSAR guidelines for C++ standards newer than C++14), zserio is constrained to utilize the C++11 standard. Consequently, certain clang-tidy findings remain unresolved at present. This is mainly attributed to zserio's C++ runtime library, which operates at a lower level and emulates standard abstractions like std::span or std::string_view introduced in C++17.

Therefore all clang-tidy findings have been carefully checked and filtered out using definitions in clang-tidy
[suppression file](https://github.com/ndsev/zserio/blob/master/compiler/extensions/cpp/runtime/ClangTidySuppressions.txt).
This suppression file contains as well the brief reasoning why these findings were not fixed. This solution
with suppression file has been chosen not to pollute C++ runtime sources with `// NOLINT` comments and to
allow implementation of warnings-as-error feature. The clang-tidy suppression file is automatically used
during compilation using `CMake`.


### C++ Generated Code

The following describes features which minimize the risk of Zserio C++ generated code malfunctioning behavior:

- Supported compilers (minimum versions): gcc 5.4.0, clang 8, MinGW 5.4.0, MSVC 2017
- Warnings are treaded as errors for all supported compilers
- All features are properly tested by unit tests for all supported compilers (>1700 tests)
- Generated C++ sources are checked by static analysis tool clang-tidy version 14 using
  [this configuration](https://github.com/ndsev/zserio/blob/master/compiler/extensions/cpp/runtime/ClangTidyConfig.txt)

### Exceptions

In functional-critical systems, the primary use case of zserio involves reading data. The zserio C++ runtime library, along with the generated C++ code, may throw a `zserio::CppRuntimeException` in rare circumstances. These exceptions can occur during reading, writing, and within its reflection functionality. While there are numerous possibilities for when the `zserio::CppRuntimeException` exception can be thrown, this section focuses specifically on describing exceptions that may occur during reading.

#### Exceptions During Reading

The following table describes all possibilities when C++ generated code can throw
a `zserio::CppRuntimeException` during parsing of binary data:

| Module | Method | Exception Message | Description |
| ------ | ------ | ----------------- | ----------- |
| `BitStreamReader.cpp` | constructor | "BitStreamReader: Buffer size exceeded limit '[MAX_BUFFER_SIZE]' bytes!" | Throws if provided buffer is bigger that 536870908 bytes (cca 511MB) on 32-bit OS or 2**64/8-4 bytes on 64-bit OS. |
| `BitStreamReader.cpp` | constructor | "BitStreamReader: Wrong buffer bit size ('[BUFFER_SIZE]' < '[SPECIFIED_BYTE_SIZE]')!" | Throws if provided buffer is smaller than specified bit size. This could happen only in case of wrong arguments. |
| `BitStreamReader.cpp` | `throwNumBitsIsNotValid()` | "BitStreamReader: ReadBits #[NUM_BITS] is not valid, reading from stream failed!" | Throws if `readBits()`, `readSignedBits()`, `readBits64()` or `readSignedBits64()` has been called with wrong (too big) `numBits` argument. This could happen only in case of data inconsistency, e.g. if dynamic bit field has length bigger than 32 or 64 bits respectively. |
| `BitStreamReader.cpp` | `throwEof()` | "BitStreamReader: Reached eof(), reading from stream failed!" | Throws if end of underlying buffer has been reached (reading beyond stream). This could happen only in case of data inconsistency, e.g. if array length is defined bigger than is actually stored in the stream). |
| `BitStreamReader.cpp` | `readVarSize()` | "BitStreamReader: Read value '[VARSIZE_VALUE]' is out of range for varsize type!" | Throws if `varsize` value stored in stream is bigger than 2147483647. This could happen only in case of data inconsistency when `varsize` value stored in the stream is wrong. |
| `OptionalHolder.h` | `throwNonPresentException()` | "Trying to access value of non-present optional field!" | Throws if optional value is not present during access. This could happen only in case of data inconsistency when optional field is not present in the stream and there is a reference to it in some expression. |
| Generated Sources | Object read constructor | "Read: Wrong offset for field [COMPOUND_NAME].[FIELD_NAME]: [STREAM_BYTE_POSITION] != [OFFSET_VALUE]!" | Throws in case of wrong offset. This could happen only in case of data inconsistency when offset value stored in the stream is wrong. |
| Generated Sources | Object read constructor | "Read: Constraint violated at [COMPOUND_NAME].[FIELD_NAME]!" | Throws in case of wrong constraint. This could happen only in case of data inconsistency when some constraint is violated. |
| Generated Sources | Object read constructor | "No match in choice [NAME]!" | Throws in case of wrong choice selector. This could happen only in case of data inconsistency when choice selector stored in the stream is wrong. |
| Generated Sources | Object read constructor | "No match in union [NAME]!" | Throws in case of wrong union tag. This could happen only in case of data inconsistency when union tag stored in the stream is wrong. |
| Generated Sources | Bitmask constructor | "Value for bitmask [NAME] out of bounds: [VALUE]!" | Throws if value stored in stream is bigger than bitmask upper bound. This could happen only in case of data inconsistency when bitmask value stored in the stream is wrong. |
| Generated Sources | `valueToEnum` | "Unknown value for enumeration [NAME]: [VALUE]!" | Throws in case of unknown enumeration value. This could happen only in case of data inconsistency when enumeration value stored in the stream is wrong. |

## Compatibility check

C++ generator honors the `zserio_compatibility_version` specified in the schema. However note that only
the version specified in the root package of the schema is taken into account. The generator checks that
language features used in the schema are still encoded in a binary compatible way with the specified
compatibility version and fires an error when it detects any problem.

> Note: Binary encoding of packed arrays has been changed in version `2.5.0` and thus versions `2.4.x` are
binary incompatible with later versions.
