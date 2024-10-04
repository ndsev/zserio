add_library(array_types_warning_zs STATIC ${TEST_ZS_ROOT}/array_types_warning.zs)
zserio_generate_cpp(
    TARGET array_types_warning_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(array_types_warning_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 12)
endif ()

add_custom_test(array_types_warning
    DEPENDS
        array_types_warning_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedArrayChoiceHasNoPackableFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedArrayStructHasNoPackableFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedArrayTemplateHasNoPackableFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedArrayUnionHasNoPackableFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedArrayUnpackableBoolElementTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedArrayUnpackableBytesElementTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedArrayUnpackableExternElementTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedArrayUnpackableFloatElementTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/PackedArrayUnpackableStringElementTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
