set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/with_range_check_code/bit4_range_check/Bit4RangeCheckCompound.cpp
    ${ZSERIO_GEN_DIR}/with_range_check_code/bit4_range_check/Bit4RangeCheckCompound.h

    ${ZSERIO_GEN_DIR}/with_range_check_code/choice_bit4_range_check/ChoiceBit4RangeCheckCompound.cpp
    ${ZSERIO_GEN_DIR}/with_range_check_code/choice_bit4_range_check/ChoiceBit4RangeCheckCompound.h

    ${ZSERIO_GEN_DIR}/with_range_check_code/int4_range_check/Int4RangeCheckCompound.cpp
    ${ZSERIO_GEN_DIR}/with_range_check_code/int4_range_check/Int4RangeCheckCompound.h

    ${ZSERIO_GEN_DIR}/with_range_check_code/int7_array_range_check/Int7ArrayRangeCheckCompound.cpp
    ${ZSERIO_GEN_DIR}/with_range_check_code/int7_array_range_check/Int7ArrayRangeCheckCompound.h

    ${ZSERIO_GEN_DIR}/with_range_check_code/int8_range_check/Int8RangeCheckCompound.cpp
    ${ZSERIO_GEN_DIR}/with_range_check_code/int8_range_check/Int8RangeCheckCompound.h

    ${ZSERIO_GEN_DIR}/with_range_check_code/optional_bit31_range_check/OptionalBit31RangeCheckCompound.cpp
    ${ZSERIO_GEN_DIR}/with_range_check_code/optional_bit31_range_check/OptionalBit31RangeCheckCompound.h

    ${ZSERIO_GEN_DIR}/with_range_check_code/uint8_range_check/UInt8RangeCheckCompound.cpp
    ${ZSERIO_GEN_DIR}/with_range_check_code/uint8_range_check/UInt8RangeCheckCompound.h

    ${ZSERIO_GEN_DIR}/with_range_check_code/union_int4_range_check/UnionInt4RangeCheckCompound.cpp
    ${ZSERIO_GEN_DIR}/with_range_check_code/union_int4_range_check/UnionInt4RangeCheckCompound.h

    ${ZSERIO_GEN_DIR}/with_range_check_code/variable_bit_range_check/VariableBitRangeCheckCompound.cpp
    ${ZSERIO_GEN_DIR}/with_range_check_code/variable_bit_range_check/VariableBitRangeCheckCompound.h

    ${ZSERIO_GEN_DIR}/with_range_check_code/variable_int_range_check/VariableIntRangeCheckCompound.cpp
    ${ZSERIO_GEN_DIR}/with_range_check_code/variable_int_range_check/VariableIntRangeCheckCompound.h

    ${ZSERIO_GEN_DIR}/with_range_check_code/varint16_range_check/VarInt16RangeCheckCompound.cpp
    ${ZSERIO_GEN_DIR}/with_range_check_code/varint16_range_check/VarInt16RangeCheckCompound.h

    ${ZSERIO_GEN_DIR}/with_range_check_code/varint32_range_check/VarInt32RangeCheckCompound.cpp
    ${ZSERIO_GEN_DIR}/with_range_check_code/varint32_range_check/VarInt32RangeCheckCompound.h

    ${ZSERIO_GEN_DIR}/with_range_check_code/varint64_range_check/VarInt64RangeCheckCompound.cpp
    ${ZSERIO_GEN_DIR}/with_range_check_code/varint64_range_check/VarInt64RangeCheckCompound.h

    ${ZSERIO_GEN_DIR}/with_range_check_code/varuint16_range_check/VarUInt16RangeCheckCompound.cpp
    ${ZSERIO_GEN_DIR}/with_range_check_code/varuint16_range_check/VarUInt16RangeCheckCompound.h

    ${ZSERIO_GEN_DIR}/with_range_check_code/varuint32_range_check/VarUInt32RangeCheckCompound.cpp
    ${ZSERIO_GEN_DIR}/with_range_check_code/varuint32_range_check/VarUInt32RangeCheckCompound.h

    ${ZSERIO_GEN_DIR}/with_range_check_code/varuint64_range_check/VarUInt64RangeCheckCompound.cpp
    ${ZSERIO_GEN_DIR}/with_range_check_code/varuint64_range_check/VarUInt64RangeCheckCompound.h
)

zserio_add_library(TARGET with_range_check_code_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE with_range_check_code.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withRangeCheckCode -withoutSourcesAmalgamation -cppStandard ${ZSERIO_CPP_STANDARD}
)

add_test_sources(
    ${ZSERIO_CPP_DIR}/Bit4RangeCheckTest.cpp
    ${ZSERIO_CPP_DIR}/ChoiceBit4RangeCheckTest.cpp
    ${ZSERIO_CPP_DIR}/Int4RangeCheckTest.cpp
    ${ZSERIO_CPP_DIR}/Int7ArrayRangeCheckTest.cpp
    ${ZSERIO_CPP_DIR}/Int8RangeCheckTest.cpp
    ${ZSERIO_CPP_DIR}/OptionalBit31RangeCheckTest.cpp
    ${ZSERIO_CPP_DIR}/UInt8RangeCheckTest.cpp
    ${ZSERIO_CPP_DIR}/UnionInt4RangeCheckTest.cpp
    ${ZSERIO_CPP_DIR}/VariableBitRangeCheckTest.cpp
    ${ZSERIO_CPP_DIR}/VariableIntRangeCheckTest.cpp
    ${ZSERIO_CPP_DIR}/VarInt16RangeCheckTest.cpp
    ${ZSERIO_CPP_DIR}/VarInt32RangeCheckTest.cpp
    ${ZSERIO_CPP_DIR}/VarInt64RangeCheckTest.cpp
    ${ZSERIO_CPP_DIR}/VarUInt16RangeCheckTest.cpp
    ${ZSERIO_CPP_DIR}/VarUInt32RangeCheckTest.cpp
    ${ZSERIO_CPP_DIR}/VarUInt64RangeCheckTest.cpp
)

add_test_dependencies(with_range_check_code_zs)
