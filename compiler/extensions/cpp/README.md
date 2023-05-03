# C++ Generator for Zserio

Zserio extension which generates C++ serialization API from the Zserio schema.

For a **quick start** see the [C++ Tutorial](https://github.com/ndsev/zserio-tutorial-cpp#zserio-c-quick-start-tutorial).

For an **API documentation** see the [C++ Runtime Library](https://zserio.org/doc/runtime/latest/cpp).

## Compatibility check

C++ generator honors the `zserio_compatibility_version` specified in the schema. However note that only
the version specified in the root package of the schema is taken into account. The generator checks that
language features used in the schema are still encoded in a binary compatible way with the specified
compatibility version and fires an error when it detects any problem.

> Note: Binary encoding of packed arrays has been changed in version `2.5.0` and thus versions `2.4.x` are
binary incompatible with later versions.
