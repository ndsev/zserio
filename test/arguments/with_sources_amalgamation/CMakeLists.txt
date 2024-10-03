add_library(with_sources_amalgamation_zs STATIC ${TEST_ZS_ROOT}/with_sources_amalgamation.zs)
zserio_generate_cpp(
    TARGET with_sources_amalgamation_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG_WITH_SOURCES_AMALGAMATION
    ERROR_VAR ZSERIO_LOG_WITH_SOURCES_AMALGAMATION
)
target_link_libraries(with_sources_amalgamation_zs PUBLIC ZserioCppRuntime)
target_include_directories(with_sources_amalgamation_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG_WITH_SOURCES_AMALGAMATION)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_with_sources_amalgamation.txt
        "${ZSERIO_LOG_WITH_SOURCES_AMALGAMATION}")
    check_zserio_warnings("${ZSERIO_LOG_WITH_SOURCES_AMALGAMATION}" 0)
endif ()

add_library(default_package_amalgamation_zs STATIC ${TEST_ZS_ROOT}/default_package_amalgamation.zs)
zserio_generate_cpp(
    TARGET default_package_amalgamation_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_default_package
    EXTRA_ARGS -withSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_DEFAULT_PACKAGE
    OUTPUT_VAR ZSERIO_LOG_DEFAULT_PACKAGE_AMALGAMATION
    ERROR_VAR ZSERIO_LOG_DEFAULT_PACKAGE_AMALGAMATION
)
target_link_libraries(default_package_amalgamation_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG_DEFAULT_PACKAGE_AMALGAMATION)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_default_package_amalgamation.txt
        "${ZSERIO_LOG_DEFAULT_PACKAGE_AMALGAMATION}")
    check_zserio_warnings("${ZSERIO_LOG_DEFAULT_PACKAGE_AMALGAMATION}" 0)
endif ()

add_custom_test(with_sources_amalgamation
    DEPENDS
        with_sources_amalgamation_zs
        default_package_amalgamation_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/DefaultPackageAmalgamationTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithSourcesAmalgamationTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
        ${GENERATED_SOURCES_DEFAULT_PACKAGE}
)
