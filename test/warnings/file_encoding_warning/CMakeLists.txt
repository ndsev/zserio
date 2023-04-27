set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/file_encoding_warning/NON_PRINTABLE_ASCII_STRING.h
    ${ZSERIO_GEN_DIR}/file_encoding_warning/NON_UTF8_STRING.h
)

zserio_add_library(file_encoding_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE file_encoding_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
    EXPECTED_WARNINGS 3
    ZSERIO_LOG_FILENAME zserio_log.txt
)

add_custom_test(file_encoding_warning
    DEPENDS
        file_encoding_warning_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/FileEncodingWarningTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
