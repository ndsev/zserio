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
    [-cpp <output directory>]
    [-doc <output directory>]
    [-h,--help]
    [-java <output directory>]
    [-javaVersion <version>]
    [-python <output directory>]
    [-setDotExecutable <dotExec>]
    [-setDotLinksPrefix <prefix>]
    [-setTopLevelPackage <package>]
    [-showAst]
    [-showDocAst <input file>]
    [-src <source directory>]
    [-v,--version]
    [-withInspectorCode|-withoutInspectorCode]
    [-withGrpcCode|-withoutGrpcCode]
    [-withRangeCheckCode|-withoutRangeCheckCode]
    [-withSourcesAmalgamation|-withoutSourcesAmalgamation]
    [-withSqlCode|-withoutSqlCode]
    [-withSvgDiagrams|-withoutSvgDiagrams]
    [-withValidationCode|-withoutValidationCode]
    [-withWriterCode|-withoutWriterCode]
    [-withUnusedWarnings|-withoutUnusedWarnings]
    [-xml <output directory>]
    <input file>
```

**`<input_file>`**

It is an absolute or relative file name for the top-level Zserio package to be parsed. If this package
contains imports, e.g. `import foo.bar.*`, Zserio will convert this package name to a relative path name
and try to read the imported package from `foo\bar.zs`.

**`-cpp`**

Zserio will generate C++ API into a given output directory.

**`-doc`**

Zserio will generate HTML documentation into a given output directory.

**`-h, --help`**

Shows all supported command line options with their description.

**`-java`**

Zserio will generate Java API into a given output directory.

**`-javaVersion`**

Specifies Java version for which Zserio will generate Java API. If omitted, Zserio will generate Java API
for version which is run during Zserio compilation. Java version 6 is the lowest supported Java version for
which Zserio is able to generate sources.

> Parameter `-javaVersion 1.6` or `-javaVersion 6` specifies Java version 6.

**`-python`**

Zserio will generate Python API into a given output directory.

**`-setDotExecutable`**

Sets path to the executable for conversion of dot files to svg format. If this option is omitted, the `dot`
executable is used and it is supposed that this executable is on the system path.

> `-setDotExecutable /usr/bin/dot` causes to run executable `/usr/bin/dot` whenever the conversion
> of dot file to svg file is needed.

**`-setDotLinksPrefix`**

Sets the prefix to all URL links which are generated in graphviz file `overview.dot`. Setting the prefix
to "`.`" means setting the URL links to the locally generated HMTL documentation (which is the default behavior
if this option is omitted).

> Parameter `-setDotLinksPrefix https://my.web-site.org/zserio/html_doc` sets the URL links
> to `my.web-site.org` web site.

**`-setTopLevelPackage`**

Sets the top level package for generated Java sources and top level namespace for generated C++ sources.

> Parameter `-setTopLevelPackage appl.Zserio` forces all generated Java sources to be in the package
> `appl.Zserio` and all generated C++ sources to be in the namespace `appl::Zserio`.

**`-showAst`**

Shows the generated ANTLR Abstract Syntax Tree (AST) after compilation.

**`-showDocAst`**

Shows the generated ANTLR Abstract Syntax Tree (AST) for documentation comment stored in the specified text
input file.

**`-src`**

Defines the root directory for the input file and all imported packages. If this option is missing, the default
value is the current working directory. Currently, only one source directory can be specified. A list of
directories as in the Java `CLASSPATH` is not supported.

> If the source path is `C:\zserio` and the input file is `com\acme\foo.zs`, Zserio will try parsing
> `C:\zserio\com\acme\foo.zs`. If `foo.zs` contains the declaration `import com.acme.bar.*`, Zserio will try
> parsing `C:\zserio\com\acme\bar.zs`.

**`-v`, `--version`**

Shows the version of the Zserio tool.

**`-withInspectorCode|-withoutInspectorCode`**

Enables/disables generation of the C++ API extension which can be used for Zserio data inspection. Currently,
extension for Zserio data inspection is not supported by Java API. By default is disabled.

