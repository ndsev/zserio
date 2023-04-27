set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(LOG_FILENAME zserio_log.txt)

# clean old log file
file(REMOVE ${CMAKE_CURRENT_BINARY_DIR}/${LOG_FILENAME})

zserio_add_library(packed_array_in_template_240_error_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE packed_array_in_template_240_error.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck
    IGNORE_ERRORS ON
    ZSERIO_LOG_FILENAME ${LOG_FILENAME}
    APPEND_TO_ZSERIO_LOG_FILE ON
)

zserio_add_library(packed_compound_array_242_error_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE packed_compound_array_242_error.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck
    IGNORE_ERRORS ON
    ZSERIO_LOG_FILENAME ${LOG_FILENAME}
    APPEND_TO_ZSERIO_LOG_FILE ON
)

zserio_add_library(packed_uint32_array_241_error_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE packed_uint32_array_241_error.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck
    IGNORE_ERRORS ON
    ZSERIO_LOG_FILENAME ${LOG_FILENAME}
    APPEND_TO_ZSERIO_LOG_FILE ON
)

zserio_add_library(version_less_than_min_supported_error_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE version_less_than_min_supported_error.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck
    IGNORE_ERRORS ON
    ZSERIO_LOG_FILENAME ${LOG_FILENAME}
    APPEND_TO_ZSERIO_LOG_FILE ON
)

zserio_add_library(wrong_compatibility_version_format_error_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE wrong_compatibility_version_format_error.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck
    IGNORE_ERRORS ON
    ZSERIO_LOG_FILENAME ${LOG_FILENAME}
    APPEND_TO_ZSERIO_LOG_FILE ON
)

add_custom_test(compatibility_error
    DEPENDS
        packed_array_in_template_240_error_zs
        packed_compound_array_242_error_zs
        packed_uint32_array_241_error_zs
        version_less_than_min_supported_error_zs
        wrong_compatibility_version_format_error_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/CompatibilityErrorTest.cpp
)
