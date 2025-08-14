add_library(with_type_info_code_zs STATIC ${TEST_ZS_ROOT}/with_type_info_code.zs)
zserio_generate_cpp(
    TARGET with_type_info_code_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation -withTypeInfoCode -withReflectionCode -allowImplicitArrays ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(with_type_info_code_zs PUBLIC ZserioCppRuntime)
target_include_directories(with_type_info_code_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_library(type_info_string_expressions_zs STATIC ${TEST_ZS_ROOT}/type_info_string_expressions.zs)
zserio_generate_cpp(
    TARGET type_info_string_expressions_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_string_expressions
    EXTRA_ARGS -withoutSourcesAmalgamation -withTypeInfoCode -withReflectionCode ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_STRING_EXPRESSIONS
    OUTPUT_VAR ZSERIO_LOG_STRING_EXPRESSIONS
    ERROR_VAR ZSERIO_LOG_STRING_EXPRESSIONS
)
target_link_libraries(type_info_string_expressions_zs PUBLIC ZserioCppRuntime)
target_include_directories(type_info_string_expressions_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG_STRING_EXPRESSIONS)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_string_expressions.txt "${ZSERIO_LOG_STRING_EXPRESSIONS}")
    check_zserio_warnings("${ZSERIO_LOG_STRING_EXPRESSIONS}" 0)
endif ()

add_library(json_param_cast_zs STATIC ${TEST_ZS_ROOT}/json_param_cast.zs)
zserio_generate_cpp(
    TARGET json_param_cast_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_json_param_cast
    EXTRA_ARGS -withoutSourcesAmalgamation -withTypeInfoCode -withReflectionCode ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_JSON_PARAM_CAST
    OUTPUT_VAR ZSERIO_LOG_JSON_PARAM_CAST
    ERROR_VAR ZSERIO_LOG_JSON_PARAM_CAST
)
target_link_libraries(json_param_cast_zs PUBLIC ZserioCppRuntime)
target_include_directories(json_param_cast_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG_JSON_PARAM_CAST)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_json_param_cast.txt "${ZSERIO_LOG_JSON_PARAM_CAST}")
    check_zserio_warnings("${ZSERIO_LOG_JSON_PARAM_CAST}" 0)
endif ()

add_custom_test(with_type_info_code
    DEPENDS
        json_param_cast_zs
        type_info_string_expressions_zs
        with_type_info_code_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/DebugStringTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/JsonParamCastTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/TypeInfoStringExpressionsTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithTypeInfoCodeCreator.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithTypeInfoCodeCreator.h
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithTypeInfoCodeTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
        ${GENERATED_SOURCES_JSON_PARAM_CAST}
        ${GENERATED_SOURCES_STRING_EXPRESSIONS}
)
