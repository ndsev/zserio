set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/choice_types/bool_param_choice/Black.h
    ${ZSERIO_GEN_DIR}/choice_types/bool_param_choice/BoolParamChoice.cpp
    ${ZSERIO_GEN_DIR}/choice_types/bool_param_choice/BoolParamChoice.h
    ${ZSERIO_GEN_DIR}/choice_types/bool_param_choice/Grey.h

    ${ZSERIO_GEN_DIR}/choice_types/choice_with_array/TestChoice.cpp
    ${ZSERIO_GEN_DIR}/choice_types/choice_with_array/TestChoice.h
    ${ZSERIO_GEN_DIR}/choice_types/choice_with_array/Data8.cpp
    ${ZSERIO_GEN_DIR}/choice_types/choice_with_array/Data8.h

    ${ZSERIO_GEN_DIR}/choice_types/empty_choice/EmptyChoice.cpp
    ${ZSERIO_GEN_DIR}/choice_types/empty_choice/EmptyChoice.h

    ${ZSERIO_GEN_DIR}/choice_types/empty_choice_with_case/EmptyChoiceWithCase.cpp
    ${ZSERIO_GEN_DIR}/choice_types/empty_choice_with_case/EmptyChoiceWithCase.h

    ${ZSERIO_GEN_DIR}/choice_types/empty_choice_with_default/EmptyChoiceWithDefault.cpp
    ${ZSERIO_GEN_DIR}/choice_types/empty_choice_with_default/EmptyChoiceWithDefault.h

    ${ZSERIO_GEN_DIR}/choice_types/enum_param_choice/Black.h
    ${ZSERIO_GEN_DIR}/choice_types/enum_param_choice/EnumParamChoice.cpp
    ${ZSERIO_GEN_DIR}/choice_types/enum_param_choice/EnumParamChoice.h
    ${ZSERIO_GEN_DIR}/choice_types/enum_param_choice/Grey.h
    ${ZSERIO_GEN_DIR}/choice_types/enum_param_choice/Selector.cpp
    ${ZSERIO_GEN_DIR}/choice_types/enum_param_choice/Selector.h
    ${ZSERIO_GEN_DIR}/choice_types/enum_param_choice/White.h

    ${ZSERIO_GEN_DIR}/choice_types/int_choice/IntChoice.cpp
    ${ZSERIO_GEN_DIR}/choice_types/int_choice/IntChoice.h
    ${ZSERIO_GEN_DIR}/choice_types/int_choice/VariantA.h
    ${ZSERIO_GEN_DIR}/choice_types/int_choice/VariantB.h
    ${ZSERIO_GEN_DIR}/choice_types/int_choice/VariantC.h

    ${ZSERIO_GEN_DIR}/choice_types/uint64_param_choice/UInt64ParamChoice.cpp
    ${ZSERIO_GEN_DIR}/choice_types/uint64_param_choice/UInt64ParamChoice.h
    ${ZSERIO_GEN_DIR}/choice_types/uint64_param_choice/VariantA.h
    ${ZSERIO_GEN_DIR}/choice_types/uint64_param_choice/VariantB.h
    ${ZSERIO_GEN_DIR}/choice_types/uint64_param_choice/VariantC.h
)

zserio_add_library(TARGET choice_types_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE choice_types.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/BoolParamChoiceTest.cpp
    cpp/ChoiceWithArrayTest.cpp
    cpp/EmptyChoiceTest.cpp
    cpp/EmptyChoiceWithCaseTest.cpp
    cpp/EmptyChoiceWithDefaultTest.cpp
    cpp/EnumParamChoiceTest.cpp
    cpp/IntChoiceTest.cpp
    cpp/UInt64ParamChoiceTest.cpp
)

add_test_dependencies(choice_types_zs)
