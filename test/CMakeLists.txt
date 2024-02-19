# Zserio C++ integration test examples.
#
# This CMake file defines an executable which contains all Zserio C++ integration test examples.
#
# This CMake file is designed to be included directly without any further dependencies.
#
cmake_minimum_required(VERSION 3.15.0)

project(ZserioCppTest)

enable_testing()

# input parameters
set(CPPCHECK_HOME "" CACHE PATH "Home directory of cppcheck tool. If empty, cppcheck tool is not called.")
set(SANITIZERS_ENABLED OFF CACHE BOOL "Whether the compiler sanitizers are enabled.")
set(CLANG_TIDY_BIN "" CACHE STRING "Name of clang-tidy binary. If empty, clang-tidy tool is not called.")
set(CLANG_FORMAT_BIN "" CACHE STRING "Name of clang-format binary. If empty, clang-format tool is not called.")

set(ZSERIO_PROJECT_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/..")
set(ZSERIO_RELEASE_ROOT "${ZSERIO_PROJECT_ROOT}/distr" CACHE PATH "Root directory of Zserio release binaries.")
set(ZSERIO_USE_BUNDLE ON CACHE BOOL "Generate code using zserio.jar instead of core+cpp.jar directly.")
set(ZSERIO_TEST_SUITES "**" CACHE STRING "List of test suites to run. Default is '**'.")

set(CMAKE_CXX_STANDARD 11 CACHE STRING "The C++ standard to use.")
set(CMAKE_CXX_STANDARD_REQUIRED ON CACHE BOOL "Whether C++ standard is required.")
set(CMAKE_CXX_EXTENSIONS OFF CACHE BOOL "Whether compiler specific C++ standard extensions are allowed.")

# cmake helpers
set(CMAKE_MODULE_PATH ${ZSERIO_PROJECT_ROOT}/cmake)

# cmake helpers
include(cmake_utils)
include(clang_tidy_utils)
include(clang_format_utils)
include(cppcheck_utils)

# setup compiler
include(compiler_utils)
compiler_set_pthread()
compiler_set_static_clibs()

if (SANITIZERS_ENABLED)
    compiler_set_address_sanitizer()
endif ()

# add test utility library
add_subdirectory(utils/cpp/test_utils test_utils)

# add gtest library
include(gtest_utils)
gtest_add_library(${ZSERIO_PROJECT_ROOT}/3rdparty/cpp/googletest)

# add SQLite3 for testing
include(sqlite_utils)
sqlite_add_library(${ZSERIO_PROJECT_ROOT})

compiler_set_warnings()
compiler_set_warnings_as_errors()

# add zserio runtime library
if (ZSERIO_USE_BUNDLE)
    set(ZSERIO_JAR_FILE "${ZSERIO_RELEASE_ROOT}/zserio.jar")
else()
    set(ZSERIO_JAR_FILE
        "${ZSERIO_RELEASE_ROOT}/zserio_libs/zserio_core.jar;${ZSERIO_RELEASE_ROOT}/zserio_libs/zserio_cpp.jar")
endif()
include(zserio_compiler)

add_subdirectory("${ZSERIO_RELEASE_ROOT}/runtime_libs/cpp" ZserioCppRuntime)

compiler_reset_warnings()
compiler_set_test_warnings()
if (SANITIZERS_ENABLED)
    compiler_set_undefined_sanitizer()
endif ()

# A function which checks whether the number of warnings produced by zserio compilers is as expected.
function(check_zserio_warnings ZSERIO_LOG EXPECTED_WARNINGS)
    string(REGEX MATCHALL "\\[WARNING\\]" WARNINGS "${ZSERIO_LOG}")
    list(LENGTH WARNINGS NUM_WARNINGS)

    if (NOT ${NUM_WARNINGS} EQUAL ${EXPECTED_WARNINGS})
        message(FATAL_ERROR "Zserio tool produced ${NUM_WARNINGS} warning(s) (expected ${EXPECTED_WARNINGS})!")
    endif ()
endfunction()

# cppcheck needs to know all include directories in generated code
function(get_generated_dirs GENERATED_SOURCES GENERATED_INCLUDES_VAR)
    foreach (SOURCE ${GENERATED_SOURCES})
        string(REGEX REPLACE "^(${CMAKE_CURRENT_BINARY_DIR}/[^/]*)/.*$" "\\1" INC_DIR "${SOURCE}")
        list(APPEND INCLUDE_DIRS "${INC_DIR}")
    endforeach ()
    list(REMOVE_DUPLICATES INCLUDE_DIRS)
    set(${GENERATED_INCLUDES_VAR} "${INCLUDE_DIRS}" PARENT_SCOPE)
endfunction()

# functions to set globals called by included CMake lists
set(TEST_DEPENDENCIES_LIST "" CACHE INTERNAL "Test objects")
set(TEST_SOURCES_LIST "" CACHE INTERNAL "Test sources")
function(add_custom_test TEST_NAME)
    cmake_parse_arguments(TEST_ARG "" "" "DEPENDS;SOURCES;GENERATED_SOURCES" ${ARGN})

    # test sources are added in add_executable to simplify dependencies handling
    add_library(${TEST_NAME} INTERFACE)
    add_dependencies(${TEST_NAME} ${TEST_ARG_DEPENDS})

    clang_tidy_add_custom_target(${TEST_NAME}-clang-tidy
                                 DEPENDS
                                    ${TEST_NAME}
                                 SOURCES
                                    "${TEST_ARG_SOURCES}"
                                    "${TEST_ARG_GENERATED_SOURCES}"
                                 BUILD_PATH
                                    "${CMAKE_BINARY_DIR}"
                                 CONFIG_FILE
                                    "${ZSERIO_PROJECT_ROOT}/compiler/extensions/cpp/runtime/ClangTidyConfig.txt"
                                 HEADER_FILTER
                                    "${CMAKE_CURRENT_SOURCE_DIR}/.*|${CMAKE_CURRENT_BINARY_DIR}/.*")

    if (DEFINED TEST_ARG_SOURCES)
        clang_format_add_custom_target(${TEST_NAME}-clang-format
                                     DEPENDS
                                        ${TEST_NAME}
                                     SOURCES
                                        "${TEST_ARG_SOURCES}"
                                     CONFIG_FILE
                                        "${ZSERIO_PROJECT_ROOT}/.clang-format"
                                     WERROR
                                        ON)
    endif ()

    get_generated_dirs("${TEST_ARG_GENERATED_SOURCES}" GENERATED_DIRS)
    cppcheck_add_custom_command(TARGET ${TEST_NAME}
                                SOURCE_DIR
                                    "${TEST_ARG_SOURCES}"
                                    "${GENERATED_DIRS}"
                                INCLUDE_DIR
                                    "${CMAKE_CURRENT_SOURCE_DIR}"
                                    "${GENERATED_DIRS}"
                                SUPPRESSION_FILE
                                    "${CMAKE_CURRENT_SOURCE_DIR}/CppcheckSuppressions.txt"
                                OPTIONS --library=googletest.cfg --suppress=assertWithSideEffect)

    set(TEST_DEPENDENCIES_LIST "${TEST_DEPENDENCIES_LIST};${TEST_ARG_DEPENDS}" CACHE INTERNAL "")
    if (DEFINED TEST_ARG_SOURCES)
        set(TEST_SOURCES_LIST "${TEST_SOURCES_LIST};${TEST_ARG_SOURCES}" CACHE INTERNAL "")
    endif ()
endfunction()

# add all test suites (filter out utils and data subdirectories)
foreach (TEST_SUITE ${ZSERIO_TEST_SUITES})
    file(GLOB_RECURSE SUBPROJECTS RELATIVE ${CMAKE_CURRENT_SOURCE_DIR} "${TEST_SUITE}/CMakeLists.txt")
    foreach (subproject ${SUBPROJECTS})
        if (NOT "${subproject}" MATCHES "utils/.*" AND NOT "${subproject}" MATCHES "data/.*")
            get_filename_component(subdirectory ${subproject} DIRECTORY)
            add_subdirectory(${subdirectory})
        endif ()
    endforeach ()
endforeach ()

if (NOT TEST_SOURCES_LIST)
    if (NOT TEST_DEPENDENCIES_LIST)
        message(STATUS "Test suites doesn't match any test!")
        return()
    endif ()

    set(EMPTY_TEST_HELPER ${CMAKE_CURRENT_BINARY_DIR}/EmptyTestHelper.cpp)
    set(EMPTY_TEST_HELPER_CONTENT
        "#include \"gtest/gtest.h\"\nTEST(EmptyTestHelper, Success)\n{\n    ASSERT_TRUE(true);\n}\n"
    )
    file(WRITE ${EMPTY_TEST_HELPER} "${EMPTY_TEST_HELPER_CONTENT}")
    set(TEST_SOURCES_LIST ${EMPTY_TEST_HELPER})
endif ()


# add_executable requieres at least a single source file
add_executable(${PROJECT_NAME} ${TEST_SOURCES_LIST})
target_link_libraries(${PROJECT_NAME} ${TEST_DEPENDENCIES_LIST} gtest_main ZserioCppTestUtils ZserioCppRuntime
        ${SQLITE_LIBRARY})
target_include_directories(${PROJECT_NAME} SYSTEM PRIVATE ${SQLITE_INCDIR})

# add tests - test are labeled by file names (i.e. grouped)
gtest_add_tests(${PROJECT_NAME} "--gtest_output=xml" ${TEST_SOURCES_LIST})
