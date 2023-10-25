set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    #${ZSERIO_GEN_DIR}/parameterized_types/array_element_param/Block.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/array_element_param/Block.h
    #${ZSERIO_GEN_DIR}/parameterized_types/array_element_param/BlockHeader.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/array_element_param/BlockHeader.h
    #${ZSERIO_GEN_DIR}/parameterized_types/array_element_param/Database.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/array_element_param/Database.h

    #${ZSERIO_GEN_DIR}/parameterized_types/bytes_param/BytesParam.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/bytes_param/BytesParam.h
    #${ZSERIO_GEN_DIR}/parameterized_types/bytes_param/Parameterized.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/bytes_param/Parameterized.h

    #${ZSERIO_GEN_DIR}/parameterized_types/dynamic_bitfield_param/DynamicBitfieldParam.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/dynamic_bitfield_param/DynamicBitfieldParam.h
    #${ZSERIO_GEN_DIR}/parameterized_types/dynamic_bitfield_param/DynamicBitfieldParamHolder.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/dynamic_bitfield_param/DynamicBitfieldParamHolder.h

    #${ZSERIO_GEN_DIR}/parameterized_types/extern_param/ExternParam.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/extern_param/ExternParam.h
    #${ZSERIO_GEN_DIR}/parameterized_types/extern_param/Parameterized.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/extern_param/Parameterized.h

    #${ZSERIO_GEN_DIR}/parameterized_types/fixed_and_variable_param/Access.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/fixed_and_variable_param/Access.h
    #${ZSERIO_GEN_DIR}/parameterized_types/fixed_and_variable_param/ArrayHolder.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/fixed_and_variable_param/ArrayHolder.h
    #${ZSERIO_GEN_DIR}/parameterized_types/fixed_and_variable_param/Color.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/fixed_and_variable_param/Color.h
    #${ZSERIO_GEN_DIR}/parameterized_types/fixed_and_variable_param/FixedAndVariableParam.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/fixed_and_variable_param/FixedAndVariableParam.h
    #${ZSERIO_GEN_DIR}/parameterized_types/fixed_and_variable_param/LimitHolder.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/fixed_and_variable_param/LimitHolder.h

    #${ZSERIO_GEN_DIR}/parameterized_types/grand_child_param/GrandChildParam.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/grand_child_param/GrandChildParam.h
    #${ZSERIO_GEN_DIR}/parameterized_types/grand_child_param/Item.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/grand_child_param/Item.h
    #${ZSERIO_GEN_DIR}/parameterized_types/grand_child_param/ItemChoice.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/grand_child_param/ItemChoice.h
    #${ZSERIO_GEN_DIR}/parameterized_types/grand_child_param/ItemChoiceHolder.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/grand_child_param/ItemChoiceHolder.h

    #${ZSERIO_GEN_DIR}/parameterized_types/packed_array_element_param/Block.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/packed_array_element_param/Block.h
    #${ZSERIO_GEN_DIR}/parameterized_types/packed_array_element_param/BlockHeader.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/packed_array_element_param/BlockHeader.h
    #${ZSERIO_GEN_DIR}/parameterized_types/packed_array_element_param/Database.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/packed_array_element_param/Database.h

    #${ZSERIO_GEN_DIR}/parameterized_types/param_with_optional/ExtraValue.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/param_with_optional/ExtraValue.h
    #${ZSERIO_GEN_DIR}/parameterized_types/param_with_optional/Holder.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/param_with_optional/Holder.h
    #${ZSERIO_GEN_DIR}/parameterized_types/param_with_optional/Param.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/param_with_optional/Param.h
    #${ZSERIO_GEN_DIR}/parameterized_types/param_with_optional/Value.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/param_with_optional/Value.h

    #${ZSERIO_GEN_DIR}/parameterized_types/parameterized_inner_classes_clashing/Compound.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/parameterized_inner_classes_clashing/Compound.h
    #${ZSERIO_GEN_DIR}/parameterized_types/parameterized_inner_classes_clashing/ElementChildrenInitializer_array.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/parameterized_inner_classes_clashing/ElementChildrenInitializer_array.h
    #${ZSERIO_GEN_DIR}/parameterized_types/parameterized_inner_classes_clashing/ElementFactory_array.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/parameterized_inner_classes_clashing/ElementFactory_array.h
    #${ZSERIO_GEN_DIR}/parameterized_types/parameterized_inner_classes_clashing/ElementInitializer_array.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/parameterized_inner_classes_clashing/ElementInitializer_array.h
    #${ZSERIO_GEN_DIR}/parameterized_types/parameterized_inner_classes_clashing/Parent.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/parameterized_inner_classes_clashing/Parent.h

    ${ZSERIO_GEN_DIR}/parameterized_types/parameterized_param/Param.cpp
    ${ZSERIO_GEN_DIR}/parameterized_types/parameterized_param/Param.h
    ${ZSERIO_GEN_DIR}/parameterized_types/parameterized_param/ParameterizedParam.cpp
    ${ZSERIO_GEN_DIR}/parameterized_types/parameterized_param/ParameterizedParam.h
    ${ZSERIO_GEN_DIR}/parameterized_types/parameterized_param/ParameterizedParamHolder.cpp
    ${ZSERIO_GEN_DIR}/parameterized_types/parameterized_param/ParameterizedParamHolder.h

    #${ZSERIO_GEN_DIR}/parameterized_types/simple_param/Item.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/simple_param/Item.h

    #${ZSERIO_GEN_DIR}/parameterized_types/subtyped_bitfield_param/ParamType.h
    #${ZSERIO_GEN_DIR}/parameterized_types/subtyped_bitfield_param/SubtypedBitfieldParam.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/subtyped_bitfield_param/SubtypedBitfieldParam.h
    #${ZSERIO_GEN_DIR}/parameterized_types/subtyped_bitfield_param/SubtypedBitfieldParamHolder.cpp
    #${ZSERIO_GEN_DIR}/parameterized_types/subtyped_bitfield_param/SubtypedBitfieldParamHolder.h
)

zserio_add_library(parameterized_types_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE parameterized_types.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_custom_test(parameterized_types
    DEPENDS
        parameterized_types_zs
    SOURCES
        #${CMAKE_CURRENT_SOURCE_DIR}/cpp/ArrayElementParamTest.cpp
        #${CMAKE_CURRENT_SOURCE_DIR}/cpp/BytesParamTest.cpp
        #${CMAKE_CURRENT_SOURCE_DIR}/cpp/DynamicBitfieldParamTest.cpp
        #${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExternParamTest.cpp
        #${CMAKE_CURRENT_SOURCE_DIR}/cpp/FixedAndVariableParamTest.cpp
        #${CMAKE_CURRENT_SOURCE_DIR}/cpp/GrandChildParamTest.cpp
        #${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedArrayElementParamTest.cpp
        #${CMAKE_CURRENT_SOURCE_DIR}/cpp/ParamWithOptionalTest.cpp
        #${CMAKE_CURRENT_SOURCE_DIR}/cpp/ParameterizedInnerClassesClashingTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ParameterizedParamTest.cpp
        #${CMAKE_CURRENT_SOURCE_DIR}/cpp/SimpleParamTest.cpp
        #${CMAKE_CURRENT_SOURCE_DIR}/cpp/SubtypedBitfieldParamTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
