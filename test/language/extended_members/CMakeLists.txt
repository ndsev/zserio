set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/extended_members/extended_choice_field/Choice.cpp
    ${ZSERIO_GEN_DIR}/extended_members/extended_choice_field/Choice.h
    ${ZSERIO_GEN_DIR}/extended_members/extended_choice_field/Extended.cpp
    ${ZSERIO_GEN_DIR}/extended_members/extended_choice_field/Extended.h
    ${ZSERIO_GEN_DIR}/extended_members/extended_choice_field/Original.cpp
    ${ZSERIO_GEN_DIR}/extended_members/extended_choice_field/Original.h

    ${ZSERIO_GEN_DIR}/extended_members/extended_compound_field/Compound.cpp
    ${ZSERIO_GEN_DIR}/extended_members/extended_compound_field/Compound.h
    ${ZSERIO_GEN_DIR}/extended_members/extended_compound_field/Extended.cpp
    ${ZSERIO_GEN_DIR}/extended_members/extended_compound_field/Extended.h
    ${ZSERIO_GEN_DIR}/extended_members/extended_compound_field/Original.cpp
    ${ZSERIO_GEN_DIR}/extended_members/extended_compound_field/Original.h

    ${ZSERIO_GEN_DIR}/extended_members/extended_field_in_template/Compound.cpp
    ${ZSERIO_GEN_DIR}/extended_members/extended_field_in_template/Compound.h
    ${ZSERIO_GEN_DIR}/extended_members/extended_field_in_template/ExtendedCompound.cpp
    ${ZSERIO_GEN_DIR}/extended_members/extended_field_in_template/ExtendedCompound.h
    ${ZSERIO_GEN_DIR}/extended_members/extended_field_in_template/ExtendedSimple.cpp
    ${ZSERIO_GEN_DIR}/extended_members/extended_field_in_template/ExtendedSimple.h
    ${ZSERIO_GEN_DIR}/extended_members/extended_field_in_template/Original.cpp
    ${ZSERIO_GEN_DIR}/extended_members/extended_field_in_template/Original.h

    ${ZSERIO_GEN_DIR}/extended_members/extended_optional_parameterized_field/Extended.cpp
    ${ZSERIO_GEN_DIR}/extended_members/extended_optional_parameterized_field/Extended.h
    ${ZSERIO_GEN_DIR}/extended_members/extended_optional_parameterized_field/Original.cpp
    ${ZSERIO_GEN_DIR}/extended_members/extended_optional_parameterized_field/Original.h
    ${ZSERIO_GEN_DIR}/extended_members/extended_optional_parameterized_field/Parameterized.cpp
    ${ZSERIO_GEN_DIR}/extended_members/extended_optional_parameterized_field/Parameterized.h

    ${ZSERIO_GEN_DIR}/extended_members/extended_simple_field/Extended.cpp
    ${ZSERIO_GEN_DIR}/extended_members/extended_simple_field/Extended.h
    ${ZSERIO_GEN_DIR}/extended_members/extended_simple_field/Original.cpp
    ${ZSERIO_GEN_DIR}/extended_members/extended_simple_field/Original.h

    ${ZSERIO_GEN_DIR}/extended_members/extended_unaligned_field/Extended.cpp
    ${ZSERIO_GEN_DIR}/extended_members/extended_unaligned_field/Extended.h
    ${ZSERIO_GEN_DIR}/extended_members/extended_unaligned_field/Original.cpp
    ${ZSERIO_GEN_DIR}/extended_members/extended_unaligned_field/Original.h

    ${ZSERIO_GEN_DIR}/extended_members/multiple_extended_fields/Extended2.cpp
    ${ZSERIO_GEN_DIR}/extended_members/multiple_extended_fields/Extended2.h
    ${ZSERIO_GEN_DIR}/extended_members/multiple_extended_fields/Extended1.cpp
    ${ZSERIO_GEN_DIR}/extended_members/multiple_extended_fields/Extended1.h
    ${ZSERIO_GEN_DIR}/extended_members/multiple_extended_fields/Original.cpp
    ${ZSERIO_GEN_DIR}/extended_members/multiple_extended_fields/Original.h

    ${ZSERIO_GEN_DIR}/extended_members/multiple_extended_fields_various_types/Extended2.cpp
    ${ZSERIO_GEN_DIR}/extended_members/multiple_extended_fields_various_types/Extended2.h
    ${ZSERIO_GEN_DIR}/extended_members/multiple_extended_fields_various_types/Extended1.cpp
    ${ZSERIO_GEN_DIR}/extended_members/multiple_extended_fields_various_types/Extended1.h
    ${ZSERIO_GEN_DIR}/extended_members/multiple_extended_fields_various_types/Original.cpp
    ${ZSERIO_GEN_DIR}/extended_members/multiple_extended_fields_various_types/Original.h
    ${ZSERIO_GEN_DIR}/extended_members/multiple_extended_fields_various_types/Union.cpp
    ${ZSERIO_GEN_DIR}/extended_members/multiple_extended_fields_various_types/Union.h
)

zserio_add_library(extended_members_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE extended_members.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_custom_test(extended_members
    DEPENDS
        extended_members_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExtendedChoiceFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExtendedCompoundFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExtendedFieldInTemplateTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExtendedOptionalParameterizedFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExtendedSimpleFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExtendedUnalignedFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/MultipleExtendedFieldsTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/MultipleExtendedFieldsVariousTypesTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
