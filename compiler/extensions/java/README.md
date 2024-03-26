# Java Generator for Zserio

Zserio extension which generates Java serialization API from the Zserio schema.

For a **quick start** see the [Java Tutorial](https://github.com/ndsev/zserio-tutorial-java#zserio-java-quick-start-tutorial).

For an **API documentation** see the [Java Runtime Library](https://zserio.org/doc/runtime/latest/java).

## Supported Java Versions

Zserio Java generator supports the Java SE 8 (LTS), the Java SE 11 (LTS) and the Java SE 17 (LTS).

Although newer Java versions are not tested, they should work as well as long as they are backward compatible.

## Subtypes

Because Java language does not support aliases for types, Zserio subtypes in Java are resolved during
API generation (resolved means that generated Java API uses always original type directly).

However, there are some use cases where it would be beneficial to have a generated Java class for
subtype types of compounds, e.g. to parse compound type which is given by a subtype class name.

Therefore, for each subtype of compound, a Java class is generated which inherits the base type. These generated
subtype classes are not used by generated code and are just meant to support applications.

> Note: Subtypes of primitive types are not reflected in generated code at all. This is because simple types
  are unboxed and other built-in types (e.g. String) are final (i.e. not possible to inherit).

## Compatibility check

Java generator honors the `zserio_compatibility_version` specified in the schema. However note that only
the version specified in the root package of the schema is taken into account. The generator checks that
language features used in the schema are still encoded in a binary compatible way with the specified
compatibility version and fires an error when it detects any problem.

> Note: Binary encoding of packed arrays has been changed in version `2.5.0` and thus versions `2.4.x` are
binary incompatible with later versions.
