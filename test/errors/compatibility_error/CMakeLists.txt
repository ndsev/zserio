set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(LOG_FILENAME zserio_log.txt)

# clean old log file
file(REMOVE ${CMAKE_CURRENT_BINARY_DIR}/${LOG_FILENAME})

zserio_add_library(TARGET packed_array_in_template_240_error_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE packed_array_in_template_240_error.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck
    ZSERIO_LOG_FILENAME ${LOG_FILENAME}
    IGNORE_ERRORS ON
)

zserio_add_library(TARGET packed_compound_array_242_error_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE packed_compound_array_242_error.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck
    ZSERIO_LOG_FILENAME ${LOG_FILENAME}
    IGNORE_ERRORS ON
)

zserio_add_library(TARGET packed_uint32_array_241_error_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE packed_uint32_array_241_error.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck
    ZSERIO_LOG_FILENAME ${LOG_FILENAME}
    IGNORE_ERRORS ON
)

zserio_add_library(TARGET version_less_than_min_supported_error_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE version_less_than_min_supported_error.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck
    ZSERIO_LOG_FILENAME ${LOG_FILENAME}
    IGNORE_ERRORS ON
)

zserio_add_library(TARGET wrong_compatibility_version_format_error_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE wrong_compatibility_version_format_error.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES EMPTY
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutCrossExtensionCheck
    ZSERIO_LOG_FILENAME ${LOG_FILENAME}
    IGNORE_ERRORS ON
)

add_test_dependencies(packed_array_in_template_240_error_zs)
add_test_dependencies(packed_compound_array_242_error_zs)
add_test_dependencies(packed_uint32_array_241_error_zs)
add_test_dependencies(version_less_than_min_supported_error_zs)
add_test_dependencies(wrong_compatibility_version_format_error_zs)

add_test_sources(
    cpp/CompatibilityErrorTest.cpp
)
