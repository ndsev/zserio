add_library(packages_warning_zs STATIC zs/packages_warning.zs)
zserio_generate_cpp(
    TARGET packages_warning_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(packages_warning_zs PUBLIC ZserioCppRuntime)
target_include_directories(packages_warning_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 4)
endif ()

add_custom_test(packages_warning
    DEPENDS
        packages_warning_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackagesWarningTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
