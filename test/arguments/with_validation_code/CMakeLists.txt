set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/with_validation_code/blob_table_validation/Blob.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/blob_table_validation/Blob.h
    ${ZSERIO_GEN_DIR}/with_validation_code/blob_table_validation/BlobTable.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/blob_table_validation/BlobTable.h
    ${ZSERIO_GEN_DIR}/with_validation_code/blob_table_validation/BlobTableValidationDb.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/blob_table_validation/BlobTableValidationDb.h

    ${ZSERIO_GEN_DIR}/with_validation_code/column_type_validation/Blob.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/column_type_validation/Blob.h
    ${ZSERIO_GEN_DIR}/with_validation_code/column_type_validation/ColumnTypeDb.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/column_type_validation/ColumnTypeDb.h
    ${ZSERIO_GEN_DIR}/with_validation_code/column_type_validation/ColumnTypeTable.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/column_type_validation/ColumnTypeTable.h

    ${ZSERIO_GEN_DIR}/with_validation_code/constraint_table_validation/Blob.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/constraint_table_validation/Blob.h
    ${ZSERIO_GEN_DIR}/with_validation_code/constraint_table_validation/ConstraintTable.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/constraint_table_validation/ConstraintTable.h
    ${ZSERIO_GEN_DIR}/with_validation_code/constraint_table_validation/ConstraintTableValidationDb.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/constraint_table_validation/ConstraintTableValidationDb.h

    ${ZSERIO_GEN_DIR}/with_validation_code/control_validation/Blob.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/control_validation/Blob.h
    ${ZSERIO_GEN_DIR}/with_validation_code/control_validation/Table1.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/control_validation/Table1.h
    ${ZSERIO_GEN_DIR}/with_validation_code/control_validation/Table2.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/control_validation/Table2.h
    ${ZSERIO_GEN_DIR}/with_validation_code/control_validation/Table3.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/control_validation/Table3.h
    ${ZSERIO_GEN_DIR}/with_validation_code/control_validation/ControlValidationDb.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/control_validation/ControlValidationDb.h

    ${ZSERIO_GEN_DIR}/with_validation_code/depend_table_validation/Blob.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/depend_table_validation/Blob.h
    ${ZSERIO_GEN_DIR}/with_validation_code/depend_table_validation/DependTable.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/depend_table_validation/DependTable.h
    ${ZSERIO_GEN_DIR}/with_validation_code/depend_table_validation/DependTableValidationDb.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/depend_table_validation/DependTableValidationDb.h

    ${ZSERIO_GEN_DIR}/with_validation_code/full_range_table_validation/FullRangeTable.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/full_range_table_validation/FullRangeTable.h
    ${ZSERIO_GEN_DIR}/with_validation_code/full_range_table_validation/FullRangeTableValidationDb.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/full_range_table_validation/FullRangeTableValidationDb.h

    ${ZSERIO_GEN_DIR}/with_validation_code/hidden_column_table_validation/HiddenColumnTable.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/hidden_column_table_validation/HiddenColumnTable.h
    ${ZSERIO_GEN_DIR}/with_validation_code/hidden_column_table_validation/HiddenColumnTableValidationDb.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/hidden_column_table_validation/HiddenColumnTableValidationDb.h

    ${ZSERIO_GEN_DIR}/with_validation_code/simple_table_validation/RootStruct.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/simple_table_validation/RootStruct.h
    ${ZSERIO_GEN_DIR}/with_validation_code/simple_table_validation/SimpleTable.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/simple_table_validation/SimpleTable.h
    ${ZSERIO_GEN_DIR}/with_validation_code/simple_table_validation/SimpleTableValidationDb.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/simple_table_validation/SimpleTableValidationDb.h
    ${ZSERIO_GEN_DIR}/with_validation_code/simple_table_validation/TestBitmask.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/simple_table_validation/TestBitmask.h
    ${ZSERIO_GEN_DIR}/with_validation_code/simple_table_validation/TestEnum.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/simple_table_validation/TestEnum.h

    ${ZSERIO_GEN_DIR}/with_validation_code/virtual_table_validation/VirtualTable.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/virtual_table_validation/VirtualTable.h
    ${ZSERIO_GEN_DIR}/with_validation_code/virtual_table_validation/VirtualTableValidationDb.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/virtual_table_validation/VirtualTableValidationDb.h
    ${ZSERIO_GEN_DIR}/with_validation_code/virtual_table_validation/TestTable.cpp
    ${ZSERIO_GEN_DIR}/with_validation_code/virtual_table_validation/TestTable.h
)

zserio_add_library(TARGET with_validation_code_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE with_validation_code.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withValidationCode -withoutSourcesAmalgamation
)

target_include_directories(with_validation_code_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_test_sources(
    cpp/BlobTableValidationTest.cpp
    cpp/ColumnTypeValidationTest.cpp
    cpp/ConstraintTableValidationTest.cpp
    cpp/ControlValidationTest.cpp
    cpp/DependTableValidationTest.cpp
    cpp/FullRangeTableValidationTest.cpp
    cpp/HiddenColumnTableValidationTest.cpp
    cpp/SimpleTableValidationTest.cpp
    cpp/VirtualTableValidationTest.cpp
)

add_test_dependencies(with_validation_code_zs)
