add_library(explicit_parameters_zs STATIC ${TEST_ZS_ROOT}/explicit_parameters.zs)
zserio_generate_cpp(
    TARGET explicit_parameters_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(explicit_parameters_zs PUBLIC ZserioCppRuntime)
target_include_directories(explicit_parameters_zs SYSTEM PRIVATE ${SQLITE_INCDIR})
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(explicit_parameters
    DEPENDS
        explicit_parameters_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExplicitBitmaskParamTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExplicitBlobParamTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExplicitEnumParamTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExplicitSameAsFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExplicitSimpleParamTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/MultipleExplicitParamsTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/MultipleWithSameNameTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
