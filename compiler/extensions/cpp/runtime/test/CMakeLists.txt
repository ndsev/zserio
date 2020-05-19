# Zserio C++ runtime library test.
#
# This CMake file defines an executable which contains the zserio C++ runtime tests.
#
# This CMake file is NOT designed to be included directly without any further dependencies.
#

cmake_minimum_required(VERSION 2.8.12.2)

project(ZserioCppRuntimeTest)

# add gtest library
include(gtest_utils)
gtest_add_library("${ZSERIO_PROJECT_ROOT}/3rdparty/cpp/googletest")

# add SQLite3 library
include(sqlite_utils)
sqlite_add_library(${ZSERIO_PROJECT_ROOT})

set(ZSERIO_CPP_RUNTIME_TEST_SRCS
    zserio/AnyHolderTest.cpp
    zserio/ArraysTest.cpp
    zserio/BitBufferTest.cpp
    zserio/BitFieldUtilTest.cpp
    zserio/BitPositionUtilTest.cpp
    zserio/BitSizeOfCalculatorTest.cpp
    zserio/BitStreamReaderTest.cpp
    zserio/BitStreamTest.cpp
    zserio/BitStreamToFileTest.cpp
    zserio/BitStreamWriterTest.cpp
    zserio/BuildInOperatorsTest.cpp
    zserio/EnumsTest.cpp
    zserio/FloatUtilTest.cpp
    zserio/HashCodeUtilTest.cpp
    zserio/OptionalHolderTest.cpp
    zserio/SqliteConnectionTest.cpp
    zserio/StringConvertUtilTest.cpp
    zserio/VarSizeUtilTest.cpp
)

add_executable(${PROJECT_NAME} ${ZSERIO_CPP_RUNTIME_TEST_SRCS})

set_target_properties(${PROJECT_NAME} PROPERTIES CXX_STANDARD 11 CXX_STANDARD_REQUIRED YES CXX_EXTENSIONS NO)
target_include_directories(${PROJECT_NAME} PRIVATE ../src)
target_include_directories(${PROJECT_NAME} SYSTEM PRIVATE ${SQLITE_INCDIR})
target_link_libraries(${PROJECT_NAME} PRIVATE ZserioCppRuntime gtest_main ${SQLITE_LIBRARY})

gtest_add_tests(${PROJECT_NAME} "--gtest_output=xml" ${ZSERIO_CPP_RUNTIME_TEST_SRCS})

# add cppcheck custom command
include(cppcheck_utils)
cppcheck_add_custom_command(TARGET ${PROJECT_NAME}
                            SOURCE_DIR "${CMAKE_CURRENT_SOURCE_DIR}"
                            INCLUDE_DIR "${ZSERIO_PROJECT_ROOT}/compiler/extensions/cpp/runtime/src"
                            OPTIONS --library=googletest.cfg -UZSERIO_STANDARD_TYPES_HEADER)
