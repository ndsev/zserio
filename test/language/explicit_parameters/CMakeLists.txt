set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/explicit_parameters/ExplicitParametersDb.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/ExplicitParametersDb.h

    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_bitmask_param/BitmaskParamTable.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_bitmask_param/BitmaskParamTable.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_bitmask_param/TestBlob.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_bitmask_param/TestBlob.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_bitmask_param/TestBitmask.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_bitmask_param/TestBitmask.h

    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_blob_param/BlobParamTable.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_blob_param/BlobParamTable.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_blob_param/Header.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_blob_param/Header.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_blob_param/TestBlob.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_blob_param/TestBlob.h

    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_enum_param/EnumParamTable.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_enum_param/EnumParamTable.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_enum_param/TestBlob.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_enum_param/TestBlob.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_enum_param/TestEnum.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_enum_param/TestEnum.h

    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_same_as_field/SameAsFieldTable.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_same_as_field/SameAsFieldTable.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_same_as_field/TestBlob.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_same_as_field/TestBlob.h

    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_simple_param/SimpleParamTable.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_simple_param/SimpleParamTable.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_simple_param/TestBlob.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/explicit_simple_param/TestBlob.h

    ${ZSERIO_GEN_DIR}/explicit_parameters/multiple_explicit_params/MultipleParamsTable.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/multiple_explicit_params/MultipleParamsTable.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/multiple_explicit_params/TestBlob.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/multiple_explicit_params/TestBlob.h


    ${ZSERIO_GEN_DIR}/explicit_parameters/multiple_with_same_name/MultipleWithSameNameTable.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/multiple_with_same_name/MultipleWithSameNameTable.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/multiple_with_same_name/Parameterized1.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/multiple_with_same_name/Parameterized1.h
    ${ZSERIO_GEN_DIR}/explicit_parameters/multiple_with_same_name/Parameterized2.cpp
    ${ZSERIO_GEN_DIR}/explicit_parameters/multiple_with_same_name/Parameterized2.h
)

zserio_add_library(explicit_parameters_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE explicit_parameters.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

target_include_directories(explicit_parameters_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_custom_test(explicit_parameters
    DEPENDS
        explicit_parameters_zs
    SOURCES
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExplicitBitmaskParamTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExplicitBlobParamTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExplicitEnumParamTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExplicitSameAsFieldTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/ExplicitSimpleParamTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/MultipleExplicitParamsTest.cpp
        ${CMAKE_CURRENT_SOURCE_DIR}/cpp/MultipleWithSameNameTest.cpp
    GENERATED_SOURCES
        ${ZSERIO_GENERATED_SOURCES}
)
