cmake_minimum_required (VERSION 3.2 FATAL_ERROR)
project (ZserioTestProject)

add_subdirectory(thirdparty/runtime)

file(GLOB_RECURSE SOURCES_CPP_API "${CMAKE_CURRENT_SOURCE_DIR}/src/*.cpp")
file(GLOB_RECURSE HEADERS_CPP_API "${CMAKE_CURRENT_SOURCE_DIR}/src/*.h")

add_library(ZserioTestCpplLib STATIC ${SOURCES_CPP_API} ${HEADERS_CPP_API})

target_include_directories(ZserioTestCpplLib PUBLIC src)
target_link_libraries(ZserioTestCpplLib ZserioCppRuntime)