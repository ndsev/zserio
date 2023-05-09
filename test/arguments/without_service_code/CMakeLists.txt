set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    # Service files are not generated
    ${ZSERIO_GEN_DIR}/without_service_code/Pubsub.cpp
    ${ZSERIO_GEN_DIR}/without_service_code/Pubsub.h
    ${ZSERIO_GEN_DIR}/without_service_code/Response.cpp
    ${ZSERIO_GEN_DIR}/without_service_code/Response.h
    ${ZSERIO_GEN_DIR}/without_service_code/Request.cpp
    ${ZSERIO_GEN_DIR}/without_service_code/Request.h
)

zserio_add_library(without_service_code_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE without_service_code.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutServiceCode -withoutSourcesAmalgamation
)

add_custom_test(without_service_code
    DEPENDS
        without_service_code_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithoutServiceCodeTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
