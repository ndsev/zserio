cmake_minimum_required(VERSION 2.8.12.2)

project(BitMaskTest)

set(SOURCES_LIST
    Emotions.cpp
    Emotions.h
    test.cpp
)

set(CMAKE_CXX_STANDARD 11)

add_subdirectory(../runtime/src runtime)

add_executable(${PROJECT_NAME} ${SOURCES_LIST})

#target_include_directories(${PROJECT_NAME} SYSTEM PRIVATE gen)
target_link_libraries(${PROJECT_NAME} ZserioCppRuntime)


