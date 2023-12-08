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

    ${ZSERIO_GEN_DIR}/array_types/auto_array_bitfield_param/ParameterizedBitfieldLength.cpp
    ${ZSERIO_GEN_DIR}/array_types/auto_array_bitfield_param/ParameterizedBitfieldLength.h

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

    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_bitfield_param/ParameterizedBitfieldLength.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_bitfield_param/ParameterizedBitfieldLength.h

    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_empty_compounds/EmptyChoice.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_empty_compounds/EmptyChoice.h
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_empty_compounds/EmptyStruct.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_empty_compounds/EmptyStruct.h
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_empty_compounds/EmptyUnion.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_empty_compounds/EmptyUnion.h
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_empty_compounds/Main.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_empty_compounds/Main.h
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_empty_compounds/PackedAutoArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_empty_compounds/PackedAutoArray.h

    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_removed_enum_item/PackedAutoArrayRemovedEnumItem.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_removed_enum_item/PackedAutoArrayRemovedEnumItem.h
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_removed_enum_item/Traffic.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_removed_enum_item/Traffic.h

    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_struct_recursion/PackedAutoArrayRecursion.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_struct_recursion/PackedAutoArrayRecursion.h

    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_struct_with_bytes/PackedAutoArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_struct_with_bytes/PackedAutoArray.h
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_struct_with_bytes/TestStructure.cpp
    ${ZSERIO_GEN_DIR}/array_types/packed_auto_array_struct_with_bytes/TestStructure.h

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

    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/PackingInterfaceOptimization.cpp
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/PackingInterfaceOptimization.h
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/UnpackedColorBitmask.cpp
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/UnpackedColorBitmask.h
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/UnpackedColorEnum.cpp
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/UnpackedColorEnum.h
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/UnpackedColorChoice.cpp
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/UnpackedColorChoice.h
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/UnpackedColorsHolder.cpp
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/UnpackedColorsHolder.h
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/UnpackedColorStruct.cpp
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/UnpackedColorStruct.h
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/UnpackedColorUnion.cpp
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/UnpackedColorUnion.h
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/MixedColorBitmask.cpp
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/MixedColorBitmask.h
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/MixedColorEnum.cpp
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/MixedColorEnum.h
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/MixedColorChoice.cpp
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/MixedColorChoice.h
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/MixedColorStruct.cpp
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/MixedColorStruct.h
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/MixedColorUnion.cpp
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/MixedColorUnion.h
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/PackedColorBitmask.cpp
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/PackedColorBitmask.h
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/PackedColorEnum.cpp
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/PackedColorEnum.h
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/PackedColorChoice.cpp
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/PackedColorChoice.h
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/PackedColorsHolder.cpp
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/PackedColorsHolder.h
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/PackedColorStruct.cpp
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/PackedColorStruct.h
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/PackedColorUnion.cpp
    ${ZSERIO_GEN_DIR}/array_types/packing_interface_optimization/PackedColorUnion.h

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

    ${ZSERIO_GEN_DIR}/array_types/variable_array_ternary_operator/VariableArray.cpp
    ${ZSERIO_GEN_DIR}/array_types/variable_array_ternary_operator/VariableArray.h
    ${ZSERIO_GEN_DIR}/array_types/variable_array_ternary_operator/VariableArrayElement.cpp
    ${ZSERIO_GEN_DIR}/array_types/variable_array_ternary_operator/VariableArrayElement.h
)

zserio_add_library(array_types_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE array_types.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

if (MSVC)
    set_property(
        SOURCE
            ${ZSERIO_GEN_DIR}/array_types/arrays_mapping/ArraysMapping.cpp
        APPEND
            PROPERTY COMPILE_OPTIONS /bigobj
    )
endif ()

add_custom_test(array_types
    DEPENDS
        array_types_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ArraysMappingTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/AutoArrayBitfieldParamTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/AutoArrayStructRecursionTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/AutoArraySubtypedUInt8Test.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/AutoArrayUInt8Test.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/FixedArrayUInt8Test.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedArraysMappingTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedAutoArrayBitfieldParamTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedAutoArrayEmptyCompoundsTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedAutoArrayRemovedEnumItemTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedAutoArrayStructRecursionTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedAutoArrayStructWithBytesTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedAutoArrayStructWithExternTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedAutoArrayStructWithUnpackedFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedAutoArrayUInt8Test.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedFixedArrayUInt8Test.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedVariableArrayStructRecursionTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedVariableArrayStructTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedVariableArrayUInt8Test.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackingInterfaceOptimizationTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/VariableArrayStructCastInt8Test.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/VariableArrayStructCastVarUIntTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/VariableArraySubtypedStructTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/VariableArrayTernaryOperatorTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
