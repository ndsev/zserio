# Zserio C++ runtime library test.
#
# This CMake file defines an executable which contains the zserio C++ runtime tests.
#
# This CMake file is NOT designed to be included directly without any further dependencies.
#

cmake_minimum_required(VERSION 3.1.0)

project(ZserioCppRuntimeTest)

# add gtest library
include(gtest_utils)
gtest_add_library("${ZSERIO_PROJECT_ROOT}/3rdparty/cpp/googletest")

# add SQLite3 library
include(sqlite_utils)
sqlite_add_library(${ZSERIO_PROJECT_ROOT})

compiler_set_warnings()
compiler_set_warnings_as_errors()
if (SANITIZERS_ENABLED)
    compiler_set_undefined_sanitizer()
endif ()

set(TEST_OBJECT_SRCS
    test_object/DummyBitmask.cpp
    test_object/DummyBitmask.h
    test_object/DummyEnum.cpp
    test_object/DummyEnum.h
    test_object/DummyNested.cpp
    test_object/DummyNested.h
    test_object/DummyObject.cpp
    test_object/DummyObject.h
)

set(ZSERIO_CPP_RUNTIME_TEST_SRCS
    zserio/AllocatorPropagatingCopyTest.cpp
    zserio/AnyHolderTest.cpp
    zserio/ArrayTest.cpp
    zserio/BitBufferTest.cpp
    zserio/BitFieldUtilTest.cpp
    zserio/BitPositionUtilTest.cpp
    zserio/BitSizeOfCalculatorTest.cpp
    zserio/BitStreamReaderTest.cpp
    zserio/BitStreamTest.cpp
    zserio/BitStreamWriterTest.cpp
    zserio/BuiltInOperatorsTest.cpp
    zserio/ConstraintExceptionTest.cpp
    zserio/CppRuntimeExceptionTest.cpp
    zserio/DebugStringUtilTest.cpp
    zserio/EnumsTest.cpp
    zserio/FloatUtilTest.cpp
    zserio/HashCodeUtilTest.cpp
    zserio/HeapOptionalHolderTest.cpp
    zserio/InplaceOptionalHolderTest.cpp
    zserio/JsonEncoderTest.cpp
    zserio/JsonDecoderTest.cpp
    zserio/JsonParserTest.cpp
    zserio/JsonReaderTest.cpp
    zserio/JsonTokenizerTest.cpp
    zserio/JsonWriterTest.cpp
    zserio/FileUtilTest.cpp
    zserio/MemoryResourceTest.cpp
    zserio/NewDeleteResourceTest.cpp
    zserio/PolymorphicAllocatorTest.cpp
    zserio/PubsubExceptionTest.cpp
    zserio/ReflectableTest.cpp
    zserio/ReflectableUtilTest.cpp
    zserio/SerializeUtilTest.cpp
    zserio/SpanTest.cpp
    zserio/ServiceExceptionTest.cpp
    zserio/SqliteConnectionTest.cpp
    zserio/StringConvertUtilTest.cpp
    zserio/StringViewTest.cpp
    zserio/TraitsTest.cpp
    zserio/TypeInfoTest.cpp
    zserio/TypeInfoUtilTest.cpp
    zserio/UniquePtrTest.cpp
    zserio/ValidationSqliteUtilTest.cpp
    zserio/VarSizeUtilTest.cpp
    zserio/WalkerTest.cpp
    zserio/ZserioTreeCreatorTest.cpp
)

set(ZSERIO_CPP_RUNTIME_TEST_HEADERS
    zserio/TrackingAllocator.h
)

if (MSVC)
    set_property(SOURCE zserio/ReflectableTest.cpp APPEND PROPERTY COMPILE_OPTIONS /bigobj)
endif ()

if (CMAKE_SIZEOF_VOID_P EQUAL 8)
    # allows bit stream reader and var size util optimizations for 64bit platforms
    set_property(SOURCE zserio/VarSizeUtilTest.cpp
                 APPEND PROPERTY COMPILE_DEFINITIONS ZSERIO_RUNTIME_64BIT)
endif ()

add_executable(${PROJECT_NAME} ${TEST_OBJECT_SRCS} ${ZSERIO_CPP_RUNTIME_TEST_SRCS}
        ${ZSERIO_CPP_RUNTIME_TEST_HEADERS})

set_target_properties(${PROJECT_NAME} PROPERTIES CXX_STANDARD 11 CXX_STANDARD_REQUIRED YES CXX_EXTENSIONS NO)
target_include_directories(${PROJECT_NAME} PRIVATE .)
target_include_directories(${PROJECT_NAME} PRIVATE ../src)
target_include_directories(${PROJECT_NAME} SYSTEM PRIVATE ${SQLITE_INCDIR})
target_link_libraries(${PROJECT_NAME} PRIVATE ZserioCppRuntime gtest_main ${SQLITE_LIBRARY})

gtest_add_tests(${PROJECT_NAME} "--gtest_output=xml" ${ZSERIO_CPP_RUNTIME_TEST_SRCS})
