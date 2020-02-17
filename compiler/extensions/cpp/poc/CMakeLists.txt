cmake_minimum_required(VERSION 3.10)

project(PubSubPoCTest)

set(CMAKE_CXX_STANDARD 11)

# use RPATH instead of the new RUNPATH
set(CMAKE_EXE_LINKER_FLAGS "-Wl,--disable-new-dtags")
set(CMAKE_CXX_FLAGS "-Wall")

add_executable(${PROJECT_NAME}
    PubSubTest.cpp
    pubsub_poc/BoolValue.cpp
    pubsub_poc/BoolValue.h
    pubsub_poc/Int32Value.cpp
    pubsub_poc/Int32Value.h
    pubsub_poc/SimplePubSub.cpp
    pubsub_poc/SimplePubSub.h
    pubsub_poc/UInt64Value.cpp
    pubsub_poc/UInt64Value.h)

add_subdirectory(../../../../distr/runtime_libs/cpp ${CMAKE_CURRENT_BINARY_DIR}/runtime)

target_include_directories(${PROJECT_NAME} PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}
        ../../../../distr/runtime_libs/cpp)
target_link_libraries(${PROJECT_NAME} ZserioCppRuntime)
