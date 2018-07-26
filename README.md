<img src="doc/long.png" height="100">

--------

No time to read? Go to the [Quick Start](#quick-start)

In for the numbers? Head over to [benchmarks](benchmarks/README.md)

Questions? Check the [FAQs](doc/FAQ.md)

------

## Serialization framework

The zserio serialization framework allows you to serialize data in a compact and efficient way.

The key features include

- compactness (smaller than most other serializers)
- advanced schema definition options
- cross-platform
- multiple programming languages


It can be retrofitted on top of almost any other serialization language or model, since it gives the developer powerful low-level access.

It features simple and compound data structures and provides advanced features for controlling at design time what writers will be able to fill in.

Although it does not have a wire format, we have added some convenience keywords lately that encapsulate some functionality. You can find more information on [Zserio Invisibles here](doc/ZserioInvisibles.md)

At the moment the following languages are supported
- C++
- JAVA
- Python (coming soon)


It is still missing some features that we will work on in the next months, which for example include definition of rpc service interfaces or a Python emitter.

## Quick introduction

As we have stated earlier, zserio does not have any wire format. So basically: what you see is what you get. (please note that zserio uses network byte order in the serialized stream, but the generated code does take care of this)

```
package tutorial;

struct Employee
{
  uint8   age;
  string  name;
  uint16  salary;
  Role    role;
};

enum uint8 Role
{
  DEVELOPER = 0,
  TEAM_LEAD = 1,
  CTO       = 2,
}
```

So if we use the schema above and serialize one employee with

- age = 32
- name = John
- salary = 5000 $
- role = DEVELOPER

the resulting byte stream looks like this:

Byte position |Bit position | content / value | comment
----|-------|-------|-----|
0|0 - 7 | 32 (age)| uint8 is of fixed size 8 bit
1|8 - 15| 4 (string length)| string length is encoded in varuint64 field before actual string
2-4|16 - 31| John | UTF-8 encoded string
5-6|32 - 47| 5000 | `uint16` always uses 2 bytes
7 | 48 - 55| 0| enum is of size `uint8` so it uses 1 byte

Please note that in contrast to other serialization mechanisms zserio's variable integers do not provide the full range of values but rather stick to the indicated size. Example: a *varuint64* will be using max 8 bytes whilst not providing the full range of a *uint64_t*.

## Quick Start

To be able to serialize data with zserio, you have to follow these basic steps:

1. Set up your development environment with the zserio runtime
2. Write the schema definition
3. Compile the schema and generate code
4. Serialize/deserialize using the generated code

This Quick Start tutorial features code generation in C++. Go to the [zserio JAVA tutorial](doc/JavaTutorial.md) if you are interested in hands-on JAVA with zserio.

The complete C++ tutorial can be found in a separate repository. There you will also find an additional schema with advanced features and a README discussing those advanced features.


### Installation & Prerequisites

Before we start, make sure you have the following components installed:

- JAVA JRE
- CMake

The easiest way of compiling the schema is to download the latest build of the zserio compiler from [Releases](https://github.com/welovemaps/zserio/releases).

If you want to  build from source, please follow the [Build Instructions for zserio Compiler](doc/zserio-compiler.md).

### Set up dev environment and zserio runtime

We start with a common layout of our project/repo where we put all the source files into a `src` folder and all 3rd party stuff into `3rdparty`. For simplicity the zserio schema file stays in the project's root folder.

Download the latest C++ runtime from [Releases](https://github.com/welovemaps/zserio/releases) and store it into a `runtimes` subfolder in `3rdparty`.

So our folder structure looks like this:
```
.
├───3rdparty
│   └───runtimes
│       └───zserio
│           └───inspector
└───src
```
The CMakeLists.txt of zserio stays in `3rdparty/runtimes`.

In addition to the zserio schema file and the zserio compiler we add the following CMakeLists.txt to the project folder:

```cmake
cmake_minimum_required (VERSION 3.2 FATAL_ERROR)
project (zserio-tutorial)

add_subdirectory (3rdparty/runtimes)
set_property (TARGET ZserioCppRuntime PROPERTY POSITION_INDEPENDENT_CODE ON)

file (GLOB_RECURSE SOURCES_TUTORIAL_API "${CMAKE_CURRENT_SOURCE_DIR}/src/tutorial/*.cpp")
file (GLOB_RECURSE HEADERS_TUTORIAL_API "${CMAKE_CURRENT_SOURCE_DIR}/src/tutorial/*.h")

add_library (tutoriallib STATIC
  ${SOURCES_TUTORIAL_API}
  ${HEADERS_TUTORIAL_API}
  )

target_include_directories (tutoriallib PUBLIC src)
target_link_libraries (tutoriallib ZserioCppRuntime)
set_property (TARGET tutoriallib PROPERTY POSITION_INDEPENDENT_CODE ON)

add_executable (zserio-tutorial
  src/main.cpp
  )

target_link_libraries (zserio-tutorial tutoriallib)
```

Now we only need to generate the code, populate the main.cpp and we are done.

But before we can generate code, we need to write the schema definition of our data.

### Writing a schema

Open up your favorite text editor and start writing your schema. We will use the example from above plus some additional structures to showcase some of zserio's features.

```
package tutorial;

struct Employee
{
  uint8           age : age <= 65; // max age is 65
  string          name;
  uint16          salary;
  optional uint16 bonus;
  Role            role;

  // if employee is a developer, list programming skill

  Experience       skills[] if role == Role.DEVELOPER;
};

struct Experience
{
  bit:6     yearsOfExperience;
  Language  programmingLanguage;
};

enum bit:2 Language
{
  CPP     = 0,
  JAVA    = 1,
  PYTHON  = 3,
  JS      = 4
};

enum uint8 Role
{
  DEVELOPER = 0,
  TEAM_LEAD = 1,
  CTO       = 2,
};
```
We have added some of zserio's features above. Let's quickly take a look:

- **constraints**

  Although the `uint8` of field `age` would allow values up to 255, we limit the use already in the schema definition by using a [constraint](doc/ZserioLanguageOverview/CompoundTypes.md#constraints)
  If we try to write values larger than 65, the generated writers will throw an exception.

- **optional fields**

  The `bonus` field is prefixed with the keyword `optional` which will add a invisible 1-bit bool before that field which indicates whether the field exists. If it is not set then only one bit will be added to the byte stream.
  See [Zserio Invisibles](doc/ZserioInvisibles.md) for more information.

- **conditions**

    We add programming skills only if the employee is developer.

For more details on the features of zserio head over to the [zserio language overview](doc/ZserioLanguageOverview/ZserioLanguageOverview.md).


We now save the file to disk as `tutorial.zs`.

>Please note that the filename has to be equivalent to the package name inside the zserio file.
The zserio compiler accepts arbitrary file extensions (in this case `*.zs`). But make sure that all imported files have also the same file extension.


### Compiling and generating code

We now are ready to compile the schema with the zserio compiler. The zserio compiler checks the schema file and its [imported files](doc/ZserioLanguageOverview/PackagesAndImports.md) and reports errors and warnings.
In addition, the zserio compiler generates code for the supported languages and may generate HTML documentation. For a complete overview of available options, please refer to the [zserio compiler User Guide](doc/zserioCompilerUserGuide.md).

So let's generate some C++ code:

```
java -jar zserio.jar -cpp src tutorial.zs
```
This command generates C++ code and puts it into the `src` folder. It actually creates subfolders for each package in the schema.

So after generating the code our folder structure looks like this:

```
.
├───3rdparty
│   └───runtimes
│       └───zserio
│           └───inspector
└───src
    └───tutorial
```

Let's take a quick look what has been generated. In the `src/tutorial` folder you now find the following files:

```
Employee.h  Experience.h  Language.h  Role.h  Tutorial.cpp
```
There is one header file for each struct or enum and one amalgamated cpp file.

We now have everything ready to serialize and deserialize our data.

### Serialize/deserialize using the generated

Before we start programming, let's have cmake generate our project:

```
touch src/main.cpp
mkdir build
cd build
cmake ..
```

Then open up your favorite IDE and start using the zserio classes by including the root element from the schema that we want to use.

```cpp
#include <tutorial/Employee.h>
```

Let's declare an employee Joe and fill in some data:

```cpp
tutorial::Employee joe;
joe.setAge(34);
joe.setName("Joe Smith");
joe.setSalary(5000);
joe.setRole(tutorial::Role::DEVELOPER);
```
To be able to populate a list of skill, we need to declare a zserio object array template of type Experience:

```cpp
zserio::ObjectArray<tutorial::Experience> skills;
```

You can find a full list of available zserio templates in the [C++ zserio API overview](doc/ZserioCppAPI.md)

So now let's generate two entries for the skills list:

First we add C++ experience:
```
tutorial::Experience skill1;
skill1.setYearsOfExperience(8);
skill1.setProgrammingLanguage(tutorial::Language::CPP);
skills.push_back(skill1);
```
and then also some Python experience:

```
tutorial::Experience skill2;
skill2.setProgrammingLanguage(tutorial::Language::PYTHON);
skill2.setYearsOfExperience(4);
skills.push_back(skill2);
```

Don't forget to set Joe's experience:

```cpp
joe.setSkills(skills);
```

So after we have set all the fields, we have to declare a BitStream writer and write the stream:

```cpp
zserio::BitStreamWriter writer;
joe.write(writer);
```

You can access the Bitstream writer's buffer by:

```cpp
size_t size;
const uint8_t* buffer = writer.getWriteBuffer(size);
```

We may now write the stream to disk using:

```cpp
std::ofstream os("tutorial.zsb", std::ofstream::binary);
os.write(reinterpret_cast<const char*>(buffer), size);
os.close();
```
Or use the buffer for any other purposes like sending it over rpc or use it internally.

Voila! You have just serialized your first data with zserio. Congratulations!

## Features

- optional elements
- constraints
- default values
- parameters
- alignments
- offsets
- arrays with indexed offsets

## Documentation

Documentation of the schema language can be found in the [Zserio Language Overview](doc/ZserioLanguageOverview/ZserioLanguageOverview.md).

Check out the [Quick Reference](doc/ZserioLanguageOverview/ZserioQuickReference.md) and the mappings for JAVA and C++ in the doc folder.
