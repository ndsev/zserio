set(ZSERIO_GEN_DIR_PACKAGES ${CMAKE_CURRENT_BINARY_DIR}/gen_packages)
set(ZSERIO_GEN_DIR_EMPTY_FILE ${CMAKE_CURRENT_BINARY_DIR}/gen_empty_file)
set(ZSERIO_GEN_DIR_DEFAULT_PACKAGE ${CMAKE_CURRENT_BINARY_DIR}/gen_default_package)

set(ZSERIO_GENERATED_SOURCES_PACKAGES
    ${ZSERIO_GEN_DIR_PACKAGES}/cyclic_imports/bottom/BottomColour.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/cyclic_imports/bottom/BottomColour.h
    ${ZSERIO_GEN_DIR_PACKAGES}/cyclic_imports/bottom/BottomStructure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/cyclic_imports/bottom/BottomStructure.h
    ${ZSERIO_GEN_DIR_PACKAGES}/cyclic_imports/middle/MiddleStructure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/cyclic_imports/middle/MiddleStructure.h
    ${ZSERIO_GEN_DIR_PACKAGES}/cyclic_imports/top/TopStructure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/cyclic_imports/top/TopStructure.h

    ${ZSERIO_GEN_DIR_PACKAGES}/package_import/first/Coordinate.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/package_import/first/Coordinate.h
    ${ZSERIO_GEN_DIR_PACKAGES}/package_import/second/Coordinate.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/package_import/second/Coordinate.h
    ${ZSERIO_GEN_DIR_PACKAGES}/package_import/top/Coordinate.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/package_import/top/Coordinate.h
    ${ZSERIO_GEN_DIR_PACKAGES}/package_import/top/TopStructure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/package_import/top/TopStructure.h

    ${ZSERIO_GEN_DIR_PACKAGES}/single_type_import/imported/SimpleParamStructure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/single_type_import/imported/SimpleParamStructure.h
    ${ZSERIO_GEN_DIR_PACKAGES}/single_type_import/imported/SimpleStructure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/single_type_import/imported/SimpleStructure.h
    ${ZSERIO_GEN_DIR_PACKAGES}/single_type_import/top/SimpleParamStructure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/single_type_import/top/SimpleParamStructure.h
    ${ZSERIO_GEN_DIR_PACKAGES}/single_type_import/top/TopStructure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/single_type_import/top/TopStructure.h
)

set(ZSERIO_GENERATED_SOURCES_DEFAULT_PACKAGE
    ${ZSERIO_GEN_DIR_DEFAULT_PACKAGE}/DefaultPackageStructure.cpp
    ${ZSERIO_GEN_DIR_DEFAULT_PACKAGE}/DefaultPackageStructure.h
    ${ZSERIO_GEN_DIR_DEFAULT_PACKAGE}/Child.cpp
    ${ZSERIO_GEN_DIR_DEFAULT_PACKAGE}/Child.h
    ${ZSERIO_GEN_DIR_DEFAULT_PACKAGE}/default_package_import/top/TopStructure.cpp
    ${ZSERIO_GEN_DIR_DEFAULT_PACKAGE}/default_package_import/top/TopStructure.h
)

zserio_add_library(TARGET packages_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE packages.zs
    OUT_DIR ${ZSERIO_GEN_DIR_PACKAGES}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES_PACKAGES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation -cppStandard ${ZSERIO_CPP_STANDARD}
)

zserio_add_library(TARGET empty_file_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE empty_file.zs
    OUT_DIR ${ZSERIO_GEN_DIR_EMPTY_FILE}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -cppStandard ${ZSERIO_CPP_STANDARD}
)

zserio_add_library(TARGET default_package_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE default_package.zs
    OUT_DIR ${ZSERIO_GEN_DIR_DEFAULT_PACKAGE}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES_DEFAULT_PACKAGE}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation -cppStandard ${ZSERIO_CPP_STANDARD}
)

add_test_sources(
    ${ZSERIO_CPP_DIR}/DefaultPackageTest.cpp
)

add_test_dependencies(packages_zs)
add_test_dependencies(default_package_zs)
