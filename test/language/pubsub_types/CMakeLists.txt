set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/pubsub_types/simple_pubsub/Int32Value.cpp
    ${ZSERIO_GEN_DIR}/pubsub_types/simple_pubsub/Int32Value.h
    ${ZSERIO_GEN_DIR}/pubsub_types/simple_pubsub/SimplePubsub.cpp
    ${ZSERIO_GEN_DIR}/pubsub_types/simple_pubsub/SimplePubsub.h
    ${ZSERIO_GEN_DIR}/pubsub_types/simple_pubsub/SimplePubsubClient.cpp
    ${ZSERIO_GEN_DIR}/pubsub_types/simple_pubsub/SimplePubsubClient.h
    ${ZSERIO_GEN_DIR}/pubsub_types/simple_pubsub/SimplePubsubProvider.cpp
    ${ZSERIO_GEN_DIR}/pubsub_types/simple_pubsub/SimplePubsubProvider.h
    ${ZSERIO_GEN_DIR}/pubsub_types/simple_pubsub/UInt64Value.cpp
    ${ZSERIO_GEN_DIR}/pubsub_types/simple_pubsub/UInt64Value.h
)

zserio_add_library(TARGET pubsub_types_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE pubsub_types.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
    ${ZSERIO_CPP_DIR}/TestPubsub.cpp
    ${ZSERIO_CPP_DIR}/TestPubsub.h
    ${ZSERIO_CPP_DIR}/SimplePubsubTest.cpp
)

add_test_dependencies(pubsub_types_zs)
