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
    pubsub_poc/Client.cpp
    pubsub_poc/Client.h
    pubsub_poc/GreaterThanTenProvider.cpp
    pubsub_poc/GreaterThanTenProvider.h
    pubsub_poc/PositivenessProvider.cpp
    pubsub_poc/PositivenessProvider.h
    pubsub_poc/PowerOfTwoProvider.cpp
    pubsub_poc/PowerOfTwoProvider.h
    pubsub_poc/UInt64Value.cpp
    pubsub_poc/UInt64Value.h
)
target_link_libraries(PubSubPoc ZserioCppRuntime)
target_include_directories(PubSubPoc PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})

add_executable(ClientTest
    ClientTest.cpp
)
target_include_directories(ClientTest PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})
target_link_libraries(ClientTest PubSubPoc PubSubMosquitto)

add_executable(PowerOfTwoProviderTest
    PowerOfTwoProviderTest.cpp
)
target_include_directories(PowerOfTwoProviderTest PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})
target_link_libraries(PowerOfTwoProviderTest PubSubPoc PubSubMosquitto)

add_executable(PositivenessProviderTest
    PositivenessProviderTest.cpp
)
target_include_directories(PositivenessProviderTest PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})
target_link_libraries(PositivenessProviderTest PubSubPoc PubSubMosquitto)

add_executable(GreaterThanTenProviderTest
    GreaterThanTenProviderTest.cpp
)
target_include_directories(GreaterThanTenProviderTest PRIVATE ${CMAKE_CURRENT_SOURCE_DIR})
target_link_libraries(GreaterThanTenProviderTest PubSubPoc PubSubMosquitto)
