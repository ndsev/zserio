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

set(ZSERIO_CPP_RUNTIME_TEST_SRCS
    zserio/AllocatorPropagatingCopyTest.cpp
    zserio/AnyHolderTest.cpp
    zserio/ArraysTest.cpp
    zserio/BitBufferTest.cpp
    zserio/BitFieldUtilTest.cpp
    zserio/BitPositionUtilTest.cpp
    zserio/BitSizeOfCalculatorTest.cpp
    zserio/BitStreamReaderTest.cpp
    zserio/BitStreamTest.cpp
    zserio/BitStreamWriterTest.cpp
    zserio/BlobBufferTest.cpp
    zserio/BuildInOperatorsTest.cpp
    zserio/CppRuntimeExceptionTest.cpp
    zserio/EnumsTest.cpp
    zserio/FloatUtilTest.cpp
    zserio/HashCodeUtilTest.cpp
    zserio/HeapOptionalHolderTest.cpp
    zserio/InplaceOptionalHolderTest.cpp
    zserio/MemoryResourceTest.cpp
    zserio/NewDeleteResourceTest.cpp
    zserio/PolymorphicAllocatorTest.cpp
    zserio/SpanTest.cpp
    zserio/SqliteConnectionTest.cpp
    zserio/StringConvertUtilTest.cpp
    zserio/StringViewTest.cpp
    zserio/TrackingAllocator.h
    zserio/UniquePtrTest.cpp
    zserio/ValidationSqliteUtilTest.cpp
    zserio/VarSizeUtilTest.cpp
)

set(ZSERIO_CPP_RUNTIME_TEST_HEADERS
    zserio/TrackingAllocator.h
)

if (CMAKE_SIZEOF_VOID_P EQUAL 8)
    # allows bit stream reader and var size util optimizations for 64bit platforms
    set_property(SOURCE zserio/VarSizeUtilTest.cpp
                 APPEND PROPERTY COMPILE_DEFINITIONS ZSERIO_RUNTIME_64BIT)
endif ()

add_executable(${PROJECT_NAME} ${ZSERIO_CPP_RUNTIME_TEST_SRCS} ${ZSERIO_CPP_RUNTIME_TEST_HEADERS})

set_target_properties(${PROJECT_NAME} PROPERTIES CXX_STANDARD 11 CXX_STANDARD_REQUIRED YES CXX_EXTENSIONS NO)
target_include_directories(${PROJECT_NAME} PRIVATE ../src)
target_include_directories(${PROJECT_NAME} SYSTEM PRIVATE ${SQLITE_INCDIR})
target_link_libraries(${PROJECT_NAME} PRIVATE ZserioCppRuntime gtest_main ${SQLITE_LIBRARY})

gtest_add_tests(${PROJECT_NAME} "--gtest_output=xml" ${ZSERIO_CPP_RUNTIME_TEST_SRCS})
