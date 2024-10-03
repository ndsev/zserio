set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)

add_library(wrong_set_cpp_allocator_error_zs INTERFACE ${TEST_ZS_ROOT}/wrong_set_cpp_allocator_error.zs)
zserio_generate_cpp(
    TARGET wrong_set_cpp_allocator_error_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${ZSERIO_GEN_DIR}
    EXTRA_ARGS -withoutCrossExtensionCheck -setCppAllocator wrong ${ZSERIO_EXTRA_ARGS}
    OUTPUT_VAR ZSERIO_LOG_WRONG_SET_CPP_ALLOCATOR
    ERROR_VAR ZSERIO_LOG_WRONG_SET_CPP_ALLOCATOR
    RESULT_VAR ZSERIO_RESULT
)
if (ZSERIO_LOG_WRONG_SET_CPP_ALLOCATOR)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_wrong_set_cpp_allocator.txt
        "${ZSERIO_LOG_WRONG_SET_CPP_ALLOCATOR}")
    if (${ZSERIO_RESULT} EQUAL 0)
        message(FATAL_ERROR "Zserio tool is expected to fail!")
    endif ()
endif ()

add_library(bitmask_values_class_clash_error_zs INTERFACE ${TEST_ZS_ROOT}/bitmask_values_class_clash_error.zs)
zserio_generate_cpp(
    TARGET bitmask_values_class_clash_error_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${ZSERIO_GEN_DIR}
    EXTRA_ARGS -withoutCrossExtensionCheck ${ZSERIO_EXTRA_ARGS}
    OUTPUT_VAR ZSERIO_LOG_BITMASK_VALUES_CLASS_CLASH
    ERROR_VAR ZSERIO_LOG_BITMASK_VALUES_CLASS_CLASH
    RESULT_VAR ZSERIO_RESULT
)
if (ZSERIO_LOG_BITMASK_VALUES_CLASS_CLASH)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_bitmask_values_class_clash.txt
        "${ZSERIO_LOG_BITMASK_VALUES_CLASS_CLASH}")
    if (${ZSERIO_RESULT} EQUAL 0)
        message(FATAL_ERROR "Zserio tool is expected to fail!")
    endif ()
endif ()

add_library(sql_table_provider_class_clash_error_zs INTERFACE)
zserio_generate_cpp(
    TARGET sql_table_provider_class_clash_error_zs
    SRC_DIR ${TEST_ZS_ROOT}
    MAIN_ZS sql_table_provider_class_clash_error.zs
    GEN_DIR ${ZSERIO_GEN_DIR}
    EXTRA_ARGS -withoutCrossExtensionCheck ${ZSERIO_EXTRA_ARGS}
    OUTPUT_VAR ZSERIO_LOG_SQL_TABLE_PROVIDER_CLASS_CLASH
    ERROR_VAR ZSERIO_LOG_SQL_TABLE_PROVIDER_CLASS_CLASH
    RESULT_VAR ZSERIO_RESULT
)
if (ZSERIO_LOG_SQL_TABLE_PROVIDER_CLASS_CLASH)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_sql_table_provider_class_clash.txt
        "${ZSERIO_LOG_SQL_TABLE_PROVIDER_CLASS_CLASH}")
    if (${ZSERIO_RESULT} EQUAL 0)
        message(FATAL_ERROR "Zserio tool is expected to fail!")
    endif ()
endif ()

add_library(sql_table_reader_class_clash_error_zs
    INTERFACE ${TEST_ZS_ROOT}/sql_table_reader_class_clash_error.zs)
zserio_generate_cpp(
    TARGET sql_table_reader_class_clash_error_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${ZSERIO_GEN_DIR}
    EXTRA_ARGS -withoutCrossExtensionCheck ${ZSERIO_EXTRA_ARGS}
    OUTPUT_VAR ZSERIO_LOG_SQL_TABLE_READER_CLASS_CLASH
    ERROR_VAR ZSERIO_LOG_SQL_TABLE_READER_CLASS_CLASH
    RESULT_VAR ZSERIO_RESULT
)
if (ZSERIO_LOG_SQL_TABLE_READER_CLASS_CLASH)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_sql_table_reader_class_clash.txt
        "${ZSERIO_LOG_SQL_TABLE_READER_CLASS_CLASH}")
    if (${ZSERIO_RESULT} EQUAL 0)
        message(FATAL_ERROR "Zserio tool is expected to fail!")
    endif ()
endif ()

add_library(sql_table_row_class_clash_error_zs INTERFACE ${TEST_ZS_ROOT}/sql_table_row_class_clash_error.zs)
zserio_generate_cpp(
    TARGET sql_table_row_class_clash_error_zs
    SRC_DIR ${TEST_ZS_ROOT}
    GEN_DIR ${ZSERIO_GEN_DIR}
    EXTRA_ARGS -withoutCrossExtensionCheck ${ZSERIO_EXTRA_ARGS}
    OUTPUT_VAR ZSERIO_LOG_SQL_TABLE_ROW_CLASS_CLASH
    ERROR_VAR ZSERIO_LOG_SQL_TABLE_ROW_CLASS_CLASH
    RESULT_VAR ZSERIO_RESULT
)
if (ZSERIO_LOG_SQL_TABLE_ROW_CLASS_CLASH)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_sql_table_row_class_clash.txt
        "${ZSERIO_LOG_SQL_TABLE_ROW_CLASS_CLASH}")
    if (${ZSERIO_RESULT} EQUAL 0)
        message(FATAL_ERROR "Zserio tool is expected to fail!")
    endif ()
endif ()

add_custom_test(cpp_generator_error
    DEPENDS
        wrong_set_cpp_allocator_error_zs
        bitmask_values_class_clash_error_zs
        sql_table_provider_class_clash_error_zs
        sql_table_reader_class_clash_error_zs
        sql_table_row_class_clash_error_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/CppGeneratorErrorTest.cpp
)
