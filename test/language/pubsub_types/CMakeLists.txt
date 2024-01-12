add_library(pubsub_types_zs STATIC zs/pubsub_types.zs)
zserio_generate_cpp(
    TARGET pubsub_types_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(pubsub_types_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(pubsub_types
    DEPENDS
        pubsub_types_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/SimplePubsubTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
