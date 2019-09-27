cmake_minimum_required(VERSION 2.8.12.2)

project(TemplatesTest)

set(SOURCES_LIST
    templates/Compound_uint32.cpp
    templates/Compound_uint32.h

    templates/Field_Compound_uint32.cpp
    templates/Field_Compound_uint32.h

    templates/StructTemplatedTemplateArgument.cpp
    templates/StructTemplatedTemplateArgument.h

    main.cpp
)

set(CMAKE_CXX_STANDARD 11)

add_subdirectory(../runtime/src runtime)

add_executable(${PROJECT_NAME} ${SOURCES_LIST})

target_include_directories(${PROJECT_NAME} SYSTEM PRIVATE .)
target_link_libraries(${PROJECT_NAME} ZserioCppRuntime)

