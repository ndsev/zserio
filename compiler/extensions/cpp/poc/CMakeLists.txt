cmake_minimum_required(VERSION 3.10)

project(ServiceTest)

set(CMAKE_CXX_STANDARD 11)

set(GRPC_ROOT $ENV{GRPC_ROOT} CACHE PATH "Root directory of gRPC - default taken from env.")
set(GRPC_ENABLED ON CACHE BOOL "Enable gRPC by default.")
set(GRPC_LIBDIR "${GRPC_ROOT}/libs/opt" CACHE PATH "Path to gRPC libraries withing GRPC_ROOT.")

set(CMAKE_MODULE_PATH "${CMAKE_CURRENT_SOURCE_DIR}/../../../../cmake")
include(grpc_utils)
find_grpc_libraries()

# Protobuf manual config
# - built and installed manually within GRPC_ROOT:
#   $ cd ${GRPC_ROOT}/third_party/protobuf
#   $ make
#   $ make install prefix=${GRPC_ROOT}/third_party/protobuf/install
set(PROTOBUF_ROOT "${GRPC_ROOT}/third_party/protobuf/install")
set(PROTOBUF_LIBDIR "${PROTOBUF_ROOT}/lib")
set(PROTOBUF_INCDIR "${PROTOBUF_ROOT}/include")
set(PROTOBUF_LIBRARIES ${PROTOBUF_LIBDIR}/libprotobuf.a)

# use RPATH instead of the new RUNPATH
SET(CMAKE_EXE_LINKER_FLAGS "-Wl,--disable-new-dtags")

if (GRPC_ENABLED)
    set(ZSERIO_SERVICE_GRPC_SOURCES
        zserio_service_grpc/zserio_service.grpc.pb.cc
        zserio_service_grpc/zserio_service.grpc.pb.h
        zserio_service_grpc/zserio_service.pb.cc
        zserio_service_grpc/zserio_service.pb.h
        zserio_service_grpc/ZserioService.cpp
        zserio_service_grpc/ZserioService.h
    )
endif ()

add_executable(${PROJECT_NAME}
    ServiceTest.cpp
    service_poc/Request.cpp
    service_poc/Request.h
    service_poc/Response.cpp
    service_poc/Response.h
    service_poc/SimpleService.cpp
    service_poc/SimpleService.h
    ${ZSERIO_SERVICE_GRPC_SOURCES}
)

add_subdirectory(../../../../distr/runtime_libs/cpp ${CMAKE_CURRENT_BINARY_DIR}/runtime)

target_include_directories(${PROJECT_NAME} PRIVATE
        ${CMAKE_CURRENT_SOURCE_DIR}
        ../../../../distr/runtime_libs/cpp)
target_link_libraries(${PROJECT_NAME} ZserioCppRuntime)

if (GRPC_ENABLED)
    target_include_directories(${PROJECT_NAME} SYSTEM PRIVATE ${GRPC_INCDIR} ${PROTOBUF_INCDIR})
    target_link_libraries(${PROJECT_NAME} ${GRPC_LIBRARIES} ${PROTOBUF_LIBRARIES})
endif ()
