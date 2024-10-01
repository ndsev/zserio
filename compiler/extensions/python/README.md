# Python Generator for Zserio

Zserio extension generates Python [serialization API](#serialization-api) from the Zserio schema together
with [additional API](#additional-api).

The generated code must be always used with
[Python Runtime Library](https://zserio.org/doc/runtime/latest/python) which provides functionality common for
all generated code.

For a **quick start** see the [Python Tutorial](https://github.com/ndsev/zserio-tutorial-python#zserio-python-quick-start-tutorial).

## Content

[Supported Python Versions](#supported-python-versions)

[Serialization API](#serialization-api)

&nbsp; &nbsp; &nbsp; &nbsp; [Auto-generated API helper](#auto-generated-api-helper)

&nbsp; &nbsp; &nbsp; &nbsp; [PEP-8 compliant API](#pep-8-compliant-api)

&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; [Possible clashing](#possible-clashing)

[Additional API](#additional-api)

&nbsp; &nbsp; &nbsp; &nbsp; [Range Check](#range-check)

&nbsp; &nbsp; &nbsp; &nbsp; [Code Comments](#code-comments)

&nbsp; &nbsp; &nbsp; &nbsp; [Type Information](#type-information)

&nbsp; &nbsp; &nbsp; &nbsp; [JSON Debug String](#json-debug-string)

[Compatibility Check](#compatibility-check)

[Optimizations](#optimizations)

## Supported Python Versions

Zserio Python generator supports the Python 3.8, 3.9, 3.10, 3.11 and 3.12.

Although newer Python versions are not tested, they should work as well as long as they are backward compatible.

## Serialization API

The serialization API provides the following features for all Zserio structures, choices and unions:

- Serialization of all Zserio objects to the bit stream
  (method [`zserio.serialization.serialize()`](https://zserio.org/doc/runtime/latest/python/zserio.serialization.html#module-zserio.serialization)).
- Deserialization of all Zserio objects from the bit stream
  (method [`zserio.serialization.deserialize()`](https://zserio.org/doc/runtime/latest/python/zserio.serialization.html#module-zserio.serialization)).
- Properties for all fields to get and set values
- Method `bitsizeof()` which calculates a number of bits needed for serialization of the Zserio object.
- Method `__eq__()` which compares two Zserio objects field by field.
- Method `__hash__()` which calculates a hash code of the Zserio object.

### Auto-generated API helper

Python Generator generates each package symbol in its own Python module (i.e. file). It's necessary in order
to prevent problems with Python symbols dependencies. Zserio allows dependencies which are defined after the
reference and it's not possible in Python. There are also problems with using `__init__.py` due to circular
imports. In order to keep things simple, the Python generator provides its own way how to make imports user
friendly.

The generator provides `api.py` files generated on each level of the generated source tree. The top level `api.py`
recursively imports all underlying `api.py` files and modules defined on particular tree level. Therefore it
provides an easy way how to make accessible all the generated classes by a single import. Moreover, all the
generated classes are accessible via the very same path as they are defined in Zserio.

Consider the following Zserio schema layout:
```
my_package
└───sub_package.zs (contains `struct TestStructure`)
└───other_package.zs (contains `union TestUnion`)
```

All generated classes can be used via the single import:
```
import my_package.api as my_package

testStructure = my_package.sub_package.TestStructure()
testUnion = my_package.other_package.TestUnion()
```

### PEP-8 compliant API

Python generator generates the serialization API according to the
[PEP-8](https://www.python.org/dev/peps/pep-0008/). It means that the generator must rename symbols defined
in the Zserio schema to conform PEP-8 style guide. However such renaming can introduce symbol clashing which
could not be detected by the Zserio core. Therefore the Python generator must do its own checks and can cause
generation failure in case that it detects some clashing.

#### Possible clashing

1. Names of generated files for package symbols (structures, choices, constants, etc.) -
   i.e. Python module names:
   - may clash with names of generated Python packages defined in the Zserio schema which causes problems to
     Python import system which cannot distinguish easily between module and package with the same name
   - may clash with other modules generated for another package symbols (both `SomeStruct` and `Some_Struct`
     are generated like `some_struct.py` module)
   - may clash with auto-generated `api.py`

2. Names of symbols in a scope (field names, enum item names, function names, etc.):
   - may clash between each other (both `some_field` and `someField` are generated like `some_field` Python
     property)
   - may clash with some of the generated API methods - e.g. `read`, `write`, `bitsizeof`, etc.
   - may clash with some private member or reserved symbol - for simplicity, Python generator forbids
     identifiers starting with underscore (`_`)

3. Top level package name
   - top level package name should be carefully chosen since Python import system is not much robust -
     e.g. all the packages and modules which are imported during Python startup are set in `sys.modules` and
     it isn't possible to import another package with the same name, at least not using classic import syntax
   - since it's not clear which packages/modules are imported during Python startup
     (and moreover the list probably isn't fixed across Python versions), the Python Generator doesn't try
     to detect such clashes and it's up to user to choose a safe name
   - in case that the top level package name cannot be change, user can use `-setTopLevelPackage` Zserio option
     which allows to set an additional top level package
   - Python Generator only checks for possible clashes with packages/modules which the generated serialization
     API uses, which is currently only `zserio` and `typing`

## Additional API

The following additional API features which are disabled by default, are available for users:

- [Range Check](#range-check) - Generation of code for the range checking for fields and parameters (integer types only).
- [Code Comments](#code-comments) - Generation of Javadoc comments in code.
- [Type Information](#type-information) - Generation of static information about Zserio objects like schema names, types, etc.
- [JSON Debug String](#json-debug-string) - Supports export/import of all Zserio objects to/from the JSON file.

All of these features can be enabled using command line options which are described in the
[Zserio User Guide](../../../doc/ZserioUserGuide.md#zserio-command-line-interface) document.

### Range Check

Because not all Zserio integer types can be directly mapped to the Python types (e.g. `bit:4` is mapped to
`int`), it can be helpful to explicitly check values stored in Java types for the correct ranges
(e.g to check if `int` value which holds `bit:4`, is from range `<0, 15>`). Such explicit checks allow
throwing exception with the detailed description of the Zserio field with wrong value.

The range check code is generated only in the `write()` method directly before the field is written to the
bit stream.

### Code Comments

The code comments generate Sphinx comments for all generated Zserio objects. Some comments available
in Zserio schema are used as well.

### Type Information

The type information generates static method `type_info()` in all generated Zserio types (except of Zserio
subtypes). This method returns all static information of Zserio type which is available in the Zserio schema
(e.g. schema name, if field is optional, if field is array, etc...).

### JSON Debug String

JSON debug string feature provides export and import to/from JSON string for all Zserio structures,
choices and unions:

- Export to the JSON string
  (method [`zserio.debugstring.to_json_string()`](https://zserio.org/doc/runtime/latest/python/zserio.debugstring.html#module-zserio.debugstring)).
- Import from the JSON string
  (method [`zserio.debugstring.from_json_string()`](https://zserio.org/doc/runtime/latest/python/zserio.debugstring.html#module-zserio.debugstring)).

> Note that this feature is available only if type information is enabled!

## Compatibility check

Python generator honors the `zserio_compatibility_version` specified in the schema. However note that only
the version specified in the root package of the schema is taken into account. The generator checks that
language features used in the schema are still encoded in a binary compatible way with the specified
compatibility version and fires an error when it detects any problem.

> Note: Binary encoding of packed arrays has been changed in version `2.5.0`.

## Optimizations

The Python generator provides the following optimizations of the generated code:

- If any Zserio structure, choice or union type is not used in the packed array, no packing interface methods
  will be generated for them (e.g. `write_packed(self, zserio_context, zserio_writer)`).

Such optimizations can be done because Zserio relays on the fact that the entire schema is known during the
generation. Therefore, splitting schema into two parts and generating them independently cannot guarantee
correct functionality. This can lead to a problem especially for templates, if a template is defined
in one part and instantiated in the other.
