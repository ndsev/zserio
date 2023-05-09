set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/sql_constraints/constraint_imports/IMPORTED_CONSTRAINTS_CONSTANT.h

    ${ZSERIO_GEN_DIR}/sql_constraints/field_constraints/CONSTRAINTS_CONSTANT.h
    ${ZSERIO_GEN_DIR}/sql_constraints/field_constraints/FieldConstraintsTable.cpp
    ${ZSERIO_GEN_DIR}/sql_constraints/field_constraints/FieldConstraintsTable.h
    ${ZSERIO_GEN_DIR}/sql_constraints/field_constraints/NOT_NULL_CONSTRAINT.h

    ${ZSERIO_GEN_DIR}/sql_constraints/table_constraints/TableConstraintsTable.cpp
    ${ZSERIO_GEN_DIR}/sql_constraints/table_constraints/TableConstraintsTable.h

    ${ZSERIO_GEN_DIR}/sql_constraints/TestDb.cpp
    ${ZSERIO_GEN_DIR}/sql_constraints/TestDb.h
)

zserio_add_library(sql_constraints_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE sql_constraints.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

target_include_directories(sql_constraints_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_custom_test(sql_constraints
    DEPENDS
        sql_constraints_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/FieldConstraintsTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/TableConstraintsTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
