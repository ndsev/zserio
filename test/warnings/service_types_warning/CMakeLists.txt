if (GRPC_ENABLED AND ${ZSERIO_CPP_STANDARD} STREQUAL c++98)
    set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
    set(ZSERIO_GENERATED_SOURCES
        ${ZSERIO_GEN_DIR}/service_types_warning/Response.h
        ${ZSERIO_GEN_DIR}/service_types_warning/Response.cpp
        ${ZSERIO_GEN_DIR}/service_types_warning/Request.h
        ${ZSERIO_GEN_DIR}/service_types_warning/Request.cpp
    )

    zserio_add_library(TARGET service_types_zs
        SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
        MAIN_SOURCE service_types_warning.zs
        OUT_DIR ${ZSERIO_GEN_DIR}
        OUT_FILES ${ZSERIO_GENERATED_SOURCES}
        ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
        ZSERIO_OPTIONS -withoutSourcesAmalgamation -cppStandard ${ZSERIO_CPP_STANDARD}
        IGNORE_WARNINGS ON
        ZSERIO_LOG_FILENAME "zserio.log"
    )

    add_test_sources(
        ${ZSERIO_CPP_DIR}/ServiceWarningsTest.cpp
    )

    add_test_dependencies(service_types_zs)
endif ()
