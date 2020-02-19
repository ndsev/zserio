cmake_minimum_required(VERSION 3.10)

project(PubSubPoCTest)

set(CMAKE_CXX_STANDARD 11)

# use RPATH instead of the new RUNPATH
set(CMAKE_EXE_LINKER_FLAGS "-Wl,--disable-new-dtags")
set(CMAKE_CXX_FLAGS "-Wall")

add_subdirectory(../../../../distr/runtime_libs/cpp ${CMAKE_CURRENT_BINARY_DIR}/runtime)

add_library(PubSubMosquitto
    zserio_pubsub_mosquitto/PubSubMosquitto.cpp
    zserio_pubsub_mosquitto/PubSubMosquitto.h
)
target_link_libraries(PubSubMosquitto -lmosquitto)
target_include_directories(PubSubMosquitto PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})

add_library(PubSubPoc
    pubsub_poc/BoolValue.cpp
    pubsub_poc/BoolValue.h
    pubsub_poc/Int32Value.cpp
    pubsub_poc/Int32Value.h
    pubsub_poc/SimplePubSub.cpp
    pubsub_poc/SimplePubSub.h
    pubsub_poc/UInt64Value.cpp
    pubsub_poc/UInt64Value.h
)
target_link_libraries(PubSubPoc ZserioCppRuntime)
target_include_directories(PubSubPoc PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})

add_executable(PubSubTestA
    PubSubTestA.cpp
)
target_include_directories(PubSubTestA PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})
target_link_libraries(PubSubTestA PubSubPoc PubSubMosquitto)

add_executable(PubSubTestB
    PubSubTestB.cpp
)
target_include_directories(PubSubTestB PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})
target_link_libraries(PubSubTestB PubSubPoc PubSubMosquitto)

add_executable(PubSubTestC
    PubSubTestC.cpp
)
target_include_directories(PubSubTestC PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})
target_link_libraries(PubSubTestC PubSubPoc PubSubMosquitto)
