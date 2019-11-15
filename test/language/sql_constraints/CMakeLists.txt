set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/sql_constraints/ConstraintsConstant.h
    ${ZSERIO_GEN_DIR}/sql_constraints/ConstraintsEnum.cpp
    ${ZSERIO_GEN_DIR}/sql_constraints/ConstraintsEnum.h
    ${ZSERIO_GEN_DIR}/sql_constraints/ConstraintsTable.cpp
    ${ZSERIO_GEN_DIR}/sql_constraints/ConstraintsTable.h
    ${ZSERIO_GEN_DIR}/sql_constraints/TestDb.cpp
    ${ZSERIO_GEN_DIR}/sql_constraints/TestDb.h
    ${ZSERIO_GEN_DIR}/sql_constraints/constraints/ImportedConstant.h
    ${ZSERIO_GEN_DIR}/sql_constraints/constraints/ImportedEnum.cpp
    ${ZSERIO_GEN_DIR}/sql_constraints/constraints/ImportedEnum.h
)

if (NOT ${ZSERIO_CPP_STANDARD} STREQUAL c++11)
    set(ZSERIO_GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
        ${ZSERIO_GEN_DIR}/sql_constraints/ConstraintsTableRow.cpp
        ${ZSERIO_GEN_DIR}/sql_constraints/ConstraintsTableRow.h
    )
endif ()

zserio_add_library(TARGET sql_constraints_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE sql_constraints.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation -cppStandard ${ZSERIO_CPP_STANDARD}
)

target_include_directories(sql_constraints_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_test_sources(
    ${ZSERIO_CPP_DIR}/SqlConstraintsTest.cpp
)

add_test_dependencies(sql_constraints_zs)
