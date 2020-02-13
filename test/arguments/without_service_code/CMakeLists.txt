set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/without_service_code/Response.cpp
    ${ZSERIO_GEN_DIR}/without_service_code/Response.h
    ${ZSERIO_GEN_DIR}/without_service_code/Request.cpp
    ${ZSERIO_GEN_DIR}/without_service_code/Request.h
    # Service files are not generated
)

zserio_add_library(TARGET without_service_code_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE without_service_code.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutServiceCode -withoutSourcesAmalgamation
)

add_test_sources(
    ${ZSERIO_CPP_DIR}/WithoutServiceCodeTest.cpp
)

add_test_dependencies(without_service_code_zs)
