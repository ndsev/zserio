add_library(service_types_zs STATIC zs/service_types.zs)
zserio_generate_cpp(
    TARGET service_types_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(service_types_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(service_types
    DEPENDS
        service_types_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/SimpleServiceTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ComplexTypesServiceTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
