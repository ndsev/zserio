set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/unused_type_warning/Black.h
    ${ZSERIO_GEN_DIR}/unused_type_warning/Blue.h
    ${ZSERIO_GEN_DIR}/unused_type_warning/BoolChoice.cpp
    ${ZSERIO_GEN_DIR}/unused_type_warning/BoolChoice.h
    ${ZSERIO_GEN_DIR}/unused_type_warning/BoolStructure.cpp
    ${ZSERIO_GEN_DIR}/unused_type_warning/BoolStructureDatabase.cpp
    ${ZSERIO_GEN_DIR}/unused_type_warning/BoolStructureDatabase.h
    ${ZSERIO_GEN_DIR}/unused_type_warning/BoolStructure.h
    ${ZSERIO_GEN_DIR}/unused_type_warning/BoolStructureTable.cpp
    ${ZSERIO_GEN_DIR}/unused_type_warning/BoolStructureTable.h
    ${ZSERIO_GEN_DIR}/unused_type_warning/BoolUnion.cpp
    ${ZSERIO_GEN_DIR}/unused_type_warning/BoolUnion.h
    ${ZSERIO_GEN_DIR}/unused_type_warning/Color.cpp
    ${ZSERIO_GEN_DIR}/unused_type_warning/Color.h
    ${ZSERIO_GEN_DIR}/unused_type_warning/DARK_BLUE.h
    ${ZSERIO_GEN_DIR}/unused_type_warning/Red.h
    ${ZSERIO_GEN_DIR}/unused_type_warning/UnusedChoice.cpp
    ${ZSERIO_GEN_DIR}/unused_type_warning/UnusedChoice.h
    ${ZSERIO_GEN_DIR}/unused_type_warning/UnusedEnumeration.cpp
    ${ZSERIO_GEN_DIR}/unused_type_warning/UnusedEnumeration.h
    ${ZSERIO_GEN_DIR}/unused_type_warning/UnusedStructure.cpp
    ${ZSERIO_GEN_DIR}/unused_type_warning/UnusedStructure.h
    ${ZSERIO_GEN_DIR}/unused_type_warning/UnusedSubtype.h
    ${ZSERIO_GEN_DIR}/unused_type_warning/UnusedTable.cpp
    ${ZSERIO_GEN_DIR}/unused_type_warning/UnusedTable.h
    ${ZSERIO_GEN_DIR}/unused_type_warning/UnusedUnion.cpp
    ${ZSERIO_GEN_DIR}/unused_type_warning/UnusedUnion.h
)

zserio_add_library(TARGET unused_type_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE unused_type_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withUnusedWarnings -withoutSourcesAmalgamation
    EXPECTED_WARNINGS 6
    ZSERIO_LOG_FILENAME zserio_log.txt
)

target_include_directories(unused_type_warning_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_test_sources(
    cpp/UnusedTypeWarningTest.cpp
)

add_test_dependencies(unused_type_warning_zs)
