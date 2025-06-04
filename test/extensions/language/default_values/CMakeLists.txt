add_library(default_values_zs STATIC ${TEST_ZS_ROOT}/default_values.zs)
zserio_generate_cpp(
    TARGET default_values_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(default_values_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(default_values
    DEPENDS
        default_values_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExtendedDefaultValuesTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/OptionalDefaultValuesTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StructureDefaultValuesTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
