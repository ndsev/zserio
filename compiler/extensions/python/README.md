# Python Generator for Zserio

Zserio extension which generates Python serialization API from the Zserio schema.

For a **quick start** see the [Python Tutorial](https://github.com/ndsev/zserio-tutorial-python#zserio-python-quick-start-tutorial).

For an **API documentation** see the [Python Runtime Library](https://zserio.org/doc/runtime/latest/python).

## Supported Python Version

Zserio Python generator supports the Python 3.8, 3.9, 3.10 and 3.11.

Although newer Python versions are not tested, they should work as well as long as they are backward compatible.

## Auto-generated API helper

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

## PEP-8 compliant API

Python generator generates the serialization API according to the
[PEP-8](https://www.python.org/dev/peps/pep-0008/). It means that the generator must rename symbols defined
in the Zserio schema to conform PEP-8 style guide. However such renaming can introduce symbol clashing which
could not be detected by the Zserio core. Therefore the Python generator must do its own checks and can cause
generation failure in case that it detects some clashing.

### Possible clashing

1. Names of generated files for package symbols (structures, choices, constants, etc.) -
   i.e. Python module names:
    * may clash with names of generated Python packages defined in the Zserio schema which causes problems to
      Python import system which cannot distinguish easily between module and package with the same name
    * may clash with other modules generated for another package symbols (both `SomeStruct` and `Some_Struct`
      are generated like `some_struct.py` module)
    * may clash with auto-generated `api.py`

2. Names of symbols in a scope (field names, enum item names, function names, etc.):
    * may clash between each other (both `some_field` and `someField` are generated like `some_field` Python
      property)
    * may clash with some of the generated API methods - e.g. `read`, `write`, `bitsizeof`, etc.
    * may clash with some private member or reserved symbol - for simplicity, Python generator forbids
     identifiers starting with underscore (`_`)

3. Top level package name
    * top level package name should be carefully chosen since Python import system is not much robust -
      e.g. all the packages and modules which are imported during Python startup are set in `sys.modules` and
      it isn't possible to import another package with the same name, at least not using classic import syntax
    * since it's not clear which packages/modules are imported during Python startup
      (and moreover the list probably isn't fixed across Python versions), the Python Generator doesn't try
      to detect such clashes and it's up to user to choose a safe name
    * in case that the top level package name cannot be change, user can use `-setTopLevelPackage` Zserio option
      which allows to set an additional top level package
    * Python Generator only checks for possible clashes with packages/modules which the generated serialization
      API uses, which is currently only `zserio` and `typing`

## Compatibility check

Python generator honors the `zserio_compatibility_version` specified in the schema. However note that only
the version specified in the root package of the schema is taken into account. The generator checks that
language features used in the schema are still encoded in a binary compatible way with the specified
compatibility version and fires an error when it detects any problem.

> Note: Binary encoding of packed arrays has been changed in version `2.5.0`.
