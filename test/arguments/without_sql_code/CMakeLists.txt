set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/without_sql_code/Tile.cpp
    ${ZSERIO_GEN_DIR}/without_sql_code/Tile.h
)

zserio_add_library(TARGET without_sql_code_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE without_sql_code.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSqlCode -withoutSourcesAmalgamation
)

add_test_sources(
    ${ZSERIO_CPP_DIR}/WithoutSqlCodeTest.cpp
)

add_test_dependencies(without_sql_code_zs)
