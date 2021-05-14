set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/array_types/arrays_mapping/ArraysMapping.cpp
    ${ZSERIO_GEN_DIR}/array_types/arrays_mapping/ArraysMapping.h
    ${ZSERIO_GEN_DIR}/array_types/arrays_mapping/TestBitmask.cpp
    ${ZSERIO_GEN_DIR}/array_types/arrays_mapping/TestBitmask.h
    ${ZSERIO_GEN_DIR}/array_types/arrays_mapping/TestEnum.cpp
    ${ZSERIO_GEN_DIR}/array_types/arrays_mapping/TestEnum.h
    ${ZSERIO_GEN_DIR}/array_types/arrays_mapping/TestStructure.cpp
    ${ZSERIO_GEN_DIR}/array_types/arrays_mapping/TestStructure.h

    ${ZSERIO_GEN_DIR}/array_types/auto_array_subtyped_uint8/ArrayElement.h
    ${ZSERIO_GEN_DIR}/array_types/auto_array_subtyped_uint8/AutoArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/auto_array_subtyped_uint8/AutoArray.h

    ${ZSERIO_GEN_DIR}/array_types/auto_array_uint8_recursion/AutoArrayRecursion.cpp
    ${ZSERIO_GEN_DIR}/array_types/auto_array_uint8_recursion/AutoArrayRecursion.h

    ${ZSERIO_GEN_DIR}/array_types/auto_array_uint8/AutoArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/auto_array_uint8/AutoArray.h

    ${ZSERIO_GEN_DIR}/array_types/fixed_array_uint8/FixedArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/fixed_array_uint8/FixedArray.h

    ${ZSERIO_GEN_DIR}/array_types/implicit_array_bit8/ImplicitArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/implicit_array_bit8/ImplicitArray.h

    ${ZSERIO_GEN_DIR}/array_types/implicit_array_int24/ImplicitArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/implicit_array_int24/ImplicitArray.h

    ${ZSERIO_GEN_DIR}/array_types/implicit_array_float16/ImplicitArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/implicit_array_float16/ImplicitArray.h

    ${ZSERIO_GEN_DIR}/array_types/implicit_array_uint8/ImplicitArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/implicit_array_uint8/ImplicitArray.h

    ${ZSERIO_GEN_DIR}/array_types/implicit_array_uint64/ImplicitArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/implicit_array_uint64/ImplicitArray.h

    ${ZSERIO_GEN_DIR}/array_types/variable_array_struct_cast_int8/VariableArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/variable_array_struct_cast_int8/VariableArray.h
    ${ZSERIO_GEN_DIR}/array_types/variable_array_struct_cast_int8/TestStructure.cpp
    ${ZSERIO_GEN_DIR}/array_types/variable_array_struct_cast_int8/TestStructure.h

    ${ZSERIO_GEN_DIR}/array_types/variable_array_struct_cast_varuint/VariableArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/variable_array_struct_cast_varuint/VariableArray.h
    ${ZSERIO_GEN_DIR}/array_types/variable_array_struct_cast_varuint/TestStructure.cpp
    ${ZSERIO_GEN_DIR}/array_types/variable_array_struct_cast_varuint/TestStructure.h

    ${ZSERIO_GEN_DIR}/array_types/variable_array_subtyped_struct/ArrayElement.h
    ${ZSERIO_GEN_DIR}/array_types/variable_array_subtyped_struct/VariableArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/variable_array_subtyped_struct/VariableArray.h
    ${ZSERIO_GEN_DIR}/array_types/variable_array_subtyped_struct/TestStructure.cpp
    ${ZSERIO_GEN_DIR}/array_types/variable_array_subtyped_struct/TestStructure.h
)

zserio_add_library(TARGET array_types_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE array_types.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/ArraysMappingTest.cpp
    cpp/AutoArraySubtypedUInt8Test.cpp
    cpp/AutoArrayUInt8RecursionTest.cpp
    cpp/AutoArrayUInt8Test.cpp
    cpp/FixedArrayUInt8Test.cpp
    cpp/ImplicitArrayBit8Test.cpp
    cpp/ImplicitArrayInt24Test.cpp
    cpp/ImplicitArrayFloat16Test.cpp
    cpp/ImplicitArrayUInt8Test.cpp
    cpp/ImplicitArrayUInt64Test.cpp
    cpp/VariableArrayStructCastInt8Test.cpp
    cpp/VariableArrayStructCastVarUIntTest.cpp
    cpp/VariableArraySubtypedStructTest.cpp
)

add_test_dependencies(array_types_zs)
