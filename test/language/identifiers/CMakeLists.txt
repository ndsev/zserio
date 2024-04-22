add_library(identifiers_zs STATIC zs/identifiers.zs)
zserio_generate_cpp(
    TARGET identifiers_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(identifiers_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(identifiers
    DEPENDS
        identifiers_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitmaskNameClashingWithJavaTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureNameClashingWithJavaTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
