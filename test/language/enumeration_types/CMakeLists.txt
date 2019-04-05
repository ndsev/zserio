set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/enumeration_types/bitfield_enum/Color.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/bitfield_enum/Color.h

    ${ZSERIO_GEN_DIR}/enumeration_types/enum_defined_by_constant/Colors.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/enum_defined_by_constant/Colors.h
    ${ZSERIO_GEN_DIR}/enumeration_types/enum_defined_by_constant/WHITE_COLOR.h

    ${ZSERIO_GEN_DIR}/enumeration_types/enum_used_by_enum/Color.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/enum_used_by_enum/Color.h
    ${ZSERIO_GEN_DIR}/enumeration_types/enum_used_by_enum/DarkColor.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/enum_used_by_enum/DarkColor.h
    ${ZSERIO_GEN_DIR}/enumeration_types/enum_used_by_enum/LightColor.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/enum_used_by_enum/LightColor.h

    ${ZSERIO_GEN_DIR}/enumeration_types/uint64_enum/DarkColor.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/uint64_enum/DarkColor.h

    ${ZSERIO_GEN_DIR}/enumeration_types/uint8_enum/DarkColor.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/uint8_enum/DarkColor.h
)

zserio_add_library(TARGET enumeration_types_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE enumeration_types.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/BitfieldEnumTest.cpp
    cpp/EnumDefinedByConstantTest.cpp
    cpp/EnumUsedByEnumTest.cpp
    cpp/UInt64EnumTest.cpp
    cpp/UInt8EnumTest.cpp
)

add_test_dependencies(enumeration_types_zs)
