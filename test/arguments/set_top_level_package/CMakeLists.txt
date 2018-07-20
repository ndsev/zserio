set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/company/appl/set_top_level_package/SimpleStructure.cpp
    ${ZSERIO_GEN_DIR}/company/appl/set_top_level_package/SimpleStructure.h
)

zserio_add_library(TARGET set_top_level_package_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE set_top_level_package.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS "-setTopLevelPackage" "company.appl" "-withoutSourcesAmalgamation" "-withWriterCode"
)

add_test_sources(
    cpp/SetTopLevelPackageTest.cpp
)

add_test_dependencies(set_top_level_package_zs)
