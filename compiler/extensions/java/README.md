# Java Generator for Zserio

Zserio extension generates Java [serialization API](#serialization-api) from the Zserio schema together
with [additional API](#additional-api).

The generated code must be always used with [Java Runtime Library](https://zserio.org/doc/runtime/latest/java)
which provides functionality common for all generated code.

For a **quick start** see the [Java Tutorial](https://github.com/ndsev/zserio-tutorial-java#zserio-java-quick-start-tutorial).

## Content

[Supported Java Versions](#supported-java-versions)

[Serialization API](#serialization-api)

&nbsp; &nbsp; &nbsp; &nbsp; [Subtypes](#subtypes)

[Additional API](#additional-api)

&nbsp; &nbsp; &nbsp; &nbsp; [Range Check](#range-check)

&nbsp; &nbsp; &nbsp; &nbsp; [Validation](#validation)

&nbsp; &nbsp; &nbsp; &nbsp; [Code Comments](#code-comments)

&nbsp; &nbsp; &nbsp; &nbsp; [Type Information](#type-information)

&nbsp; &nbsp; &nbsp; &nbsp; [JSON Debug String](#json-debug-string)

[Compatibility Check](#compatibility-check)

[Optimizations](#optimizations)

## Supported Java Versions

Zserio Java generator supports the Java SE 8 (LTS), the Java SE 11 (LTS) and the Java SE 17 (LTS).

Although newer Java versions are not tested, they should work as well as long as they are backward compatible.

## Serialization API

The serialization API provides the following features for all Zserio structures, choices and unions:

- Serialization of all Zserio objects to the bit stream
  (method [`zserio.runtime.io.SerializeUtil.serialize()`](https://zserio.org/doc/runtime/latest/java/zserio/runtime/io/SerializeUtil.html)).
- Deserialization of all Zserio objects from the bit stream
  (method [`zserio.runtime.io.SerializeUtil.deserialize()`](https://zserio.org/doc/runtime/latest/java/zserio/runtime/io/SerializeUtil.html)).
- Getters and setters for all fields
- Method `bitSizeOf()` which calculates a number of bits needed for serialization of the Zserio object.
- Method `equals()` which compares two Zserio objects field by field.
- Method `hashCode()` which calculates a hash code of the Zserio object.

### Subtypes

Because Java language does not support aliases for types, Zserio subtypes in Java are resolved during
API generation (resolved means that generated Java API uses always original type directly).

However, there are some use cases where it would be beneficial to have a generated Java class for
subtype types of compounds, e.g. to parse compound type which is given by a subtype class name.

Therefore, for each subtype of compound, a Java class is generated which inherits the base type. These generated
subtype classes are not used by generated code and are just meant to support applications.

> Note: Subtypes of primitive types are not reflected in generated code at all. This is because simple types
  are unboxed and other built-in types (e.g. String) are final (i.e. not possible to inherit).

## Additional API

The following additional API features which are disabled by default, are available for users:

- [Range Check](#range-check) - Generation of code for the range checking for fields and parameters (integer types only).
- [Validation](#validation) - Generation of code which is used for SQLite databases validation.
- [Code Comments](#code-comments) - Generation of Javadoc comments in code.
- [Type Information](#type-information) - Generation of static information about Zserio objects like schema names, types, etc.
- [JSON Debug String](#json-debug-string) - Supports export/import of all Zserio objects to/from the JSON file.

All of these features can be enabled using command line options which are described in the
[Zserio User Guide](../../../doc/ZserioUserGuide.md#zserio-command-line-interface) document.

### Range Check

Because not all Zserio integer types can be directly mapped to the Java types (e.g. `bit:4` is mapped to
`byte`), it can be helpful to explicitly check values stored in Java types for the correct ranges
(e.g to check if `byte` value which holds `bit:4`, is from range `<0, 15>`). Such explicit checks allow
throwing exception with the detailed description of the Zserio field with wrong value.

The range check code is generated only in the setters method directly before the field is set.

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

The code comments generate Javadoc comments for all generated Zserio objects. Some comments available
in Zserio schema are used as well.

### Type Information

The type information generates static method `typeInfo()` in all generated Zserio types (except of Zserio
subtypes). This method returns all static information of Zserio type which is available in the Zserio schema
(e.g. schema name, if field is optional, if field is array, etc...).

### JSON Debug String

JSON debug string feature provides export and import to/from JSON string for all Zserio structures,
choices and unions:

- Export to the JSON string
  (method [`zserio.runtime.DebugStringUtil.toJsonString()`](https://zserio.org/doc/runtime/latest/java/zserio/runtime/DebugStringUtil.html)).
- Import from the JSON string
  (method [`zserio.runtime.DebugStringUtil.fromJsonString()`](https://zserio.org/doc/runtime/latest/java/zserio/runtime/DebugStringUtil.html)).

> Note that this feature is available only if type information is enabled!

## Compatibility check

Java generator honors the `zserio_compatibility_version` specified in the schema. However note that only
the version specified in the root package of the schema is taken into account. The generator checks that
language features used in the schema are still encoded in a binary compatible way with the specified
compatibility version and fires an error when it detects any problem.

> Note: Binary encoding of packed arrays has been changed in version `2.5.0` and thus versions `2.4.x` are
binary incompatible with later versions.

## Optimizations

The Java generator provides the following optimizations of the generated code:

- If any Zserio structure, choice or union type is not used in the packed array, no packing interface methods
  will be generated for them
  (e.g. `write(zserio.runtime.array.PackingContext context, zserio.runtime.io.BitStreamWriter out)`).

Such optimizations can be done because Zserio relays on the fact that the entire schema is known during the
generation. Therefore, splitting schema into two parts and generating them independently cannot guarantee
correct functionality. This can lead to a problem especially for templates, if a template is defined
in one part and instantiated in the other.
