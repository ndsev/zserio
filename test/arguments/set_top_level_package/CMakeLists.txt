add_library(set_top_level_package_zs STATIC zs/set_top_level_package.zs)
zserio_generate_cpp(
    TARGET set_top_level_package_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation -setTopLevelPackage company.appl
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(set_top_level_package_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(set_top_level_package
    DEPENDS
        set_top_level_package_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/SetTopLevelPackageTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
