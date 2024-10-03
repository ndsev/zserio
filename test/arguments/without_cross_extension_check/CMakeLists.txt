add_library(invalid_in_java_zs STATIC ${TEST_ZS_ROOT}/invalid_in_java.zs)
zserio_generate_cpp(
    TARGET invalid_in_java_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_java
    EXTRA_ARGS -withoutSourcesAmalgamation -withoutCrossExtensionCheck ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_JAVA
    OUTPUT_VAR ZSERIO_LOG_INVALID_IN_JAVA
    ERROR_VAR ZSERIO_LOG_INVALID_IN_JAVA
)
target_link_libraries(invalid_in_java_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG_INVALID_IN_JAVA)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_invalid_in_java.txt "${ZSERIO_LOG_INVALID_IN_JAVA}")
    check_zserio_warnings("${ZSERIO_LOG_INVALID_IN_JAVA}" 0)
endif ()

add_library(invalid_in_python_zs STATIC ${TEST_ZS_ROOT}/invalid_in_python.zs)
zserio_generate_cpp(
    TARGET invalid_in_python_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen_python
    EXTRA_ARGS -withoutSourcesAmalgamation -withoutCrossExtensionCheck ${ZSERIO_EXTRA_ARGS}
    GENERATED_SOURCES_VAR GENERATED_SOURCES_PYTHON
    OUTPUT_VAR ZSERIO_LOG_INVALID_IN_PYTHON
    ERROR_VAR ZSERIO_LOG_INVALID_IN_PYTHON
)
target_link_libraries(invalid_in_python_zs PUBLIC ZserioCppRuntime)
if (ZSERIO_LOG_INVALID_IN_PYTHON)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_invalid_in_python.txt "${ZSERIO_LOG_INVALID_IN_PYTHON}")
    check_zserio_warnings("${ZSERIO_LOG_INVALID_IN_PYTHON}" 0)
endif ()

add_custom_test(without_cross_extension_check
    DEPENDS
        invalid_in_java_zs
        invalid_in_python_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/WithoutCrossExtensionCheckTest.cpp
    GENERATED_SOURCES
        ${GENERATED_SOURCES_JAVA}
        ${GENERATED_SOURCES_PYTHON}
)
