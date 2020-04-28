set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    # Pubsub files are not generated
    ${ZSERIO_GEN_DIR}/without_pubsub_code/Response.cpp
    ${ZSERIO_GEN_DIR}/without_pubsub_code/Response.h
    ${ZSERIO_GEN_DIR}/without_pubsub_code/Request.cpp
    ${ZSERIO_GEN_DIR}/without_pubsub_code/Request.h
    ${ZSERIO_GEN_DIR}/without_pubsub_code/Service.cpp
    ${ZSERIO_GEN_DIR}/without_pubsub_code/Service.h
)

zserio_add_library(TARGET without_pubsub_code_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE without_pubsub_code.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutPubsubCode -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/WithoutPubsubCodeTest.cpp
)

add_test_dependencies(without_pubsub_code_zs)
