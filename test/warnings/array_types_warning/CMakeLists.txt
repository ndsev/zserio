set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_choice_has_no_packable_field/ChoiceWithoutPackableField.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_choice_has_no_packable_field/ChoiceWithoutPackableField.h
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_choice_has_no_packable_field/PackedArrayChoiceHasNoPackableField.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_choice_has_no_packable_field/PackedArrayChoiceHasNoPackableField.h
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_choice_has_no_packable_field/StructWithPackable.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_choice_has_no_packable_field/StructWithPackable.h
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_choice_has_no_packable_field/StructWithPackableArray.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_choice_has_no_packable_field/StructWithPackableArray.h
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_choice_has_no_packable_field/TestEnum.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_choice_has_no_packable_field/TestEnum.h
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_choice_has_no_packable_field/UnionWithPackableField.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_choice_has_no_packable_field/UnionWithPackableField.h

    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_struct_has_no_packable_field/EmptyStruct.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_struct_has_no_packable_field/EmptyStruct.h
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_struct_has_no_packable_field/PackedArrayStructHasNoPackableField.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_struct_has_no_packable_field/PackedArrayStructHasNoPackableField.h
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_struct_has_no_packable_field/StructWithoutPackable.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_struct_has_no_packable_field/StructWithoutPackable.h
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_struct_has_no_packable_field/StructWithPackable.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_struct_has_no_packable_field/StructWithPackable.h
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_struct_has_no_packable_field/StructWithPackableArray.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_struct_has_no_packable_field/StructWithPackableArray.h
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_struct_has_no_packable_field/TestEnum.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_struct_has_no_packable_field/TestEnum.h
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_struct_has_no_packable_field/UnionWithPackableField.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_struct_has_no_packable_field/UnionWithPackableField.h

    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_template_has_no_packable_field/Packable.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_template_has_no_packable_field/Packable.h
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_template_has_no_packable_field/T_packable.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_template_has_no_packable_field/T_packable.h
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_template_has_no_packable_field/T_str.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_template_has_no_packable_field/T_str.h
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_template_has_no_packable_field/T_u32.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_template_has_no_packable_field/T_u32.h
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_template_has_no_packable_field/T_unpackable.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_template_has_no_packable_field/T_unpackable.h
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_template_has_no_packable_field/Unpackable.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_template_has_no_packable_field/Unpackable.h

    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_unpackable_bool_element/PackedArrayUnpackableBoolElement.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_unpackable_bool_element/PackedArrayUnpackableBoolElement.h
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_unpackable_bool_element/TestBitmask.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_unpackable_bool_element/TestBitmask.h
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_unpackable_bool_element/TestEnum.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_unpackable_bool_element/TestEnum.h

    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_unpackable_bytes_element/PackedArrayUnpackableBytesElement.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_unpackable_bytes_element/PackedArrayUnpackableBytesElement.h

    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_unpackable_extern_element/PackedArrayUnpackableExternElement.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_unpackable_extern_element/PackedArrayUnpackableExternElement.h

    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_unpackable_float_element/PackedArrayUnpackableFloatElement.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_unpackable_float_element/PackedArrayUnpackableFloatElement.h

    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_unpackable_string_element/PackedArrayUnpackableStringElement.cpp
    ${ZSERIO_GEN_DIR}/array_types_warning/packed_array_unpackable_string_element/PackedArrayUnpackableStringElement.h
)

zserio_add_library(array_types_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE array_types_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
    EXPECTED_WARNINGS 11
    ZSERIO_LOG_FILENAME zserio_log.txt
)

add_custom_test(array_types_warning
    DEPENDS
        array_types_warning_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ArrayTypesWarningTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedArrayChoiceHasNoPackableFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedArrayStructHasNoPackableFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedArrayTemplateHasNoPackableFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedArrayUnpackableBoolElementTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedArrayUnpackableBytesElementTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedArrayUnpackableExternElementTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedArrayUnpackableFloatElementTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedArrayUnpackableStringElementTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
