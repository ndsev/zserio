add_library(complex_allocation_zs STATIC zs/complex_allocation.zs)
zserio_generate_cpp(
    TARGET complex_allocation_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_complex_allocation
    EXTRA_ARGS -withoutSourcesAmalgamation -setCppAllocator polymorphic ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_COMPLEX_ALLOCATION
    OUTPUT_VAR ZSERIO_LOG_COMPLEX_ALLOCATION
    ERROR_VAR ZSERIO_LOG_COMPLEX_ALLOCATION
)
target_link_libraries(complex_allocation_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG_COMPLEX_ALLOCATION)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_complex_allocation.txt "${ZSERIO_LOG_COMPLEX_ALLOCATION}")
    check_zserio_warnings("${ZSERIO_LOG_COMPLEX_ALLOCATION}" 0)
endif ()

add_library(pubsub_allocation_zs STATIC zs/pubsub_allocation.zs)
zserio_generate_cpp(
    TARGET pubsub_allocation_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_pubsub_allocation
    EXTRA_ARGS -withoutSourcesAmalgamation -setCppAllocator polymorphic ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_PUBSUB_ALLOCATION
    OUTPUT_VAR ZSERIO_LOG_PUBSUB_ALLOCATION
    ERROR_VAR ZSERIO_LOG_PUBSUB_ALLOCATION
)
target_link_libraries(pubsub_allocation_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG_PUBSUB_ALLOCATION)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_pubsub_allocation.txt "${ZSERIO_LOG_PUBSUB_ALLOCATION}")
    check_zserio_warnings("${ZSERIO_LOG_PUBSUB_ALLOCATION}" 0)
endif ()

add_library(service_allocation_zs STATIC zs/service_allocation.zs)
zserio_generate_cpp(
    TARGET service_allocation_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_service_allocation
    EXTRA_ARGS -withoutSourcesAmalgamation -setCppAllocator polymorphic ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_SERVICE_ALLOCATION
    OUTPUT_VAR ZSERIO_LOG_SERVICE_ALLOCATION
    ERROR_VAR ZSERIO_LOG_SERVICE_ALLOCATION
)
target_link_libraries(service_allocation_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG_SERVICE_ALLOCATION)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_service_allocation.txt "${ZSERIO_LOG_SERVICE_ALLOCATION}")
    check_zserio_warnings("${ZSERIO_LOG_SERVICE_ALLOCATION}" 0)
endif ()

add_library(sql_allocation_zs STATIC zs/sql_allocation.zs)
zserio_generate_cpp(
    TARGET sql_allocation_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_sql_allocation
    EXTRA_ARGS -withoutSourcesAmalgamation -setCppAllocator polymorphic -withValidationCode ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG_SQL_ALLOCATION
    ERROR_VAR ZSERIO_LOG_SQL_ALLOCATION
)
target_link_libraries(sql_allocation_zs PUBLIC ZserioCppRuntime)
target_include_directories(sql_allocation_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG_SQL_ALLOCATION)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_sql_allocation.txt "${ZSERIO_LOG_SQL_ALLOCATION}")
    check_zserio_warnings("${ZSERIO_LOG_SQL_ALLOCATION}" 0)
endif ()

add_custom_test(set_cpp_allocator
    DEPENDS
        complex_allocation_zs
        pubsub_allocation_zs
        service_allocation_zs
        sql_allocation_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ComplexAllocationTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PubsubAllocationTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ServiceAllocationTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/SqlAllocationTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES_COMPLEX_ALLOCATION}
        ${GENERATED_SOURCES_PUBSUB_ALLOCATION}
        ${GENERATED_SOURCES_SERVICE_ALLOCATION}
        ${GENERATED_SOURCES_SQL_ALLOCATION}
)
