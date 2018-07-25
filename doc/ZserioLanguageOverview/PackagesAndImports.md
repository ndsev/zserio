## Packages and Imports

Complex zserio specifications should be split into multiple packages stored in separate source files. Every
user-defined type belongs to a unique package. For backward compatibility, there is an unnamed default package
used for files without an explicit package declaration. It is strongly recommended to use a package declaration
in each zserio source file.

A package provides a lexical scope for types. Type names must be unique within a package, but a given type name
may be defined in more than one package. If a type named `Coordinate` is defined in package `com.acme.foo`, the
type can be globally identified by its fully qualified name `com.acme.foo.Coordinate`, which is obtained by
prefixing the type name with the name of the defining package, joined by a dot. Another package
`com.acme.foo.bar` may also define a type named `Coordinate`, having the fully qualified name
`com.acme.bar.Coordinate`.

By default, types from other packages are not visible in the current package, unless there are imported
explicitly. The package and import syntax and semantics follow the Java example.

**Example**

```
package map;

import common.geometry.*;
import common.featuretypes.*;
```

Import declarations only have any effect when there is a reference to a type name not defined in the current
package. If package map defines its own `Coordinate` type, any reference to that within package `map` will be
resolved to the local type `map.Coordinate`, even when one or more of the imported packages also define a type
named `Coordinate`.

On the other hand, if package `map` references a `Coordinate` type but does not define it, the import
declarations are used to resolve that type in one of the imported packages. In that case, the referenced type
must be matched by exactly one of the imported packages. It is obviously a semantic error if the type name is
defined in none of the packages. It is also an error if the type name is defined in two or more of the imported
packages. The order of the import declarations does not matter.

It is always possible to use the fully qualified name of a type, e.g. `com.acme.bar.Coordinate`. This makes it
possible to import a type with the same name (e.g. `Coordinate`) from more than one package or to import a type
with the same name as a type defined locally.

Individual types can be imported using their fully qualified name:

```
import common.geometry.Geometry;
```

This single import has precedence over any wildcard import. It prevents an ambiguity with
`common.featuretypes.Geometry`. It is possible to import the same type name from different packages but then
each usage of such type must be fully qualified. Using the unqualified type name in this situation results in
a compilation error as the type is ambiguous.

### Packages and Files

Package and file names are closely related. Each package must be located in a separate file. The above example
declares a package `map` stored in a source file `map.zs`. The import declarations direct the parser to locate
and parse source files `common/geometry.zs` and `common/featuretypes.zs`.

Imported files may again contain import declarations. Cyclic import relations between packages are supported
but should be avoided. The zserio parser takes care to parse each source file just once.

[\[top\]](ZserioLanguageOverview.md#language-guide)
