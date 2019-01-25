set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/explicit_parameters/TestBlob.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/TestBlob.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/TestTable.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/TestTable.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/TestTableRow.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/TestTableRow.h

    ${ZSERIO_GEN_DIR}/explicit_parameters/IParameterProvider.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/TestDb.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/TestDb.h
)

zserio_add_library(TARGET explicit_parameters_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE explicit_parameters.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

add_test_sources(
    cpp/ExplicitParametersTest.cpp
)

add_test_dependencies(explicit_parameters_zs)
