# Zserio Compiler User Guide

This document is a user guide for users and developers of the Zserio compiler tool. It will aid the user
in usage of the Zserio tool.

The following subjects are covered:

[Zserio Command Line Interface](#zserio-command-line-interface)

[Zserio Command Line Interface Examples](#zserio-command-line-interface-examples)

[Zserio Ant Task](#zserio-ant-task)

[Zserio Ant Task Examples](#zserio-ant-task-examples)

## Zserio Command Line Interface

Zserio compiler can be run using jar package `zserio.jar`. This "all-in-one" jar package contains Zserio core
compiler packed together with all available extensions (for example, C++ and Java generators) and 3rd party
libraries.

The following shows Zserio compiler command line syntax:

```
java -jar zserio.jar
    [-allowImplicitArrays]
    [-cpp <output directory>]
    [-doc <output directory>]
    [-h,--help <[topic]>]
    [-ignoreTimestamps]
    [-java <output directory>]
    [-python <output directory>]
    [-setCppAllocator <allocator>]
    [-setDotExecutable <dotExec>]
    [-setTopLevelPackage <package>]
    [-src <source directory>]
    [-v,--version]
    [-withCrossExtensionCheck|-withoutCrossExtensionCheck]
    [-withGlobalRuleIdCheck|-withoutGlobalRuleIdCheck]
    [-withPubsubCode|-withoutPubsubCode]
    [-withRangeCheckCode|-withoutRangeCheckCode]
    [-withReflectionCode|-withoutReflectionCode]
    [-withServiceCode|-withoutServiceCode]
    [-withSourcesAmalgamation|-withoutSourcesAmalgamation]
    [-withSqlCode|-withoutSqlCode]
    [-withSvgDiagrams|-withoutSvgDiagrams]
    [-withTypeInfoCode|-withoutTypeInfoCode]
    [-withWarnings|-withoutWarnings <warning[,warning]*>]
    [-withValidationCode|-withoutValidationCode]
    [-withWriterCode|-withoutWriterCode]
    [-xml <output directory>]
    <input file>
```

**`<input_file>`**

It is an absolute or relative file name for the top-level Zserio package to be parsed. If this package
contains imports, e.g. `import foo.bar.*`, Zserio will convert this package name to a relative path name
and try to read the imported package from `foo\bar.zs`.

**`-allowImplicitArrays`**

Allows implicit arrays in zserio language to be compatible with old schemas. Implicit arrays are disabled
by default because they are deprecated and they will removed in the future.

**`-cpp`**

Zserio will generate C++ API into a given output directory.

**`-doc`**

Zserio will generate HTML documentation into a given output directory.

**`-h, --help`**

Shows all supported command line options with their description.

Optionally specify one of the following topics for detailed description: warnings.

**`-ignoreTimestamps`**

Switches Zserio to ignore timestamps and thus forces it to always regenerate output.

**`-java`**

Zserio will generate Java API into a given output directory.

**`-python`**

Zserio will generate Python API into a given output directory.

**`-setCppAllocator`**

Sets the C++ allocator type to be used in generated code. Possible values: `std` (default), `polymorphic`.

`std` stands for `std::allocator<>` class implemented in standard C++ library.
`polymorphic` stands for `zserio::pmr::PropagatingPolymorphicAllocator<>` class implemented in
Zserio C++ runtime library.

**`-setDotExecutable`**

Sets path to the executable for conversion of dot files to svg format. If this option is omitted, the `dot`
executable is used and it is supposed that this executable is on the system path.

> `-setDotExecutable /usr/bin/dot` causes to run executable `/usr/bin/dot` whenever the conversion
> of dot file to svg file is needed.

**`-setTopLevelPackage`**

Sets the top level package for generated Java sources and top level namespace for generated C++ sources.

> Parameter `-setTopLevelPackage appl.Zserio` forces all generated Java sources to be in the package
> `appl.Zserio` and all generated C++ sources to be in the namespace `appl::Zserio`.

**`-src`**

Defines the root directory for the input file and all imported packages. If this option is missing, the default
value is the current working directory. Currently, only one source directory can be specified. A list of
directories as in the Java `CLASSPATH` is not supported.

> If the source path is `C:\zserio` and the input file is `com\acme\foo.zs`, Zserio will try parsing
> `C:\zserio\com\acme\foo.zs`. If `foo.zs` contains the declaration `import com.acme.bar.*`, Zserio will
> try parsing `C:\zserio\com\acme\bar.zs`.

**`-v`, `--version`**

Shows the version of the Zserio tool.

**`-withCrossExtensionCheck|-withoutCrossExtensionCheck`**

Enables/disables cross extension check, which causes that the checking phase is executed for all available
extensions. By default is enabled to simplify to write portable schemas.

**`-withGlobalRuleIdCheck|-withoutGlobalRuleIdCheck`**

Enables/disables the checking of rule id uniqueness in zserio language between all packages (globally).
If it is disabled, the rule id uniqueness is checked only within a package. By default is disabled.

**`-withPubsubCode|-withoutPubsubCode`**

Enables/disables generation of code for Pubsub Types. By default is enabled, but note that pubsub types can be
enabled only when writer code is enabled (see `-withWriterCode` option).

**`-withRangeCheckCode|-withoutRangeCheckCode`**

Enables/disables code for range checking for fields and parameters (integer types only). By default is disabled.
Note that range checking can be enabled only when writer code is enabled (see `-withWriterCode` option).

**`-withReflectionCode|-withoutReflectionCode`**

Enables/disables generation of reflection code. By default is disabled. Note that reflection code can be
enabled only when generation of type information code is enabled (see `-withTypeInfoCode` option) and when
writer code is enabled (see `-withWriterCode` option).

> This parameter is currently supported by C++ generator only.

**`-withServiceCode|-withoutServiceCode`**

Enables/disables generation of code for Service Types. By default is enabled, but note that services can be
enabled only when writer code is enabled (see `-withWriterCode` option).

**`-withSourcesAmalgamation|-withoutSourcesAmalgamation`**

Enables/disables amalgamation of generated C++ sources. When amalgamation is enabled (default behavior),
C++ sources will be automatically amalgamated to speed up C++ compilation time. C++ sources generated in
different subdirectories will be amalgamated separately. Thus, if amalgamation is enabled, each generated
subdirectory will contain only one C++ source module.

**`-withSqlCode|-withoutSqlCode`**

Enables/disables generation of code for SQLite extension (SQLite types like `sql_database`, `sql_table`,
etc...). By default is enabled.

**`-withSvgDiagrams|-withoutSvgDiagrams`**

Enables/disables automatic conversion of the generated dot files to the svg file format. The conversion to the
svg files is done by calling of the external dot tool executable. Therefore this dot executable must be
available on system path or must be defined by `-setDotExecutable` option. By default is disabled. The dot
executable is a part of the Graphviz package which can be downloaded from
[Graphviz Web Page](http://www.graphviz.org/download).

**`-withTypeInfoCode|-withoutTypeInfoCode`**

Enables/disables generation of type information code. By default is disabled.

**`-withWarnings|-withoutWarnings`**

Allows to enable/disable specific warnings. Use '--help warnings' for detailed description.

**`-withValidationCode|-withoutValidationCode`**

Enables/disables generation of the API extension, which is used for SQLite database validation. Currently,
database validation is not supported by C++ API. By default is disabled. Note that validation code can be
enabled only when writer code is enabled (see `-withWriterCode` option).

**`-withWriterCode|-withoutWriterCode`**

Enables/disables generation of the API writing interface extension. This extension allows writing data to the
bit stream or to the SQLite database. By default is enabled.

**`-xml`**

When present, Zserio will dump an XML representation of the syntax tree of all input files into the
specified directory.

[top](#zserio-compiler-user-guide)

## Zserio Command Line Interface Examples

The following command compiles Zserio source file `zserio/test.zs` located in directory `sources` and generates
the Java API into directory `api/java`:

`java -jar zserio.jar -java api/java -src sources zserio/test.zs`

The following command compiles Zserio source file `zserio/test.zs` located in directory `sources` and generates
HTML documentation into directory `html`:

`java -jar zserio.jar -doc html -src sources zserio/test.zs`

The following commands command compiles Zserio source file `zserio/test.zs` located in `sources` and
generates:

- Java API into directory `api/java`,
- C++ API into directory `api/cpp`,
- Python API into directory `api/python`,
- HTML documentation into directory `html`,
- XML syntax tree of all input files into directory `xml`.

`java -jar zserio.jar -java api/java -cpp api/cpp -python api/python -doc html -xml xml -src sources zserio/test.zs`

[top](#zserio-compiler-user-guide)

## Zserio Warnings Subsystem

Zserio provides possibility to configure warnings on command line. Each
warning has it's own specifier (i.e. tag) which can be used to either
enable or disable the particular warning.

Options `-withWarnings` and `-withoutWarnings` can be combined. When
warnings options groups are used, more generic groups are applied first
so that it is possible to enable all warnings in a group and then
disable some smaller set of warnings or just a single warning.

### List of Warnings
- `all`
  Controls all warnings at once.
- `choice-unhandled-enum-item`
  Warn when a choice with enumeration selector does not handle some of
  the enumeration items. Enabled by default.
- `default-instantiation`
  Warn about template instantiations which are not instantiated using
  instantiate keyword. Disabled by default.
- `doc-comment-format`
  Warn when a documentation comment has invalid format. Enabled by
  default.
- `doc-comment-see`
  Warn when a documentation see tag contains invalid symbol reference.
  Enabled by default.
- `doc-comment-unused`
  Warn when a documentation comment is not used - i.e. cannot be
  assigned to any documentable element. Enabled by default.
- `encoding`
  Warn when a source file is not in UTF-8 encoding or when it contains
  non-printable characters. Enabled by default.
- `import`
  Warn when some imports are duplicated or overrides other imports.
  Enabled by default.
- `optional-field-reference`
  Warn when an expression contains a reference to an optional field,
  while the owner of the expression has no or inconsistent optional
  condition. Enabled by default.
- `timestamp`
  Warn when timestamp of a source or a resource (on a class path) cannot
  be retrieved. Enabled by default.
- `sql-primary-key`
  Warn when a problem with primary key is detected in a SQL table. This
  can happen in various cases - no primary key, duplicated primary key,
  etc. Enabled by default.
- `unpackable-array`
  Warn when a packed array is used on arrays of unpackable elements. Can
  be fired either for arrays of unpackable simple types (e.g. string) or
  for arrays of compounds which contain only unpackable types. Enabled
  by default.
- `unused`
  Warn about defined, but unused types. Disabled by default.


## Zserio Ant Task

The Zserio release distribution also includes a custom Ant task. It can be found in folder `ant_task` inside
the binary distribution archive file as `zserio_ant.jar` file.

To use the Ant task, it needs to be defined first in Ant configuration file `build.xml`. The following command
can be used for Zserio Ant task definition:

`<taskdef name="zserio" classpath="zserio_ant.jar" classname="zserio.ant.ZserioTask"/>`

From that point, you can use the task `zserio`. The usage is:

```
<zserio attributes… >
    child nodes…
</zserio>
```

The following table shows all supported Zserio task attributes:

Zserio Task Attribute   | Description
----------------------- | -------------------------------------------------------------------------------------
`srcPath="path"`        | Path to source files. Required.
`srcFile="file"`        | Input file, relative to `srcPath`. Required.
`ignoreError="boolean"` | When set to `true`, compilation error will be ignored and not reported to Ant. Default is `false`.

The next section describes all supported Zserio task child nodes:

```
<arg name="name" value="value"/>
```

Adds a custom option of the form `-name=value` or `-name` if value is not specified. This node can be used

multiple times.

```
<classpath>
    path-like-structure
</classpath>
```

Classpath used to locate Zserio Tool. It must include `zserio.jar` or `zserio_core.jar` and all dependent
extensions (like `zerio_java.jar`) and 3rd party libraries (like `antlr.jar`).

[top](#zserio-compiler-user-guide)

## Zserio Ant Task Examples

The following demonstrates the usage of Zserio Ant task:

```
<zserio srcPath="my_in_dir" srcFile="my.zs">
    <arg name="withRangeCheckCode"/>
    <arg name="java" value="my_out_dir"/>
    <classpath>
        <path refid="my_classpath"/>
    </classpath>
</zerio>
```

This command calls Zserio tool to compile source file `my.zs`, located in directory `my_in_dir` and
generates the Java API including strict range checking into directory `my_out_dir`.

[top](#zserio-compiler-user-guide)
