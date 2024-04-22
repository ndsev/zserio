add_library(with_reflection_code_zs STATIC zs/with_reflection_code.zs)
zserio_generate_cpp(
    TARGET with_reflection_code_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation -withTypeInfoCode -withReflectionCode ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(with_reflection_code_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(with_reflection_code
    DEPENDS
        with_reflection_code_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithReflectionCodeTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
