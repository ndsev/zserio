# Zserio C++ runtime library and tests.
#
# This CMake file defines a static library which contains the zserio C++ runtime and an executable which
# contains the zserio C++ runtime tests.
#
# This CMake file is designed to be included directly without any further dependencies.
#

cmake_minimum_required(VERSION 3.6.0)

project(ZserioCppRuntimeTests)

enable_testing()

set(CPPCHECK_HOME "" CACHE PATH "Home directory of cppcheck tool. If empty, cppcheck tool is not called.")
set(SANITIZERS_ENABLED OFF CACHE BOOL "Whether the compiler sanitizers are enabled.")
set(CLANG_TIDY_BIN "" CACHE STRING "Name of clang-tidy binary. If empty, clang-tidy tool is not called.")

set(ZSERIO_PROJECT_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/../../../..")
set(CMAKE_MODULE_PATH "${ZSERIO_PROJECT_ROOT}/cmake")

set(ZSERIO_CODE_COVERAGE_ENABLE OFF CACHE BOOL "Enable unit test code coverage calculation.")
set(ZSERIO_CODE_COVERAGE_FAIL_ON_INCOMPLETE ON CACHE BOOL "Fail build if line coverage is not 98%.")

# cmake helpers
include(cmake_utils)

# setup compiler
include(compiler_utils)
compiler_set_static_clibs()
if (SANITIZERS_ENABLED)
    compiler_set_address_sanitizer()
endif ()

# warnings and sanitizers only for src
compiler_set_warnings()
compiler_set_warnings_as_errors()
if (SANITIZERS_ENABLED)
    compiler_set_undefined_sanitizer()
endif ()

add_subdirectory(src runtime)

compiler_reset_warnings()
compiler_reset_warnings_as_errors()
if (SANITIZERS_ENABLED)
    compiler_reset_undefined_sanitizer()
endif ()

add_subdirectory(test runtime_test)

# coverage
if (${ZSERIO_CODE_COVERAGE_ENABLE})
    include(coverage_utils)
    enable_coverage_for_target(ZserioCppRuntime)
    enable_coverage_for_target(ZserioCppRuntimeTest)

    set(COV_PARAMS)
    if (${ZSERIO_CODE_COVERAGE_FAIL_ON_INCOMPLETE})
        list(APPEND COV_PARAMS "INCOMPLETE_COVERAGE_FAIL")
        list(APPEND COV_PARAMS "98")
    endif ()
    list(APPEND COV_PARAMS "EXCLUDE_SOURCES")
    list(APPEND COV_PARAMS ".*test_object.*")
    create_coverage_target(${COV_PARAMS})
endif ()

# install Zserio C++ runtime library (we release sources only)
install(DIRECTORY src/ DESTINATION cpp
        FILES_MATCHING
        PATTERN "CMakeLists.txt"
        PATTERN "*.h"
        PATTERN "*.cpp")

# add clang-tidy custom target
include(clang_tidy_utils)
clang_tidy_add_custom_target(ZserioCppRuntime-clang-tidy
                             DEPENDS ZserioCppRuntime ZserioCppRuntimeTest
                             SOURCES_GLOBS
                                "${CMAKE_CURRENT_SOURCE_DIR}/src/zserio/*.cpp"
                                "${CMAKE_CURRENT_SOURCE_DIR}/test/zserio/*.cpp"
                             BUILD_PATH "${CMAKE_BINARY_DIR}"
                             CONFIG_FILE "${CMAKE_CURRENT_SOURCE_DIR}/ClangTidyConfig.txt"
                             HEADER_FILTER
                                ".*(src|test)/zserio/.*"
                             SUPPRESSIONS_FILE "${CMAKE_CURRENT_SOURCE_DIR}/ClangTidySuppressions.txt")

# add cppcheck custom command
include(cppcheck_utils)
cppcheck_add_custom_command(TARGET ZserioCppRuntime
                            SOURCE_DIR
                                "${CMAKE_CURRENT_SOURCE_DIR}/src/zserio"
                                "${CMAKE_CURRENT_SOURCE_DIR}/src/zserio/*.h"
                                "${CMAKE_CURRENT_SOURCE_DIR}/src/zserio/*/*.h"
                                "${CMAKE_CURRENT_SOURCE_DIR}/test/zserio"
                                "${CMAKE_CURRENT_SOURCE_DIR}/test/zserio/*.h"
                            INCLUDE_DIR
                                "${CMAKE_CURRENT_SOURCE_DIR}/src"
                            OPTIONS --library=googletest.cfg -UZSERIO_STANDARD_TYPES_HEADER)

# build and install doxygen documentation
find_package(Doxygen REQUIRED)
add_custom_target(ZserioCppRuntime-doxygen ALL
    ${CMAKE_COMMAND} -E env ZSERIO_CPP_RUNTIME_OUTPUT_DIR="${CMAKE_CURRENT_BINARY_DIR}"
    ${DOXYGEN_EXECUTABLE} ${CMAKE_CURRENT_SOURCE_DIR}/doc/doxyfile
    WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/src
    COMMENT "Generating C++ runtime HTML documentation"
)
install(DIRECTORY ${CMAKE_CURRENT_BINARY_DIR}/zserio_doc DESTINATION cpp)
