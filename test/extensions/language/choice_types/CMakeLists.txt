add_library(choice_types_zs STATIC ${TEST_ZS_ROOT}/choice_types.zs)
zserio_generate_cpp(
    TARGET choice_types_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(choice_types_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(choice_types
    DEPENDS
        choice_types_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BitmaskParamChoiceTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BoolChoiceWithDefaultTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/BoolParamChoiceTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ChoiceCompatibilityCheckTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ChoiceWithArrayTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ConstantInChoiceCaseTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/DefaultEmptyChoiceTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/EmptyChoiceTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/EmptyChoiceWithCaseTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/EmptyChoiceWithDefaultTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/EnumParamChoiceTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/EnumWithRemovedItemParamChoiceTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExpressionSelectorChoiceTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/FullBitmaskParamChoiceTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/FullEnumParamChoiceTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/FunctionReturningLiteralSelectorChoiceTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/FunctionSelectorChoiceTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UInt16ParamChoiceTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UInt64ParamChoiceTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
