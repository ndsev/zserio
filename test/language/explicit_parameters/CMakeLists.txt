set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/explicit_parameters/ExplicitParametersDb.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/ExplicitParametersDb.h

    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_blob_param/BlobParamTable.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_blob_param/BlobParamTable.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_blob_param/BlobParamTableRow.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_blob_param/BlobParamTableRow.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_blob_param/Header.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_blob_param/Header.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_blob_param/TestBlob.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_blob_param/TestBlob.h

    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_simple_param/SimpleParamTable.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_simple_param/SimpleParamTable.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_simple_param/SimpleParamTableRow.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_simple_param/SimpleParamTableRow.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_simple_param/TestBlob.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_simple_param/TestBlob.h

    ${ZSERIO_GEN_DIR}/explicit_parameters/multiple_explicit_params/MultipleParamsTable.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/multiple_explicit_params/MultipleParamsTable.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/multiple_explicit_params/MultipleParamsTableRow.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/multiple_explicit_params/MultipleParamsTableRow.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/multiple_explicit_params/TestBlob.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/multiple_explicit_params/TestBlob.h

    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_same_as_field/SameAsFieldTable.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_same_as_field/SameAsFieldTable.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_same_as_field/SameAsFieldTableRow.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_same_as_field/SameAsFieldTableRow.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_same_as_field/TestBlob.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_same_as_field/TestBlob.h
)

zserio_add_library(TARGET explicit_parameters_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE explicit_parameters.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

target_include_directories(explicit_parameters_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_test_sources(
    cpp/ExplicitBlobParamTest.cpp
    cpp/ExplicitSameAsFieldTest.cpp
    cpp/ExplicitSimpleParamTest.cpp
    cpp/MultipleExplicitParamsTest.cpp
)

add_test_dependencies(explicit_parameters_zs)