**`-withGrpcCode|-withoutGrpcCode`**

Enables/disables generation of code for [GPRC](https://grpc.io/) services. By default is enabled. 
Java is based on release [v1.14.0](https://github.com/grpc/grpc-java/releases/tag/v1.14.0).
C++ is based on release [v1.14.1](https://github.com/grpc/grpc/releases/tag/v1.14.1).
Python is based on `grpcio` module release [v1.17.1](https://github.com/grpc/grpc/releases/tag/v1.17.1).

**`-withRangeCheckCode|-withoutRangeCheckCode`**

Enables/disables code for range checking for fields and parameters (integer types only). By default is disabled.

**`-withSourcesAmalgamation|-withoutSourcesAmalgamation`**

Enables/disables amalgamation of generated C++ sources. When amalgamation is enabled (default behavior),
C++ sources will be automatically amalgamated to speed up C++ compilation time. C++ sources generated in
different subdirectories will be amalgamated separately. Thus, if amalgamation is enabled, each generated
subdirectory will contain only one C++ source module.

**`-withSqlCode|-withoutSqlCode`**

Enables/disables generation of code for SQLite extension (SQLite types like `sql_database`, `sql_table`,
etc...).

**`-withSvgDiagrams|-withoutSvgDiagrams`**

Enables/disables automatic conversion of the generated dot files to the svg file format. The conversion to the
svg files is done by calling of the external dot tool executable. Therefore this dot executable must be
available on system path or must be defined by `-setDotExecutable` option. By default is disabled. The dot
executable is a part of the Graphviz package which can be downloaded from
[Graphviz Web Page](http://www.graphviz.org/download).

**`-withValidationCode|-withoutValidationCode`**

Enables/disables generation of the API extension, which is used for SQLite database validation. Currently,
database validation is not supported by C++ API. By default is disabled.

**`-withWriterCode|-withoutWriterCode`**

Enables/disables generation of the C++ and Java API writing interface extension. This extension allows
writing data to the bit stream or to the SQLite database. By default is enabled.

**`-withUnusedWarnings|-withoutUnusedWarnings`**

Enables/disables warnings for unused types. By default is disabled.

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
`clean="boolean"`       | When set to `true`, all outputs will be removed at first. It requires output child node which specifies what to delete. Default is `false`.
`ignoreError="boolean"` | When set to `true`, compilation error will be ignored and not reported to Ant. Default is `false`.
`java="outDir"`         | Generates Java sources in `outDir`.
`cpp="outDir"`          | Generates C++ sources in `outDir`.
`python="outDir"`       | Generates Python sources in `outDir`.
`doc="outDir"`          | Generates HTML documentation in `outDir`.
`xml="outDir"`          | Generates XML representation of the input in `outDir`.

The next section describes all supported Zserio task child nodes:

```
<arg name="name" value="value"/>
```

Adds a custom option of the form `-name=value` or `-name` if value is not specified. This node can be used

multiple times.

```
<dependencies>
    path-like-structure
</dependencies>
```

Specifies dependencies. This is used only to avoid generating the output files if they have been generated
already and all the dependencies are older than all the outputs. When omitted or empty, output files are
always generated.

```
<output>
    path-like-structure
</output>
```

Specifies output files. This is used to avoid generating the output files if they have been generated already
and all the dependencies are older than all the outputs. When omitted or empty, output files are always
generated. Furthermore the list specifies what files to delete when clean attribute is set to true.

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
<zserio srcPath="my_in_dir" srcFile="my.zs" java="my_out_dir">
    <arg name="withRangeCheckCode"/>
    <output>
        <fileset dir="my_out_dir" includes="**/*.java" erroronmissingdir="false"/>
    </output>
    <classpath>
        <path refid="rds.my_classpath"/>
    </classpath>
</zerio>
```

This command calls Zserio tool to compile source file `my.zs`, located in directory `my_in_dir` and
generates the Java API including strict range checking into directory `my_out_dir`.

[top](#zserio-compiler-user-guide)
