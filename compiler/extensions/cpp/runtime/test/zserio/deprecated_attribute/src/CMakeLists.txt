cmake_minimum_required(VERSION 3.15.0)

project(DeprecatedAttribute)

set(ZSERIO_PROJECT_ROOT "${CMAKE_CURRENT_SOURCE_DIR}/../../../../../../../..")
set(CMAKE_MODULE_PATH "${ZSERIO_PROJECT_ROOT}/cmake")

include(cmake_utils)
include(compiler_utils)

compiler_set_warnings()

add_executable(${PROJECT_NAME} DeprecatedAttribute.cpp)
set_target_properties(${PROJECT_NAME} PROPERTIES CXX_STANDARD 11 CXX_STANDARD_REQUIRED YES CXX_EXTENSIONS NO)
target_include_directories(${PROJECT_NAME} PRIVATE ../../../../src)
