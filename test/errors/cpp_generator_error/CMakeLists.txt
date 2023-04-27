set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(LOG_FILENAME zserio_log.txt)

# clean old log file
file(REMOVE ${CMAKE_CURRENT_BINARY_DIR}/${LOG_FILENAME})

zserio_add_library(wrong_set_cpp_allocator_error_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE wrong_set_cpp_allocator_error.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck -setCppAllocator wrong
    IGNORE_ERRORS ON
    ZSERIO_LOG_FILENAME ${LOG_FILENAME}
    APPEND_TO_ZSERIO_LOG_FILE ON
)

zserio_add_library(bitmask_values_class_clash_error_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE bitmask_values_class_clash_error.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck
    IGNORE_ERRORS ON
    ZSERIO_LOG_FILENAME ${LOG_FILENAME}
    APPEND_TO_ZSERIO_LOG_FILE ON
)

zserio_add_library(sql_table_provider_class_clash_error_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE sql_table_provider_class_clash_error.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck
    IGNORE_ERRORS ON
    ZSERIO_LOG_FILENAME ${LOG_FILENAME}
    APPEND_TO_ZSERIO_LOG_FILE ON
)

zserio_add_library(sql_table_reader_class_clash_error_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE sql_table_reader_class_clash_error.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck
    IGNORE_ERRORS ON
    ZSERIO_LOG_FILENAME ${LOG_FILENAME}
    APPEND_TO_ZSERIO_LOG_FILE ON
)

zserio_add_library(sql_table_row_class_clash_error_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE sql_table_row_class_clash_error.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck
    IGNORE_ERRORS ON
    ZSERIO_LOG_FILENAME ${LOG_FILENAME}
    APPEND_TO_ZSERIO_LOG_FILE ON
)

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
