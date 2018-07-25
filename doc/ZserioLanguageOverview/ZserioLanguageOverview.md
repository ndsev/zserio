# Zserio 1.0 Language Overview

This document contains a detailed specification of the zserio schema language. The Zserio Language Overview
document is targeted for developers who write zserio schema definitions.

Zserio is a serialization schema language for modeling binary datatypes, bitstreams or file formats. Based
on the zserio language it is possible to automatically generate encoders and decoders for a given schema
in various target languages (e.g. JAVA, C++).

Zserio is similar to other serialization mechanism like
[Google's Protocol Buffers](https://github.com/google/protobuf) but does not use what is called a "wire-format".
Zserio therefore gives full control to the developers and comes with no serialization overhead.
It is a WYSIWYG serialization mechanism.

Zserio also features an extension for SQLite databases. With that extension it is possible to use SQLite
as a backend store for data defined with the zserio language. SQLite tables, columns and BLOBs can be all
described in zserio, giving the developer overall control of the data schema used in SQLite databases.

## Language Guide

[Literals](Literals.md)

[Base Types](BaseTypes.md)

[Constants](Constants.md)

[Enumeration Types](EnumerationTypes.md)

[Compound Types](CompoundTypes.md)

[Array Types](ArrayTypes)

[Alignment and Offsets](AlignmentAndOffsets.md)

[Expressions](Expressions.md)

[Member Access](MemberAccess.md)

[Parametrized Types](ParametrizedTypes.md)

[Subtypes](Subtypes.md)

[Comments](Comments.md)

[Packages and Imports](PackagesAndImports.md)

[SQLite Extension](SqliteExtension.md)

[Quick Reference](ZserioQuickReference.md)

[Background & History](BackgroundAndHistory.md)

[License & Copyright](LicenseAndCopyright.md)
