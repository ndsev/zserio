# Zserio C++ integration test examples.
#
# This CMake file defines an executable which contains all Zserio C++ integration test examples.
#
# This CMake file is designed to be included directly without any further dependencies.
#

cmake_minimum_required(VERSION 2.8.12.2)

project(ZserioCppTest)

enable_testing()

# input parameters
set(ZSERIO_PROJECT_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/..")
set(ZSERIO_RELEASE_ROOT "${ZSERIO_PROJECT_ROOT}/distr" CACHE PATH "Root directory of Zserio release binaries.")

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

# GRPC prebuild libraries for testing Services
include(grpc_utils)
find_grpc_libraries()

set(CMAKE_CXX_STANDARD 11) # needed due to GRPC

# add zserio runtime library
include(zserio_utils)
set(ZSERIO_JAR_DIR "${ZSERIO_RELEASE_ROOT}/zserio_libs")
set(ZSERIO_RUNTIME_LIBRARY_DIR "${ZSERIO_RELEASE_ROOT}/runtime_libs/cpp")
zserio_add_runtime_library(RUNTIME_LIBRARY_DIR "${ZSERIO_RUNTIME_LIBRARY_DIR}"
                           INCLUDE_INSPECTOR ON
                           INCLUDE_RELATIONAL ON)

# functions to set globals called by included CMake lists
set(TEST_SOURCES_LIST "" CACHE INTERNAL "Test sources") # clean list to allow cmake reconfigure
function(add_test_sources)
    foreach (SRC_FILE ${ARGV})
        set(TEST_SOURCES_LIST "${TEST_SOURCES_LIST};${CMAKE_CURRENT_SOURCE_DIR}/${SRC_FILE}" CACHE INTERNAL "")
    endforeach ()
endfunction()

set(TEST_DEPENDENCIES_LIST "" CACHE INTERNAL "Test dependencies") # clean list to allow cmake reconfigure
function(add_test_dependencies)
    foreach (DEPENDENCY ${ARGV})
        set(TEST_DEPENDENCIES_LIST "${TEST_DEPENDENCIES_LIST};${DEPENDENCY}" CACHE INTERNAL "")
    endforeach ()
endfunction()

# add all test cases
file(GLOB_RECURSE SUBPROJECTS RELATIVE ${CMAKE_CURRENT_SOURCE_DIR} "*/CMakeLists.txt")
foreach(subproject ${SUBPROJECTS})
    get_filename_component(subdirectory ${subproject} DIRECTORY)
    add_subdirectory(${subdirectory})
endforeach()

add_executable(${PROJECT_NAME} ${TEST_SOURCES_LIST})
target_link_libraries(${PROJECT_NAME} gtest_main ${TEST_DEPENDENCIES_LIST})

# grpc
if (GRPC_ENABLED)
    target_include_directories(${PROJECT_NAME} SYSTEM PRIVATE ${GRPC_INCDIR})
    target_link_libraries(${PROJECT_NAME} ${GRPC_LIBRARIES})
endif ()

# add tests - test are labeled by file names (i.e. grouped)
gtest_add_tests(${PROJECT_NAME} "--gtest_output=xml" ${TEST_SOURCES_LIST})

# add cppcheck custom command
include(cppcheck_utils)
cppcheck_add_custom_command(TARGET ${PROJECT_NAME} SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR})
