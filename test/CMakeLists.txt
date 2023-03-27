# Zserio C++ integration test examples.
#
# This CMake file defines an executable which contains all Zserio C++ integration test examples.
#
# This CMake file is designed to be included directly without any further dependencies.
#

cmake_minimum_required(VERSION 3.1.0)

project(ZserioCppTest)

enable_testing()

# input parameters
set(CPPCHECK_HOME "" CACHE PATH "Home directory of cppcheck tool. If empty, cppcheck tool is not called.")
set(SANITIZERS_ENABLED OFF CACHE BOOL "Whether the compiler sanitizers are enabled.")
set(CLANG_TIDY_BIN "" CACHE STRING "Name of clang-tidy binary. If empty, clang-tidy tool is not called.")

set(ZSERIO_PROJECT_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/..")
set(ZSERIO_RELEASE_ROOT "${ZSERIO_PROJECT_ROOT}/distr" CACHE PATH "Root directory of Zserio release binaries.")
set(ZSERIO_TEST_SUITES "**" CACHE STRING "List of test suites to run. Default is '**'.")

# cmake helpers
set(CMAKE_MODULE_PATH ${ZSERIO_PROJECT_ROOT}/cmake)

# cmake helpers
include(cmake_utils)

# setup compiler
include(compiler_utils)
set(CMAKE_CXX_STANDARD 11)
compiler_set_pthread()
compiler_set_static_clibs()

if (SANITIZERS_ENABLED)
    compiler_set_address_sanitizer()
endif ()
# add gtest library
include(gtest_utils)
gtest_add_library(${ZSERIO_PROJECT_ROOT}/3rdparty/cpp/googletest)

# add SQLite3 for testing
include(sqlite_utils)
sqlite_add_library(${ZSERIO_PROJECT_ROOT})

compiler_set_warnings()
compiler_set_warnings_as_errors()

# add zserio runtime library
include(zserio_utils)
set(ZSERIO_JAR_DIR "${ZSERIO_RELEASE_ROOT}/zserio_libs")
set(ZSERIO_CPP_DISTR_DIR "${ZSERIO_JAR_DIR}")
set(ZSERIO_RUNTIME_LIBRARY_DIR "${ZSERIO_RELEASE_ROOT}/runtime_libs/cpp")
zserio_add_runtime_library(RUNTIME_LIBRARY_DIR "${ZSERIO_RUNTIME_LIBRARY_DIR}")

compiler_reset_warnings()
compiler_set_test_warnings()
if (SANITIZERS_ENABLED)
    compiler_set_undefined_sanitizer()
endif ()

# functions to set globals called by included CMake lists
set(TEST_SOURCES_LIST "" CACHE INTERNAL "Test sources")
function(add_test_sources)
    foreach (SRC_FILE ${ARGV})
        set(TEST_SOURCES_LIST "${TEST_SOURCES_LIST};${CMAKE_CURRENT_SOURCE_DIR}/${SRC_FILE}" CACHE INTERNAL "")
    endforeach ()
endfunction()

set(TEST_DEPENDENCIES_LIST "" CACHE INTERNAL "Test dependencies")
function(add_test_dependencies)
    foreach (DEPENDENCY ${ARGV})
        set(TEST_DEPENDENCIES_LIST "${TEST_DEPENDENCIES_LIST};${DEPENDENCY}" CACHE INTERNAL "")
    endforeach ()
endfunction()

# add all test suites
foreach(TEST_SUITE ${ZSERIO_TEST_SUITES})
    file(GLOB_RECURSE SUBPROJECTS RELATIVE ${CMAKE_CURRENT_SOURCE_DIR} "${TEST_SUITE}/CMakeLists.txt")
    foreach(subproject ${SUBPROJECTS})
        get_filename_component(subdirectory ${subproject} DIRECTORY)
        add_subdirectory(${subdirectory})
    endforeach()
endforeach()

if (NOT TEST_SOURCES_LIST)
    if (NOT TEST_DEPENDENCIES_LIST)
        message(STATUS "Test suites doesn't match any test!")
        return()
    endif ()

    set(EMPTY_TEST_HELPER_CONTENT
        "#include \"gtest/gtest.h\"\nTEST(EmptyTestHelper, Success)\n{\n    ASSERT_TRUE(true)\;\n}\n"
    )
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/EmptyTestHelper.cpp ${EMPTY_TEST_HELPER_CONTENT})
    set(TEST_SOURCES_LIST ${CMAKE_CURRENT_BINARY_DIR}/EmptyTestHelper.cpp)
endif ()

add_executable(${PROJECT_NAME} ${TEST_SOURCES_LIST})

target_link_libraries(${PROJECT_NAME} gtest_main ${TEST_DEPENDENCIES_LIST})

target_include_directories(${PROJECT_NAME} SYSTEM PRIVATE ${SQLITE_INCDIR})
target_link_libraries(${PROJECT_NAME} ${SQLITE_LIBRARY})

target_include_directories(${PROJECT_NAME} PRIVATE ${CMAKE_CURRENT_SOURCE_DIR}/utils/cpp)

# add tests - test are labeled by file names (i.e. grouped)
gtest_add_tests(${PROJECT_NAME} "--gtest_output=xml" ${TEST_SOURCES_LIST})

# clang-tidy
if (CLANG_TIDY_BIN)
    set_property(TARGET ${PROJECT_NAME} PROPERTY CXX_CLANG_TIDY
        "${CLANG_TIDY_BIN}"
        "--config-file=${CMAKE_CURRENT_SOURCE_DIR}/.clang-tidy")
endif ()

# add cppcheck custom command
include(cppcheck_utils)
set(SUPPRESSION_FILE_NAME "${CMAKE_CURRENT_SOURCE_DIR}/CppcheckSuppressions.txt")
cppcheck_add_custom_command(TARGET ${PROJECT_NAME}
                            SOURCE_DIR "${TEST_SOURCES_LIST}"
                            SUPPRESSION_FILE "${SUPPRESSION_FILE_NAME}"
                            OPTIONS --library=googletest.cfg)
