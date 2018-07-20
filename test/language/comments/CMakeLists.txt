set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(DOC_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/doc)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/Direction.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/Direction.h
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/DirectionStructure.cpp
    ${ZSERIO_GEN_DIR}/comments/documentation_comments/DirectionStructure.h
)

zserio_add_library(TARGET comments_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE comments.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -doc ${DOC_GEN_DIR} -withoutSourcesAmalgamation -withWriterCode
)

add_test_sources(
)

add_test_dependencies(comments_zs)
