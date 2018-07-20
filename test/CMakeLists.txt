# Zserio C++ integration test examples.
#
# This CMake file defines an executable which contains all Zserio C++ integration test examples.
#
# This CMake file is designed to be included directly without any further dependencies.
#

cmake_minimum_required(VERSION 2.8.12.2)

project(ZserioCppTestExamples)

enable_testing()

# input parameters
set(ZSERIO_PROJECT_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/..")
set(ZSERIO_RELEASE_ROOT "${ZSERIO_PROJECT_ROOT}/distr" CACHE PATH "Root directory of Zserio release binaries.")
set(ZSERIO_TEST_NAME "" CACHE STRING "Example test name to run. Empty means all example tests.")

# add gtest library
set(CMAKE_MODULE_PATH ${ZSERIO_PROJECT_ROOT}/cmake)
include(gtest_utils)
gtest_add_library(${ZSERIO_PROJECT_ROOT}/3rdparty/cpp/googletest)

# cmake helpers
include(cmake_utils)

# set gcc paramaters
include(gcc_utils)
gcc_set_pthread()
gcc_set_warnings()
gcc_set_static_clibs()

# add SQLite3 for testing
include(sqlite_utils)
sqlite_add_library(${ZSERIO_PROJECT_ROOT})

# add zserio runtime library
include(zserio_utils)
set(ZSERIO_JAR_DIR "${ZSERIO_RELEASE_ROOT}/zserio_libs")
set(ZSERIO_RUNTIME_LIBRARY_DIR "${ZSERIO_RELEASE_ROOT}/runtime_libs/cpp")
zserio_add_runtime_library(RUNTIME_LIBRARY_DIR "${ZSERIO_RUNTIME_LIBRARY_DIR}"
                           INCLUDE_INSPECTOR ON
                           INCLUDE_RELATIONAL ON)

# functions to set globals called by included CMake lists
function(add_test_sources)
    foreach (SRC_FILE ${ARGV})
        set(TEST_SOURCES_LIST "${TEST_SOURCES_LIST};${CMAKE_CURRENT_SOURCE_DIR}/${SRC_FILE}"
            CACHE INTERNAL "test sources")
    endforeach ()
endfunction()

function(add_test_dependencies)
    foreach (DEPENDENCY ${ARGV})
        set(TEST_DEPENDENCIES_LIST "${TEST_DEPENDENCIES_LIST};${DEPENDENCY}"
            CACHE INTERNAL "test dependencies")
    endforeach ()
endfunction()

# add requested test case or all test cases
if (NOT ZSERIO_TEST_NAME STREQUAL "")
    add_subdirectory(${ZSERIO_TEST_NAME})
else ()
    add_subdirectory(arguments/set_top_level_package)
    add_subdirectory(arguments/with_inspector_code)
    add_subdirectory(arguments/with_range_check_code)
    add_subdirectory(arguments/without_sources_amalgamation)
    add_subdirectory(arguments/without_sql_code)
    add_subdirectory(arguments/without_writer_code)

    add_subdirectory(language/alignment)
    add_subdirectory(language/array_types)
    add_subdirectory(language/base_types)
    add_subdirectory(language/choice_types)
    add_subdirectory(language/comments)
    add_subdirectory(language/constants)
    add_subdirectory(language/constraints)
    add_subdirectory(language/default_values)
    add_subdirectory(language/enumeration_types)
    add_subdirectory(language/explicit_parameters)
    add_subdirectory(language/expressions)
    add_subdirectory(language/functions)
    add_subdirectory(language/indexed_offsets)
    add_subdirectory(language/literals)
    add_subdirectory(language/member_access)
    add_subdirectory(language/offsets)
    add_subdirectory(language/optional_members)
    add_subdirectory(language/packages)
    add_subdirectory(language/parameterized_types)
    add_subdirectory(language/sql_constraints)
    add_subdirectory(language/sql_databases)
    add_subdirectory(language/sql_tables)
    add_subdirectory(language/sql_types)
    add_subdirectory(language/sql_virtual_columns)
    add_subdirectory(language/sql_virtual_tables)
    add_subdirectory(language/sql_without_rowid_tables)
    add_subdirectory(language/structure_types)
    add_subdirectory(language/subtypes)
    add_subdirectory(language/union_types)

    add_subdirectory(others/complex_example)
    add_subdirectory(others/gif)
    add_subdirectory(others/metamodel)
endif ()

if ("${TEST_SOURCES_LIST}" STREQUAL "")
    file(WRITE ${CMAKE_BINARY_DIR}/DummyTestSuite.cpp
        "#include \"gtest/gtest.h\"\n"
        "\n"
        "TEST(DummyTest, test)\n"
        "{\n"
        "}\n")
    set(TEST_SOURCES_LIST "${CMAKE_BINARY_DIR}/DummyTestSuite.cpp")
endif ()

add_executable(${PROJECT_NAME} ${TEST_SOURCES_LIST})
target_link_libraries(${PROJECT_NAME} gtest_main ${TEST_DEPENDENCIES_LIST})

gtest_add_tests(${PROJECT_NAME} "--gtest_output=xml" ${TEST_SOURCES_LIST})

# add cppcheck custom command
include(cppcheck_utils)
cppcheck_add_custom_command(TARGET ${PROJECT_NAME} SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR})
