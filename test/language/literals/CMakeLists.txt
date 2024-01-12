add_library(literals_zs INTERFACE zs/literals.zs)
zserio_generate_cpp(
    TARGET literals_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(literals_zs INTERFACE ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(literals
    DEPENDS
        literals_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/LiteralsTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
