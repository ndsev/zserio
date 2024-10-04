add_library(union_types_zs STATIC ${TEST_ZS_ROOT}/union_types.zs)
zserio_generate_cpp(
    TARGET union_types_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen
    EXTRA_ARGS -withoutSourcesAmalgamation ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES
    OUTPUT_VAR ZSERIO_LOG
    ERROR_VAR ZSERIO_LOG
)
target_link_libraries(union_types_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log.txt "${ZSERIO_LOG}")
    check_zserio_warnings("${ZSERIO_LOG}" 0)
endif ()

add_custom_test(union_types
    DEPENDS
        union_types_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/EmptyUnionTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/EmptyUnionWithParameterTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/SimpleUnionTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UnionCompatibilityCheckTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UnionWithArrayTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UnionWithParameterizedFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/UnionWithParameterTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES}
)
