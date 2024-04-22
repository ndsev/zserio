add_library(optional_members_warning_zs STATIC zs/optional_members_warning.zs)
zserio_generate_cpp(
    TARGET optional_members_warning_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(optional_members_warning_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 24)
endif ()

add_custom_test(optional_members_warning
    DEPENDS
        optional_members_warning_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/OptionalMembersWarningTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
