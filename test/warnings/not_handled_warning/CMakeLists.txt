set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/not_handled_warning/Black.h
    ${ZSERIO_GEN_DIR}/not_handled_warning/EnumParamChoice.cpp
    ${ZSERIO_GEN_DIR}/not_handled_warning/EnumParamChoice.h
    ${ZSERIO_GEN_DIR}/not_handled_warning/Grey.h
    ${ZSERIO_GEN_DIR}/not_handled_warning/Selector.cpp
    ${ZSERIO_GEN_DIR}/not_handled_warning/Selector.h
)

zserio_add_library(not_handled_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE not_handled_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
    EXPECTED_WARNINGS 2
    ZSERIO_LOG_FILENAME zserio_log.txt
)

add_custom_test(not_handled_warning
    DEPENDS
        not_handled_warning_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/NotHandledWarningTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
