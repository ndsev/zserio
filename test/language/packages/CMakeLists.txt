set(ZSERIO_GEN_DIR_PACKAGES ${CMAKE_CURRENT_BINARY_DIR}/gen_packages)
set(ZSERIO_GEN_DIR_EMPTY_FILE ${CMAKE_CURRENT_BINARY_DIR}/gen_empty_file)
set(ZSERIO_GEN_DIR_DEFAULT_PACKAGE ${CMAKE_CURRENT_BINARY_DIR}/gen_default_package)
set(ZSERIO_GEN_DIR_PACKAGE_NAME_CONFLICT ${CMAKE_CURRENT_BINARY_DIR}/gen_package_name_conflict)

set(ZSERIO_GENERATED_SOURCES_PACKAGES
    ${ZSERIO_GEN_DIR_PACKAGES}/ambiguous_imports/first_colour/Colour.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/ambiguous_imports/first_colour/Colour.h
    ${ZSERIO_GEN_DIR_PACKAGES}/ambiguous_imports/second_colour/Colour.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/ambiguous_imports/second_colour/Colour.h
    ${ZSERIO_GEN_DIR_PACKAGES}/ambiguous_imports/top/TopStructure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/ambiguous_imports/top/TopStructure.h

    ${ZSERIO_GEN_DIR_PACKAGES}/cyclic_imports/bottom/BottomColour.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/cyclic_imports/bottom/BottomColour.h
    ${ZSERIO_GEN_DIR_PACKAGES}/cyclic_imports/bottom/BottomStructure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/cyclic_imports/bottom/BottomStructure.h
    ${ZSERIO_GEN_DIR_PACKAGES}/cyclic_imports/middle/MiddleStructure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/cyclic_imports/middle/MiddleStructure.h
    ${ZSERIO_GEN_DIR_PACKAGES}/cyclic_imports/top/GREEN.h
    ${ZSERIO_GEN_DIR_PACKAGES}/cyclic_imports/top/TopStructure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/cyclic_imports/top/TopStructure.h

    ${ZSERIO_GEN_DIR_PACKAGES}/import_name_conflict/colour/Colour.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/import_name_conflict/colour/Colour.h
    ${ZSERIO_GEN_DIR_PACKAGES}/import_name_conflict/top/TopStructure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/import_name_conflict/top/TopStructure.h

    ${ZSERIO_GEN_DIR_PACKAGES}/package_import/first/Coordinate.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/package_import/first/Coordinate.h
    ${ZSERIO_GEN_DIR_PACKAGES}/package_import/second/Coordinate.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/package_import/second/Coordinate.h
    ${ZSERIO_GEN_DIR_PACKAGES}/package_import/top/Coordinate.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/package_import/top/Coordinate.h
    ${ZSERIO_GEN_DIR_PACKAGES}/package_import/top/TopStructure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/package_import/top/TopStructure.h

    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_conflict/imported/CONST_A.h
    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_conflict/imported/CONST_B.h
    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_conflict/imported/SimpleParamStructure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_conflict/imported/SimpleParamStructure.h
    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_conflict/imported/SimpleStructure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_conflict/imported/SimpleStructure.h
    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_conflict/top/CONST_A.h
    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_conflict/top/SimpleParamStructure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_conflict/top/SimpleParamStructure.h
    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_conflict/top/TopStructure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_conflict/top/TopStructure.h

    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_precedence/pkg/CONST_A.h
    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_precedence/pkg/Structure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_precedence/pkg/Structure.h
    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_precedence/single/CONST_A.h
    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_precedence/single/Structure.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_precedence/single/Structure.h
    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_precedence/top/Parameterized.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_precedence/top/Parameterized.h
    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_precedence/top/SingleImportPrecedence.cpp
    ${ZSERIO_GEN_DIR_PACKAGES}/single_import_precedence/top/SingleImportPrecedence.h
)

set(ZSERIO_GENERATED_SOURCES_DEFAULT_PACKAGE
    ${ZSERIO_GEN_DIR_DEFAULT_PACKAGE}/DefaultPackageStructure.cpp
    ${ZSERIO_GEN_DIR_DEFAULT_PACKAGE}/DefaultPackageStructure.h
    ${ZSERIO_GEN_DIR_DEFAULT_PACKAGE}/Child.cpp
    ${ZSERIO_GEN_DIR_DEFAULT_PACKAGE}/Child.h
    ${ZSERIO_GEN_DIR_DEFAULT_PACKAGE}/default_package_import/top/TopStructure.cpp
    ${ZSERIO_GEN_DIR_DEFAULT_PACKAGE}/default_package_import/top/TopStructure.h
)

set(ZSERIO_GENERATED_SOURCES_PACKAGE_NAME_CONFLICT
    ${ZSERIO_GEN_DIR_PACKAGE_NAME_CONFLICT}/package_name_conflict/PackageNameConflict.cpp
    ${ZSERIO_GEN_DIR_PACKAGE_NAME_CONFLICT}/package_name_conflict/PackageNameConflict.h
    ${ZSERIO_GEN_DIR_PACKAGE_NAME_CONFLICT}/package_name_conflict/PackageNameConflictInner.cpp
    ${ZSERIO_GEN_DIR_PACKAGE_NAME_CONFLICT}/package_name_conflict/PackageNameConflictInner.h
    ${ZSERIO_GEN_DIR_PACKAGE_NAME_CONFLICT}/package_name_conflict/Blob.cpp
    ${ZSERIO_GEN_DIR_PACKAGE_NAME_CONFLICT}/package_name_conflict/Blob.h
    ${ZSERIO_GEN_DIR_PACKAGE_NAME_CONFLICT}/package_name_conflict/package_name_conflict/Blob.cpp
    ${ZSERIO_GEN_DIR_PACKAGE_NAME_CONFLICT}/package_name_conflict/package_name_conflict/Blob.h
)

zserio_add_library(TARGET packages_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE packages.zs
    OUT_DIR ${ZSERIO_GEN_DIR_PACKAGES}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES_PACKAGES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

zserio_add_library(TARGET empty_file_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE empty_file.zs
    OUT_DIR ${ZSERIO_GEN_DIR_EMPTY_FILE}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
)

zserio_add_library(TARGET default_package_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE default_package.zs
    OUT_DIR ${ZSERIO_GEN_DIR_DEFAULT_PACKAGE}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES_DEFAULT_PACKAGE}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

zserio_add_library(TARGET package_name_conflict_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE package_name_conflict.zs
    OUT_DIR ${ZSERIO_GEN_DIR_PACKAGE_NAME_CONFLICT}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES_PACKAGE_NAME_CONFLICT}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/DefaultPackageTest.cpp
    cpp/PackageNameConflictTest.cpp
)

add_test_dependencies(packages_zs)
add_test_dependencies(default_package_zs)
add_test_dependencies(package_name_conflict_zs)
