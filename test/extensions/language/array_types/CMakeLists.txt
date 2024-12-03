add_library(array_types_zs STATIC ${TEST_ZS_ROOT}/array_types.zs)
zserio_generate_cpp(
    TARGET array_types_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(array_types_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_library(choice_tag_clash_zs STATIC ${TEST_ZS_ROOT}/choice_tag_clash.zs)
zserio_generate_cpp(
    TARGET choice_tag_clash_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_choice_tag_clash
    EXTRA_ARGS -withoutSourcesAmalgamation -withoutCrossExtensionCheck ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_CHOICE_TAG_CLASH
    OUTPUT_VAR ZSERIO_LOG_CHOICE_TAG_CLASH
    ERROR_VAR ZSERIO_LOG_CHOICE_TAG_CLASH
)
target_link_libraries(choice_tag_clash_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG_CHOICE_TAG_CLASH)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_choice_tag_clash.txt
        "${ZSERIO_LOG_CHOICE_TAG_CLASH}")
    check_zserio_warnings("${ZSERIO_LOG_CHOICE_TAG_CLASH}" 0)
endif ()

add_custom_test(array_types
    DEPENDS
        array_types_zs
        choice_tag_clash_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ArraysMappingTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/AutoArrayBitfieldParamTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/AutoArrayStructRecursionTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/AutoArraySubtypedUInt8Test.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/AutoArrayUInt8Test.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ChoiceTagClashTest.cpp
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
        ${GENERATED_SOURCES}
        ${GENERATED_SOURCES_CHOICE_TAG_CLASH}
)
