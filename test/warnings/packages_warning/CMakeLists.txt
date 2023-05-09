set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/packages_warning/package_import_overwrite_warning/TopDatabase.cpp
    ${ZSERIO_GEN_DIR}/packages_warning/package_import_overwrite_warning/TopDatabase.h

    ${ZSERIO_GEN_DIR}/packages_warning/simple_database/SimpleDatabase.cpp
    ${ZSERIO_GEN_DIR}/packages_warning/simple_database/SimpleDatabase.h
    ${ZSERIO_GEN_DIR}/packages_warning/simple_database/SimpleTable.cpp
    ${ZSERIO_GEN_DIR}/packages_warning/simple_database/SimpleTable.h

    ${ZSERIO_GEN_DIR}/packages_warning/single_type_already_imported_warning/TopDatabase.cpp
    ${ZSERIO_GEN_DIR}/packages_warning/single_type_already_imported_warning/TopDatabase.h
)

zserio_add_library(packages_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE packages_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
    EXPECTED_WARNINGS 4
    ZSERIO_LOG_FILENAME zserio_log.txt
)

target_include_directories(packages_warning_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_custom_test(packages_warning
    DEPENDS
        packages_warning_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackagesWarningTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
