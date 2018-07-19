# Zserio C++ runtime library and tests.
#
# This CMake file defines a static library which contains the zserio C++ runtime and an executable which
# contains the zserio C++ runtime tests.
#
# This CMake file is designed to be included directly without any further dependencies.
#

cmake_minimum_required(VERSION 2.8.12.2)

project(ZserioCppRuntimeTests)

enable_testing()

set(CPPCHECK_HOME "" CACHE PATH "Home directory of cppcheck tool. If empty, cppcheck tool is not called.")

set(ZSERIO_PROJECT_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/../../../..")
set(CMAKE_MODULE_PATH "${ZSERIO_PROJECT_ROOT}/cmake")

# compile custom SQLite3 for testing
add_library(SQLite3 ${ZSERIO_PROJECT_ROOT}/3rdparty/cpp/sqlite/sqlite3.c)
if (UNIX)
    target_link_libraries(SQLite3 PUBLIC dl)
endif ()

set(ZSERIO_RUNTIME_SQLITE_LIBRARY SQLite3)
set(ZSERIO_RUNTIME_SQLITE_INCLUDE_DIR ${ZSERIO_PROJECT_ROOT}/3rdparty/cpp/sqlite)

# cmake helpers
include(cmake_utils)

# set gcc paramaters
include(gcc_utils)
gcc_set_warnings()
gcc_set_static_clibs()

add_subdirectory(src)
add_subdirectory(test)

# install Zserio C++ runtime library (we release sources only)
install(DIRECTORY src/ DESTINATION runtime_libs/cpp
        FILES_MATCHING
        PATTERN "3rdParty.txt"
        PATTERN "CMakeLists.txt"
        PATTERN "*.h"
        PATTERN "*.cpp")

# add cppcheck custom command
include(cppcheck_utils)
cppcheck_add_custom_command(TARGET ZserioCppRuntime SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/src
        SUPPRESSION_FILE ${CMAKE_CURRENT_SOURCE_DIR}/src/CppcheckSuppressions.txt)
