set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/expressions/array_type/ArrayTypeExpression.cpp
    ${ZSERIO_GEN_DIR}/expressions/array_type/ArrayTypeExpression.h

    ${ZSERIO_GEN_DIR}/expressions/cast_uint8_to_uint64/CastUInt8ToUInt64Expression.cpp
    ${ZSERIO_GEN_DIR}/expressions/cast_uint8_to_uint64/CastUInt8ToUInt64Expression.h

    ${ZSERIO_GEN_DIR}/expressions/cast_uint64_to_uint8/CastUInt64ToUInt8Expression.cpp
    ${ZSERIO_GEN_DIR}/expressions/cast_uint64_to_uint8/CastUInt64ToUInt8Expression.h

    ${ZSERIO_GEN_DIR}/expressions/const_type/ConstTypeExpression.cpp
    ${ZSERIO_GEN_DIR}/expressions/const_type/ConstTypeExpression.h
    ${ZSERIO_GEN_DIR}/expressions/const_type/INVALID_VALUE.h

    ${ZSERIO_GEN_DIR}/expressions/enumeration_type/Color.cpp
    ${ZSERIO_GEN_DIR}/expressions/enumeration_type/Color.h
    ${ZSERIO_GEN_DIR}/expressions/enumeration_type/EnumerationTypeExpression.cpp
    ${ZSERIO_GEN_DIR}/expressions/enumeration_type/EnumerationTypeExpression.h

    ${ZSERIO_GEN_DIR}/expressions/field_type/ContainedType.cpp
    ${ZSERIO_GEN_DIR}/expressions/field_type/ContainedType.h
    ${ZSERIO_GEN_DIR}/expressions/field_type/FieldTypeExpression.cpp
    ${ZSERIO_GEN_DIR}/expressions/field_type/FieldTypeExpression.h

    ${ZSERIO_GEN_DIR}/expressions/float_type/FloatTypeExpression.cpp
    ${ZSERIO_GEN_DIR}/expressions/float_type/FloatTypeExpression.h

    ${ZSERIO_GEN_DIR}/expressions/full_const_type/FullConstTypeExpression.cpp
    ${ZSERIO_GEN_DIR}/expressions/full_const_type/FullConstTypeExpression.h
    ${ZSERIO_GEN_DIR}/expressions/full_const_type/FULL_INVALID_VALUE.h

    ${ZSERIO_GEN_DIR}/expressions/full_enumeration_type/Color.cpp
    ${ZSERIO_GEN_DIR}/expressions/full_enumeration_type/Color.h
    ${ZSERIO_GEN_DIR}/expressions/full_enumeration_type/FullEnumerationTypeExpression.cpp
    ${ZSERIO_GEN_DIR}/expressions/full_enumeration_type/FullEnumerationTypeExpression.h

    ${ZSERIO_GEN_DIR}/expressions/function_type/Color.cpp
    ${ZSERIO_GEN_DIR}/expressions/function_type/Color.h
    ${ZSERIO_GEN_DIR}/expressions/function_type/FunctionTypeExpression.cpp
    ${ZSERIO_GEN_DIR}/expressions/function_type/FunctionTypeExpression.h

    ${ZSERIO_GEN_DIR}/expressions/index_operator/Element.cpp
    ${ZSERIO_GEN_DIR}/expressions/index_operator/Element.h
    ${ZSERIO_GEN_DIR}/expressions/index_operator/ElementList.cpp
    ${ZSERIO_GEN_DIR}/expressions/index_operator/ElementList.h

    ${ZSERIO_GEN_DIR}/expressions/lengthof_operator/LengthOfFunctions.cpp
    ${ZSERIO_GEN_DIR}/expressions/lengthof_operator/LengthOfFunctions.h

    ${ZSERIO_GEN_DIR}/expressions/negation_operator/NegationOperatorExpression.cpp
    ${ZSERIO_GEN_DIR}/expressions/negation_operator/NegationOperatorExpression.h

    ${ZSERIO_GEN_DIR}/expressions/numbits_operator/NumBitsFunctions.cpp
    ${ZSERIO_GEN_DIR}/expressions/numbits_operator/NumBitsFunctions.h

    ${ZSERIO_GEN_DIR}/expressions/parameter_type/Color.cpp
    ${ZSERIO_GEN_DIR}/expressions/parameter_type/Color.h
    ${ZSERIO_GEN_DIR}/expressions/parameter_type/ParameterTypeExpression.cpp
    ${ZSERIO_GEN_DIR}/expressions/parameter_type/ParameterTypeExpression.h

    ${ZSERIO_GEN_DIR}/expressions/parameterized_array_type/ParameterizedArrayElement.cpp
    ${ZSERIO_GEN_DIR}/expressions/parameterized_array_type/ParameterizedArrayElement.h
    ${ZSERIO_GEN_DIR}/expressions/parameterized_array_type/ParameterizedArrayHolder.cpp
    ${ZSERIO_GEN_DIR}/expressions/parameterized_array_type/ParameterizedArrayHolder.h
    ${ZSERIO_GEN_DIR}/expressions/parameterized_array_type/ParameterizedArrayTypeExpression.cpp
    ${ZSERIO_GEN_DIR}/expressions/parameterized_array_type/ParameterizedArrayTypeExpression.h

    ${ZSERIO_GEN_DIR}/expressions/parenthesis/ParenthesisExpression.cpp
    ${ZSERIO_GEN_DIR}/expressions/parenthesis/ParenthesisExpression.h

    ${ZSERIO_GEN_DIR}/expressions/question_mark/QuestionMarkExpression.cpp
    ${ZSERIO_GEN_DIR}/expressions/question_mark/QuestionMarkExpression.h

    ${ZSERIO_GEN_DIR}/expressions/string_type/StringTypeExpression.cpp
    ${ZSERIO_GEN_DIR}/expressions/string_type/StringTypeExpression.h

    ${ZSERIO_GEN_DIR}/expressions/sum_operator/SumFunction.cpp
    ${ZSERIO_GEN_DIR}/expressions/sum_operator/SumFunction.h

    ${ZSERIO_GEN_DIR}/expressions/uint64_type/UInt64TypeExpression.cpp
    ${ZSERIO_GEN_DIR}/expressions/uint64_type/UInt64TypeExpression.h

    ${ZSERIO_GEN_DIR}/expressions/used_before_type/Color.cpp
    ${ZSERIO_GEN_DIR}/expressions/used_before_type/Color.h
    ${ZSERIO_GEN_DIR}/expressions/used_before_type/COLOR_RED_VALUE.h
    ${ZSERIO_GEN_DIR}/expressions/used_before_type/NUM_VALUE_BITS.h
    ${ZSERIO_GEN_DIR}/expressions/used_before_type/UsedBeforeTypeExpression.cpp
    ${ZSERIO_GEN_DIR}/expressions/used_before_type/UsedBeforeTypeExpression.h

    ${ZSERIO_GEN_DIR}/expressions/valueof_operator/Color.cpp
    ${ZSERIO_GEN_DIR}/expressions/valueof_operator/Color.h
    ${ZSERIO_GEN_DIR}/expressions/valueof_operator/ValueOfFunctions.cpp
    ${ZSERIO_GEN_DIR}/expressions/valueof_operator/ValueOfFunctions.h
)

zserio_add_library(TARGET expressions_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE expressions.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/ArrayTypeTest.cpp
    cpp/CastUInt64ToUInt8Test.cpp
    cpp/CastUInt8ToUInt64Test.cpp
    cpp/ConstTypeTest.cpp
    cpp/EnumerationTypeTest.cpp
    cpp/FieldTypeTest.cpp
    cpp/FloatTypeTest.cpp
    cpp/FullConstTypeTest.cpp
    cpp/FullEnumerationTypeTest.cpp
    cpp/FunctionTypeTest.cpp
    cpp/IndexOperatorTest.cpp
    cpp/LengthOfOperatorTest.cpp
    cpp/NegationOperatorTest.cpp
    cpp/NumBitsOperatorTest.cpp
    cpp/ParameterTypeTest.cpp
    cpp/ParameterizedArrayTypeTest.cpp
    cpp/ParenthesisTest.cpp
    cpp/QuestionMarkTest.cpp
    cpp/StringTypeTest.cpp
    cpp/SumOperatorTest.cpp
    cpp/UInt64TypeTest.cpp
    cpp/UsedBeforeTypeTest.cpp
    cpp/ValueOfOperatorTest.cpp
)

add_test_dependencies(expressions_zs)
