set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/templates_warning/default_instantiation_warning/Subpackage3TemplateU32.h
    ${ZSERIO_GEN_DIR}/templates_warning/default_instantiation_warning/DefaultInstantiationWarning.cpp
    ${ZSERIO_GEN_DIR}/templates_warning/default_instantiation_warning/DefaultInstantiationWarning.h
    ${ZSERIO_GEN_DIR}/templates_warning/default_instantiation_warning/Template_uint32.h
    ${ZSERIO_GEN_DIR}/templates_warning/default_instantiation_warning/Template_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates_warning/default_instantiation_warning/Subpackage3TemplateU32.cpp
    ${ZSERIO_GEN_DIR}/templates_warning/default_instantiation_subpackage2/Subpackage2TemplateSTR.h
    ${ZSERIO_GEN_DIR}/templates_warning/default_instantiation_subpackage2/Subpackage2Template_string.cpp
    ${ZSERIO_GEN_DIR}/templates_warning/default_instantiation_subpackage2/Subpackage2TemplateU32.h
    ${ZSERIO_GEN_DIR}/templates_warning/default_instantiation_subpackage2/Subpackage2TemplateSTR.cpp
    ${ZSERIO_GEN_DIR}/templates_warning/default_instantiation_subpackage2/Subpackage2TemplateU32.cpp
    ${ZSERIO_GEN_DIR}/templates_warning/default_instantiation_subpackage2/Subpackage2Template_string.h
    ${ZSERIO_GEN_DIR}/templates_warning/default_instantiation_subpackage3/Subpackage3InnerTemplate_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates_warning/default_instantiation_subpackage3/Subpackage3InnerTemplate_uint32.h
    ${ZSERIO_GEN_DIR}/templates_warning/default_instantiation_subpackage1/Subpackage1TemplateU32.cpp
    ${ZSERIO_GEN_DIR}/templates_warning/default_instantiation_subpackage1/Subpackage1Template_string.cpp
    ${ZSERIO_GEN_DIR}/templates_warning/default_instantiation_subpackage1/Subpackage1TemplateU32.h
    ${ZSERIO_GEN_DIR}/templates_warning/default_instantiation_subpackage1/Subpackage1Template_string.h
)

zserio_add_library(templates_warning_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE templates_warning.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withWarnings default-instantiation -withoutSourcesAmalgamation
    EXPECTED_WARNINGS 5
    ZSERIO_LOG_FILENAME zserio_log.txt
)

add_custom_test(templates_warning
    DEPENDS
        templates_warning_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/TemplatesWarningTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
