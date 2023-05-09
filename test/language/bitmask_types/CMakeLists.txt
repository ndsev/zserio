set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/bitmask_types/bitfield_const_bitmask/Permission.cpp
    ${ZSERIO_GEN_DIR}/bitmask_types/bitfield_const_bitmask/Permission.h
    ${ZSERIO_GEN_DIR}/bitmask_types/bitfield_const_bitmask/NUM_BITS.h

    ${ZSERIO_GEN_DIR}/bitmask_types/bitfield_bitmask/Permission.cpp
    ${ZSERIO_GEN_DIR}/bitmask_types/bitfield_bitmask/Permission.h

    ${ZSERIO_GEN_DIR}/bitmask_types/bitmask_defined_by_constant/Permission.cpp
    ${ZSERIO_GEN_DIR}/bitmask_types/bitmask_defined_by_constant/Permission.h
    ${ZSERIO_GEN_DIR}/bitmask_types/bitmask_defined_by_constant/READ_PERMISSION.h

    ${ZSERIO_GEN_DIR}/bitmask_types/bitmask_used_by_bitmask/SimplePermission.cpp
    ${ZSERIO_GEN_DIR}/bitmask_types/bitmask_used_by_bitmask/SimplePermission.h
    ${ZSERIO_GEN_DIR}/bitmask_types/bitmask_used_by_bitmask/Permission.cpp
    ${ZSERIO_GEN_DIR}/bitmask_types/bitmask_used_by_bitmask/Permission.h
    ${ZSERIO_GEN_DIR}/bitmask_types/bitmask_used_by_bitmask/ComplexPermission.cpp
    ${ZSERIO_GEN_DIR}/bitmask_types/bitmask_used_by_bitmask/ComplexPermission.h

    ${ZSERIO_GEN_DIR}/bitmask_types/bitmask_without_none/Permission.cpp
    ${ZSERIO_GEN_DIR}/bitmask_types/bitmask_without_none/Permission.h

    ${ZSERIO_GEN_DIR}/bitmask_types/uint8_bitmask/Permission.cpp
    ${ZSERIO_GEN_DIR}/bitmask_types/uint8_bitmask/Permission.h

    ${ZSERIO_GEN_DIR}/bitmask_types/uint64_bitmask/Permission.cpp
    ${ZSERIO_GEN_DIR}/bitmask_types/uint64_bitmask/Permission.h

    ${ZSERIO_GEN_DIR}/bitmask_types/varuint_bitmask/Permission.cpp
    ${ZSERIO_GEN_DIR}/bitmask_types/varuint_bitmask/Permission.h
)

zserio_add_library(bitmask_types_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE bitmask_types.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_custom_test(bitmask_types
    DEPENDS
        bitmask_types_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitfieldBitmaskTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitfieldConstBitmaskTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitmaskDefinedByConstantTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitmaskUsedByBitmaskTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitmaskWithoutNoneTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UInt64BitmaskTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UInt8BitmaskTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/VarUIntBitmaskTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
