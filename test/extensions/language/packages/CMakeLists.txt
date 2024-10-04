add_library(default_package_zs STATIC ${TEST_ZS_ROOT}/default_package.zs)
zserio_generate_cpp(
    TARGET default_package_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_default_package
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_DEFAULT_PACKAGE
    OUTPUT_VAR ZSERIO_LOG_DEFAULT_PACKAGE
    ERROR_VAR ZSERIO_LOG_DEFAULT_PACKAGE
)
target_link_libraries(default_package_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG_DEFAULT_PACKAGE)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_default_package.txt "${ZSERIO_LOG_DEFAULT_PACKAGE}")
    check_zserio_warnings("${ZSERIO_LOG_DEFAULT_PACKAGE}" 0)
endif ()

add_library(empty_file_zs INTERFACE)
zserio_generate_cpp(
    TARGET empty_file_zs
    SRC_DIR ${TEST_ZS_ROOT}
    MAIN_ZS empty_file.zs
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_empty_file
    OUTPUT_VAR ZSERIO_LOG_EMPTY_FILE
    ERROR_VAR ZSERIO_LOG_EMPTY_FILE
)
if (ZSERIO_LOG_EMPTY_FILE)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_empty_file.txt "${ZSERIO_LOG_EMPTY_FILE}")
    check_zserio_warnings("${ZSERIO_LOG_EMPTY_FILE}" 0)
endif ()

add_library(index_zs STATIC ${TEST_ZS_ROOT}/index.zs)
zserio_generate_cpp(
    TARGET index_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_index
    # we must use 'index_workaround' to prevent clash with global function 'index' which is included
    # via gtest.h -> ... -> strings.h
    EXTRA_ARGS -withoutSourcesAmalgamation -setTopLevelPackage index_workaround ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_INDEX
    OUTPUT_VAR ZSERIO_LOG_INDEX
    ERROR_VAR ZSERIO_LOG_INDEX
)
target_link_libraries(index_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG_INDEX)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_index.txt "${ZSERIO_LOG_INDEX}")
    check_zserio_warnings("${ZSERIO_LOG_INDEX}" 0)
endif ()

add_library(package_name_conflict_zs STATIC ${TEST_ZS_ROOT}/package_name_conflict.zs)
zserio_generate_cpp(
    TARGET package_name_conflict_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_package_name_conflict
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_PACKAGE_NAME_CONFLICT
    OUTPUT_VAR ZSERIO_LOG_PACKAGE_NAME_CONFLICT
    ERROR_VAR ZSERIO_LOG_PACKAGE_NAME_CONFLICT
)
target_link_libraries(package_name_conflict_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG_PACKAGE_NAME_CONFLICT)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_package_name_conflict.txt
        "${ZSERIO_LOG_PACKAGE_NAME_CONFLICT}")
    check_zserio_warnings("${ZSERIO_LOG_PACKAGE_NAME_CONFLICT}" 0)
endif ()

add_library(packages_zs STATIC ${TEST_ZS_ROOT}/packages.zs)
zserio_generate_cpp(
    TARGET packages_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_packages
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG_PACKAGES
    ERROR_VAR ZSERIO_LOG_PACKAGES
)
target_link_libraries(packages_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG_PACKAGES)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_packages.txt "${ZSERIO_LOG_PACKAGES}")
    check_zserio_warnings("${ZSERIO_LOG_PACKAGES}" 0)
endif ()

add_library(reader_zs STATIC ${TEST_ZS_ROOT}/reader.zs)
zserio_generate_cpp(
    TARGET reader_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_reader
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG_READER
    ERROR_VAR ZSERIO_LOG_READER
)
target_link_libraries(reader_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG_READER)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_reader.txt "${ZSERIO_LOG_READER}")
    check_zserio_warnings("${ZSERIO_LOG_READER}" 0)
endif ()

add_custom_test(packages
    DEPENDS
        empty_file_zs
        default_package_zs
        index_zs
        package_name_conflict_zs
        packages_zs
        reader_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/DefaultPackageTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/IndexTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackageNameConflictTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ReaderTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES_DEFAULT_PACKAGE}
        ${GENERATED_SOURCES_INDEX}
        ${GENERATED_SOURCES_PACKAGE_NAME_CONFLICT}
        ${GENERATED_SOURCES_PACKAGES}
        ${GENERATED_SOURCES_READER}
)
