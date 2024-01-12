set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)

add_library(packed_array_in_template_240_error_zs INTERFACE zs/packed_array_in_template_240_error.zs)
zserio_generate_cpp(
    TARGET packed_array_in_template_240_error_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${ZSERIO_GEN_DIR}
    EXTRA_ARGS -withoutCrossExtensionCheck
    OUTPUT_VAR ZSERIO_LOG_PACKED_ARRAY_IN_TEMPLATE_240
    ERROR_VAR ZSERIO_LOG_PACKED_ARRAY_IN_TEMPLATE_240
    RESULT_VAR ZSERIO_RESULT
)
if (ZSERIO_LOG_PACKED_ARRAY_IN_TEMPLATE_240)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_packed_array_in_template_240.txt
        "${ZSERIO_LOG_PACKED_ARRAY_IN_TEMPLATE_240}")
    if (${ZSERIO_RESULT} EQUAL 0)
        message(FATAL_ERROR "Zserio tool is expected to fail!")
    endif ()
endif ()

add_library(packed_compound_array_242_error_zs INTERFACE zs/packed_compound_array_242_error.zs)
zserio_generate_cpp(
    TARGET packed_compound_array_242_error_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${ZSERIO_GEN_DIR}
    EXTRA_ARGS -withoutCrossExtensionCheck
    OUTPUT_VAR ZSERIO_LOG_PACKED_COMPOUND_ARRAY_242
    ERROR_VAR ZSERIO_LOG_PACKED_COMPOUND_ARRAY_242
    RESULT_VAR ZSERIO_RESULT
)
if (ZSERIO_LOG_PACKED_COMPOUND_ARRAY_242)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_packed_compound_array_242.txt
        "${ZSERIO_LOG_PACKED_COMPOUND_ARRAY_242}")
    if (${ZSERIO_RESULT} EQUAL 0)
        message(FATAL_ERROR "Zserio tool is expected to fail!")
    endif ()
endif ()

add_library(packed_uint32_array_241_error_zs INTERFACE zs/packed_uint32_array_241_error.zs)
zserio_generate_cpp(
    TARGET packed_uint32_array_241_error_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${ZSERIO_GEN_DIR}
    EXTRA_ARGS -withoutCrossExtensionCheck
    OUTPUT_VAR ZSERIO_LOG_PACKED_UINT32_ARRAY_241
    ERROR_VAR ZSERIO_LOG_PACKED_UINT32_ARRAY_241
    RESULT_VAR ZSERIO_RESULT
)
if (ZSERIO_LOG_PACKED_UINT32_ARRAY_241)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_packed_uint32_array_241.txt
        "${ZSERIO_LOG_PACKED_UINT32_ARRAY_241}")
    if (${ZSERIO_RESULT} EQUAL 0)
        message(FATAL_ERROR "Zserio tool is expected to fail!")
    endif ()
endif ()

add_library(version_less_than_min_supported_error_zs INTERFACE zs/version_less_than_min_supported_error.zs)
zserio_generate_cpp(
    TARGET version_less_than_min_supported_error_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${ZSERIO_GEN_DIR}
    EXTRA_ARGS -withoutCrossExtensionCheck
    OUTPUT_VAR ZSERIO_LOG_VERSION_LESS_THAN_MIN_SUPPORTED
    ERROR_VAR ZSERIO_LOG_VERSION_LESS_THAN_MIN_SUPPORTED
    RESULT_VAR ZSERIO_RESULT
)
if (ZSERIO_LOG_VERSION_LESS_THAN_MIN_SUPPORTED)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_version_less_than_min_supported.txt
        "${ZSERIO_LOG_VERSION_LESS_THAN_MIN_SUPPORTED}")
    if (${ZSERIO_RESULT} EQUAL 0)
        message(FATAL_ERROR "Zserio tool is expected to fail!")
    endif ()
endif ()

add_library(wrong_compatibility_version_format_error_zs INTERFACE
    zs/wrong_compatibility_version_format_error.zs
)
zserio_generate_cpp(
    TARGET wrong_compatibility_version_format_error_zs
    SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    GEN_DIR ${ZSERIO_GEN_DIR}
    EXTRA_ARGS -withoutCrossExtensionCheck
    OUTPUT_VAR ZSERIO_LOG_WRONG_COMPATIBILITY_VERSION_FORMAT
    ERROR_VAR ZSERIO_LOG_WRONG_COMPATIBILITY_VERSION_FORMAT
    RESULT_VAR ZSERIO_RESULT
)
if (ZSERIO_LOG_WRONG_COMPATIBILITY_VERSION_FORMAT)
    file(WRITE ${CMAKE_CURRENT_BINARY_DIR}/zserio_log_wrong_compatibility_version_format.txt
        "${ZSERIO_LOG_WRONG_COMPATIBILITY_VERSION_FORMAT}")
    if (${ZSERIO_RESULT} EQUAL 0)
        message(FATAL_ERROR "Zserio tool is expected to fail!")
    endif ()
endif ()

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
