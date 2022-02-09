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

    ${ZSERIO_GEN_DIR}/array_types/auto_array_struct_recursion/AutoArrayRecursion.cpp
    ${ZSERIO_GEN_DIR}/array_types/auto_array_struct_recursion/AutoArrayRecursion.h

    ${ZSERIO_GEN_DIR}/array_types/auto_array_subtyped_uint8/ArrayElement.h
    ${ZSERIO_GEN_DIR}/array_types/auto_array_subtyped_uint8/AutoArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/auto_array_subtyped_uint8/AutoArray.h

    ${ZSERIO_GEN_DIR}/array_types/auto_array_uint8/AutoArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/auto_array_uint8/AutoArray.h

    ${ZSERIO_GEN_DIR}/array_types/fixed_array_uint8/FixedArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/fixed_array_uint8/FixedArray.h

    ${ZSERIO_GEN_DIR}/array_types/packed_arrays_mapping/TestStructure.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_arrays_mapping/TestStructure.h
    ${ZSERIO_GEN_DIR}/array_types/packed_arrays_mapping/PackedArraysMapping.h
    ${ZSERIO_GEN_DIR}/array_types/packed_arrays_mapping/TestBitmask.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_arrays_mapping/TestEnum.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_arrays_mapping/TestBitmask.h
    ${ZSERIO_GEN_DIR}/array_types/packed_arrays_mapping/PackedArraysMapping.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_arrays_mapping/TestEnum.h

    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_struct_recursion/PackedAutoArrayRecursion.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_struct_recursion/PackedAutoArrayRecursion.h

    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_struct_with_extern/PackedAutoArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_struct_with_extern/PackedAutoArray.h
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_struct_with_extern/TestStructure.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_struct_with_extern/TestStructure.h

    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_struct_with_unpacked_field/PackedAutoArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_struct_with_unpacked_field/PackedAutoArray.h
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_struct_with_unpacked_field/TestStructure.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_struct_with_unpacked_field/TestStructure.h

    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_uint8/PackedAutoArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_uint8/PackedAutoArray.h

    ${ZSERIO_GEN_DIR}/array_types/packed_fixed_array_uint8/PackedFixedArray.h
    ${ZSERIO_GEN_DIR}/array_types/packed_fixed_array_uint8/PackedFixedArray.cpp

    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/Bit15.h
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/BITS_PER_BYTE.h
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/Empty.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/Empty.h
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/PackedVariableArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/PackedVariableArray.h
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/TestUnpackedArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/TestUnpackedArray.h
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/TestStructure.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/TestStructure.h
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/TestBitmask.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/TestEnum.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/TestBitmask.h
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/TestChoice.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/TestChoice.h
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/TestEnum.h
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/TestUnion.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/TestUnion.h
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/TestPackedArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/TestPackedArray.h
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/Value32.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct/Value32.h

    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct_recursion/PackedVariableArray.h
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct_recursion/PackedVariableArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct_recursion/Block.h
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_struct_recursion/Block.cpp

    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_uint8/PackedVariableArray.h
    ${ZSERIO_GEN_DIR}/array_types/packed_variable_array_uint8/PackedVariableArray.cpp

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
    cpp/AutoArrayStructRecursionTest.cpp
    cpp/AutoArraySubtypedUInt8Test.cpp
    cpp/AutoArrayUInt8Test.cpp
    cpp/FixedArrayUInt8Test.cpp
    cpp/PackedArraysMappingTest.cpp
    cpp/PackedAutoArrayStructRecursionTest.cpp
    cpp/PackedAutoArrayStructWithExternTest.cpp
    cpp/PackedAutoArrayStructWithUnpackedFieldTest.cpp
    cpp/PackedAutoArrayUInt8Test.cpp
    cpp/PackedFixedArrayUInt8Test.cpp
    cpp/PackedVariableArrayStructRecursionTest.cpp
    cpp/PackedVariableArrayStructTest.cpp
    cpp/PackedVariableArrayUInt8Test.cpp
    cpp/VariableArrayStructCastInt8Test.cpp
    cpp/VariableArrayStructCastVarUIntTest.cpp
    cpp/VariableArraySubtypedStructTest.cpp
)

add_test_dependencies(array_types_zs)
