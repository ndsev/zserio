set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/without_writer_code/ExtraParamUnion.cpp
    ${ZSERIO_GEN_DIR}/without_writer_code/ExtraParamUnion.h
    ${ZSERIO_GEN_DIR}/without_writer_code/GeoMapTable.cpp
    ${ZSERIO_GEN_DIR}/without_writer_code/GeoMapTable.h
    ${ZSERIO_GEN_DIR}/without_writer_code/ItemChoice.cpp
    ${ZSERIO_GEN_DIR}/without_writer_code/ItemChoice.h
    ${ZSERIO_GEN_DIR}/without_writer_code/ItemChoiceHolder.cpp
    ${ZSERIO_GEN_DIR}/without_writer_code/ItemChoiceHolder.h
    ${ZSERIO_GEN_DIR}/without_writer_code/Item.cpp
    ${ZSERIO_GEN_DIR}/without_writer_code/Item.h
    ${ZSERIO_GEN_DIR}/without_writer_code/ItemType.cpp
    ${ZSERIO_GEN_DIR}/without_writer_code/ItemType.h
    ${ZSERIO_GEN_DIR}/without_writer_code/Tile.cpp
    ${ZSERIO_GEN_DIR}/without_writer_code/Tile.h
    ${ZSERIO_GEN_DIR}/without_writer_code/VersionAvailability.cpp
    ${ZSERIO_GEN_DIR}/without_writer_code/VersionAvailability.h
    ${ZSERIO_GEN_DIR}/without_writer_code/WorldDb.cpp
    ${ZSERIO_GEN_DIR}/without_writer_code/WorldDb.h
)

zserio_add_library(without_writer_code_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE without_writer_code.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutWriterCode -withoutSourcesAmalgamation -withTypeInfoCode -withReflectionCode
)

target_include_directories(without_writer_code_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_custom_test(without_writer_code
    DEPENDS
        without_writer_code_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithoutWriterCodeTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
