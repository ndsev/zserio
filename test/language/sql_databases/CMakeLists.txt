set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/sql_databases/simple_db/GeoMapTable.cpp
    ${ZSERIO_GEN_DIR}/sql_databases/simple_db/GeoMapTable.h
    ${ZSERIO_GEN_DIR}/sql_databases/simple_db/GeoMapTableRow.cpp
    ${ZSERIO_GEN_DIR}/sql_databases/simple_db/GeoMapTableRow.h
    ${ZSERIO_GEN_DIR}/sql_databases/simple_db/Tile.cpp
    ${ZSERIO_GEN_DIR}/sql_databases/simple_db/Tile.h
    ${ZSERIO_GEN_DIR}/sql_databases/simple_db/WorldDb.cpp
    ${ZSERIO_GEN_DIR}/sql_databases/simple_db/WorldDb.h

    ${ZSERIO_GEN_DIR}/sql_databases/db_with_relocation/AmericaDb.cpp
    ${ZSERIO_GEN_DIR}/sql_databases/db_with_relocation/AmericaDb.h
    ${ZSERIO_GEN_DIR}/sql_databases/db_with_relocation/CountryMapTable.cpp
    ${ZSERIO_GEN_DIR}/sql_databases/db_with_relocation/CountryMapTable.h
    ${ZSERIO_GEN_DIR}/sql_databases/db_with_relocation/CountryMapTableRow.cpp
    ${ZSERIO_GEN_DIR}/sql_databases/db_with_relocation/CountryMapTableRow.h
    ${ZSERIO_GEN_DIR}/sql_databases/db_with_relocation/EuropeDb.cpp
    ${ZSERIO_GEN_DIR}/sql_databases/db_with_relocation/EuropeDb.h
    ${ZSERIO_GEN_DIR}/sql_databases/db_with_relocation/Tile.cpp
    ${ZSERIO_GEN_DIR}/sql_databases/db_with_relocation/Tile.h

    ${ZSERIO_GEN_DIR}/sql_databases/IParameterProvider.h
    ${ZSERIO_GEN_DIR}/sql_databases/MasterDatabase.cpp
    ${ZSERIO_GEN_DIR}/sql_databases/MasterDatabase.h
)

zserio_add_library(TARGET sql_databases_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE sql_databases.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation -withWriterCode
)

add_test_sources(
    cpp/MasterDatabaseTest.cpp
    cpp/SimpleDbTest.cpp
)

add_test_dependencies(sql_databases_zs)
