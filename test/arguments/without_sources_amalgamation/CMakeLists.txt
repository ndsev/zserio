set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/without_sources_amalgamation/WithoutSourcesAmalgamation.cpp

    ${ZSERIO_GEN_DIR}/without_sources_amalgamation/GeoMapTable.h
    ${ZSERIO_GEN_DIR}/without_sources_amalgamation/GeoMapTableRow.h
    ${ZSERIO_GEN_DIR}/without_sources_amalgamation/IParameterProvider.h
    ${ZSERIO_GEN_DIR}/without_sources_amalgamation/Tile.h
    ${ZSERIO_GEN_DIR}/without_sources_amalgamation/WorldDb.h
)

# Actually, we are using '-withoutSourcesAmalgamation' everywhere but we don't check amalgamation
# (default behavior). Therefore, this test checks surprisingly default behavior, so no
# '-withoutSourcesAmalgamation' for ZSERIO_OPTIONS is ok.
zserio_add_library(TARGET without_sources_amalgamation_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE without_sources_amalgamation.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
)

add_test_sources(
    cpp/WithoutSourcesAmalgamationTest.cpp
)

add_test_dependencies(without_sources_amalgamation_zs)
