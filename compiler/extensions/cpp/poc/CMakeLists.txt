cmake_minimum_required(VERSION 2.8.12.2)

project(BitStreamTest)

set(SOURCES_LIST
    ExternalStructure.cpp
    ExternalStructure.h

    SimpleStructure.cpp
    SimpleStructure.h

    main.cpp
)

set(CMAKE_CXX_STANDARD 11)

add_subdirectory(../runtime/src runtime)

add_executable(${PROJECT_NAME} ${SOURCES_LIST})

target_include_directories(${PROJECT_NAME} SYSTEM PRIVATE .)
target_link_libraries(${PROJECT_NAME} ZserioCppRuntime)
