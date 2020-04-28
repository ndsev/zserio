set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GEN_DEFAULT_PACKAGE_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_default_package)

set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/with_sources_amalgamation/WithSourcesAmalgamation.cpp
    ${ZSERIO_GEN_DIR}/with_sources_amalgamation/GeoMapTable.h
    ${ZSERIO_GEN_DIR}/with_sources_amalgamation/WorldDb.h

    ${ZSERIO_GEN_DIR}/_imported_tile_/ImportedTile.cpp
    ${ZSERIO_GEN_DIR}/_imported_tile_/Tile.h

    ${ZSERIO_GEN_DIR}/___/Amalgamation.cpp
    ${ZSERIO_GEN_DIR}/___/Empty.h
)

set(ZSERIO_GENERATED_SOURCES_DEFAULT_PACKAGE
    ${ZSERIO_GEN_DEFAULT_PACKAGE_DIR}/DefaultPackageAmalgamation.cpp

    ${ZSERIO_GEN_DEFAULT_PACKAGE_DIR}/AmalgamationStructure.h
)

zserio_add_library(TARGET with_sources_amalgamation_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE with_sources_amalgamation.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withSourcesAmalgamation
)

zserio_add_library(TARGET default_package_amalgamation_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE default_package_amalgamation.zs
    OUT_DIR ${ZSERIO_GEN_DEFAULT_PACKAGE_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES_DEFAULT_PACKAGE}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withSourcesAmalgamation
)

target_include_directories(with_sources_amalgamation_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_test_sources(
    cpp/WithSourcesAmalgamationTest.cpp
)

add_test_dependencies(with_sources_amalgamation_zs)
add_test_dependencies(default_package_amalgamation_zs)
