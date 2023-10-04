set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/enumeration_types/bitfield_const_enum/Color.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/bitfield_const_enum/Color.h
    ${ZSERIO_GEN_DIR}/enumeration_types/bitfield_const_enum/NUM_ENUM_BITS.h

    ${ZSERIO_GEN_DIR}/enumeration_types/bitfield_enum/Color.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/bitfield_enum/Color.h

    ${ZSERIO_GEN_DIR}/enumeration_types/deprecated_enum_item/Traffic.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/deprecated_enum_item/Traffic.h

    ${ZSERIO_GEN_DIR}/enumeration_types/enum_defined_by_constant/Colors.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/enum_defined_by_constant/Colors.h
    ${ZSERIO_GEN_DIR}/enumeration_types/enum_defined_by_constant/WHITE_COLOR.h

    ${ZSERIO_GEN_DIR}/enumeration_types/enum_used_by_enum/Color.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/enum_used_by_enum/Color.h
    ${ZSERIO_GEN_DIR}/enumeration_types/enum_used_by_enum/DarkColor.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/enum_used_by_enum/DarkColor.h
    ${ZSERIO_GEN_DIR}/enumeration_types/enum_used_by_enum/LightColor.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/enum_used_by_enum/LightColor.h

    ${ZSERIO_GEN_DIR}/enumeration_types/multiple_removed_enum_items/AllocatorType.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/multiple_removed_enum_items/AllocatorType.h
    ${ZSERIO_GEN_DIR}/enumeration_types/multiple_removed_enum_items/Traffic.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/multiple_removed_enum_items/Traffic.h

    ${ZSERIO_GEN_DIR}/enumeration_types/removed_enum_item/AllocatorType.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/removed_enum_item/AllocatorType.h
    ${ZSERIO_GEN_DIR}/enumeration_types/removed_enum_item/Traffic.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/removed_enum_item/Traffic.h

    ${ZSERIO_GEN_DIR}/enumeration_types/uint64_enum/DarkColor.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/uint64_enum/DarkColor.h

    ${ZSERIO_GEN_DIR}/enumeration_types/uint8_enum/DarkColor.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/uint8_enum/DarkColor.h

    ${ZSERIO_GEN_DIR}/enumeration_types/varuint_enum/DarkColor.cpp
    ${ZSERIO_GEN_DIR}/enumeration_types/varuint_enum/DarkColor.h
)

zserio_add_library(enumeration_types_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE enumeration_types.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation -withTypeInfoCode
)

add_custom_test(enumeration_types
    DEPENDS
        enumeration_types_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitfieldConstEnumTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitfieldEnumTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/DeprecatedEnumItemTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/EnumDefinedByConstantTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/EnumUsedByEnumTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/MultipleRemovedEnumItemsTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/RemovedEnumItemTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UInt64EnumTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UInt8EnumTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/VarUIntEnumTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
