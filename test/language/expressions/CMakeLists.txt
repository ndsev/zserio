add_library(expressions_zs STATIC ${TEST_ZS_ROOT}/expressions.zs)
zserio_generate_cpp(
    TARGET expressions_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(expressions_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(expressions
    DEPENDS
        expressions_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ArrayTypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitmaskTypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/CastUInt64ToUInt8Test.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/CastUInt8ToUInt64Test.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ConstTypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/EnumerationTypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/FieldTypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/FieldTypeWithClashTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/FloatTypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/FullConstTypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/FullEnumerationTypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/FunctionTypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/IndexOperatorTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/IsSetOperatorTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/LengthOfOperatorTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ModuloOperatorTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/NegationOperatorTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/NumBitsOperatorTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ParameterTypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ParameterizedArrayTypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ParenthesisTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/QuestionMarkTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/StringTypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UInt64TypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UsedBeforeTypeTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ValueOfOperatorTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
