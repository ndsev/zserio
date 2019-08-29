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
gtest_add_library("${CMAKE_CURRENT_SOURCE_DIR}/../../../../../3rdparty/cpp/googletest")

# add SQLite3 library
include(sqlite_utils)
sqlite_add_library(${ZSERIO_PROJECT_ROOT})

set(ZSERIO_CPP_RUNTIME_TEST_SRCS
    zserio/AlignedStorageTest.cpp
    zserio/AnyHolderTest.cpp
    zserio/BasicArrayTest.cpp
    zserio/BitFieldArrayTest.cpp
    zserio/BitFieldUtilTest.cpp
    zserio/BitPositionUtilTest.cpp
    zserio/BitSizeOfCalculatorTest.cpp
    zserio/BitStreamReaderTest.cpp
    zserio/BitStreamTest.cpp
    zserio/BitStreamToFileTest.cpp
    zserio/BitStreamWriterTest.cpp
    zserio/BuildInOperatorsTest.cpp
    zserio/ContainerTest.cpp
    zserio/FloatUtilTest.cpp
    zserio/HashCodeUtilTest.cpp
    zserio/ObjectArrayTest.cpp
    zserio/OptionalHolderTest.cpp
    zserio/SqliteConnectionTest.cpp
    zserio/StringConvertUtilTest.cpp
    zserio/StringHolderTest.cpp
    zserio/VarUInt64UtilTest.cpp
)

if (ZSERIO_RUNTIME_INCLUDE_INSPECTOR)
    set(ZSERIO_CPP_RUNTIME_TEST_SRCS
        ${ZSERIO_CPP_RUNTIME_TEST_SRCS}
        zserio/inspector/BlobInspectorTreeTest.cpp
        zserio/inspector/BlobInspectorTreeUtilTest.cpp
        zserio/inspector/BlobInspectorValueTest.cpp
    )
endif ()

add_executable(${PROJECT_NAME} ${ZSERIO_CPP_RUNTIME_TEST_SRCS})
target_include_directories(${PROJECT_NAME} PRIVATE ../src)
target_include_directories(${PROJECT_NAME} SYSTEM PRIVATE ${SQLITE_INCDIR})
target_link_libraries(${PROJECT_NAME} PRIVATE ZserioCppRuntime gtest_main ${SQLITE_LIBRARY})

gtest_add_tests(${PROJECT_NAME} "--gtest_output=xml" ${ZSERIO_CPP_RUNTIME_TEST_SRCS})

# add cppcheck custom command
include(cppcheck_utils)
cppcheck_add_custom_command(TARGET ${PROJECT_NAME}
                            SOURCE_DIR "${CMAKE_CURRENT_SOURCE_DIR}"
                            INCLUDE_DIR ../src "${SQLITE_INCDIR}")
