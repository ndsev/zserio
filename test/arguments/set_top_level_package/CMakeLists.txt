set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/company/appl/set_top_level_package/constant/CONST.h

    ${ZSERIO_GEN_DIR}/company/appl/set_top_level_package/enumeration/Enumeration.cpp
    ${ZSERIO_GEN_DIR}/company/appl/set_top_level_package/enumeration/Enumeration.h

    ${ZSERIO_GEN_DIR}/company/appl/SimpleChoice.cpp
    ${ZSERIO_GEN_DIR}/company/appl/SimpleChoice.h
    ${ZSERIO_GEN_DIR}/company/appl/SimpleStructure.cpp
    ${ZSERIO_GEN_DIR}/company/appl/SimpleStructure.h
    ${ZSERIO_GEN_DIR}/company/appl/SimpleTemplate_Enumeration.cpp
    ${ZSERIO_GEN_DIR}/company/appl/SimpleTemplate_Enumeration.h
    ${ZSERIO_GEN_DIR}/company/appl/U8.h
)

zserio_add_library(TARGET set_top_level_package_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE set_top_level_package.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -setTopLevelPackage company.appl -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/SetTopLevelPackageTest.cpp
)

add_test_dependencies(set_top_level_package_zs)
