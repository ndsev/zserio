set(ZSERIO_GEN_DIR ${CMAKE_CURRENT_BINARY_DIR}/gen)
set(ZSERIO_GENERATED_SOURCES
    ${ZSERIO_GEN_DIR}/templates/choice_templated_enum_selector/EnumFromZero.cpp
    ${ZSERIO_GEN_DIR}/templates/choice_templated_enum_selector/EnumFromZero.h
    ${ZSERIO_GEN_DIR}/templates/choice_templated_enum_selector/EnumFromOne.cpp
    ${ZSERIO_GEN_DIR}/templates/choice_templated_enum_selector/EnumFromOne.h
    ${ZSERIO_GEN_DIR}/templates/choice_templated_enum_selector/TemplatedChoice_EnumFromZero.cpp
    ${ZSERIO_GEN_DIR}/templates/choice_templated_enum_selector/TemplatedChoice_EnumFromZero.h
    ${ZSERIO_GEN_DIR}/templates/choice_templated_enum_selector/TemplatedChoice_EnumFromOne.cpp
    ${ZSERIO_GEN_DIR}/templates/choice_templated_enum_selector/TemplatedChoice_EnumFromOne.h
    ${ZSERIO_GEN_DIR}/templates/choice_templated_enum_selector/ChoiceTemplatedEnumSelector.cpp
    ${ZSERIO_GEN_DIR}/templates/choice_templated_enum_selector/ChoiceTemplatedEnumSelector.h

    ${ZSERIO_GEN_DIR}/templates/choice_templated_selector/ChoiceTemplatedSelector.cpp
    ${ZSERIO_GEN_DIR}/templates/choice_templated_selector/ChoiceTemplatedSelector.h
    ${ZSERIO_GEN_DIR}/templates/choice_templated_selector/Shift16.h
    ${ZSERIO_GEN_DIR}/templates/choice_templated_selector/Shift32.h
    ${ZSERIO_GEN_DIR}/templates/choice_templated_selector/TemplatedChoice_uint16_Shift16.cpp
    ${ZSERIO_GEN_DIR}/templates/choice_templated_selector/TemplatedChoice_uint16_Shift16.h
    ${ZSERIO_GEN_DIR}/templates/choice_templated_selector/TemplatedChoice_uint32_Shift32.cpp
    ${ZSERIO_GEN_DIR}/templates/choice_templated_selector/TemplatedChoice_uint32_Shift32.h

    ${ZSERIO_GEN_DIR}/templates/choice_templated_field/TemplatedChoice_uint32_uint16.cpp
    ${ZSERIO_GEN_DIR}/templates/choice_templated_field/TemplatedChoice_uint32_uint16.h
    ${ZSERIO_GEN_DIR}/templates/choice_templated_field/TemplatedChoice_Compound_uint32_uint16.cpp
    ${ZSERIO_GEN_DIR}/templates/choice_templated_field/TemplatedChoice_Compound_uint32_uint16.h
    ${ZSERIO_GEN_DIR}/templates/choice_templated_field/Compound_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/choice_templated_field/Compound_uint32.h
    ${ZSERIO_GEN_DIR}/templates/choice_templated_field/ChoiceTemplatedField.cpp
    ${ZSERIO_GEN_DIR}/templates/choice_templated_field/ChoiceTemplatedField.h

    ${ZSERIO_GEN_DIR}/templates/expression_bitmask_template_argument/Permission.cpp
    ${ZSERIO_GEN_DIR}/templates/expression_bitmask_template_argument/Permission.h
    ${ZSERIO_GEN_DIR}/templates/expression_bitmask_template_argument/BitmaskTemplateArgument_Permission.cpp
    ${ZSERIO_GEN_DIR}/templates/expression_bitmask_template_argument/BitmaskTemplateArgument_Permission.h
    ${ZSERIO_GEN_DIR}/templates/expression_bitmask_template_argument/BitmaskTemplateArgumentHolder.cpp
    ${ZSERIO_GEN_DIR}/templates/expression_bitmask_template_argument/BitmaskTemplateArgumentHolder.h

    ${ZSERIO_GEN_DIR}/templates/expression_enum_template_argument/Color.cpp
    ${ZSERIO_GEN_DIR}/templates/expression_enum_template_argument/Color.h
    ${ZSERIO_GEN_DIR}/templates/expression_enum_template_argument/EnumTemplateArgument_Color.cpp
    ${ZSERIO_GEN_DIR}/templates/expression_enum_template_argument/EnumTemplateArgument_Color.h
    ${ZSERIO_GEN_DIR}/templates/expression_enum_template_argument/EnumTemplateArgumentHolder.cpp
    ${ZSERIO_GEN_DIR}/templates/expression_enum_template_argument/EnumTemplateArgumentHolder.h

    ${ZSERIO_GEN_DIR}/templates/expression_enum_template_argument_conflict/E.cpp
    ${ZSERIO_GEN_DIR}/templates/expression_enum_template_argument_conflict/E.h
    ${ZSERIO_GEN_DIR}/templates/expression_enum_template_argument_conflict/EnumTemplateArgumentConflictHolder.cpp
    ${ZSERIO_GEN_DIR}/templates/expression_enum_template_argument_conflict/EnumTemplateArgumentConflictHolder.h
    ${ZSERIO_GEN_DIR}/templates/expression_enum_template_argument_conflict/EnumTemplateArgumentConflict_Letters.cpp
    ${ZSERIO_GEN_DIR}/templates/expression_enum_template_argument_conflict/EnumTemplateArgumentConflict_Letters.h
    ${ZSERIO_GEN_DIR}/templates/expression_enum_template_argument_conflict/Letters.cpp
    ${ZSERIO_GEN_DIR}/templates/expression_enum_template_argument_conflict/Letters.h

    ${ZSERIO_GEN_DIR}/templates/expression_full_template_argument/import_color/Color.cpp
    ${ZSERIO_GEN_DIR}/templates/expression_full_template_argument/import_color/Color.h
    ${ZSERIO_GEN_DIR}/templates/expression_full_template_argument/Color.cpp
    ${ZSERIO_GEN_DIR}/templates/expression_full_template_argument/Color.h
    ${ZSERIO_GEN_DIR}/templates/expression_full_template_argument/FullTemplateArgument_Color_7C6F461F.cpp
    ${ZSERIO_GEN_DIR}/templates/expression_full_template_argument/FullTemplateArgument_Color_7C6F461F.h
    ${ZSERIO_GEN_DIR}/templates/expression_full_template_argument/FullTemplateArgument_Color_F30EBCB3.cpp
    ${ZSERIO_GEN_DIR}/templates/expression_full_template_argument/FullTemplateArgument_Color_F30EBCB3.h
    ${ZSERIO_GEN_DIR}/templates/expression_full_template_argument/FullTemplateArgumentHolder.cpp
    ${ZSERIO_GEN_DIR}/templates/expression_full_template_argument/FullTemplateArgumentHolder.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_clash_other_template/InstantiateClashOtherTemplate.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_clash_other_template/InstantiateClashOtherTemplate.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_clash_other_template/Test_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_clash_other_template/Test_uint32.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_clash_other_template/Test_uint32_99604043.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_clash_other_template/Test_uint32_99604043.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_imported/pkg
    ${ZSERIO_GEN_DIR}/templates/instantiate_imported/Test_string.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_imported/Test_string.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_imported/InstantiateImported.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_imported/InstantiateImported.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_imported/pkg/U32.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_imported/pkg/U32.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_nested_template/TStr.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_nested_template/TStr.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_nested_template/NStr.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_nested_template/NStr.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_nested_template/InstantiateNestedTemplate.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_nested_template/InstantiateNestedTemplate.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_not_imported/InstantiateNotImported.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_not_imported/InstantiateNotImported.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_not_imported/pkg/U32.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_not_imported/pkg/U32.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_not_imported/pkg/Test_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_not_imported/pkg/Test_uint32.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_not_imported/pkg/Test_string.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_not_imported/pkg/Test_string.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_only_nested/N32.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_only_nested/N32.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_only_nested/InstantiateOnlyNested.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_only_nested/InstantiateOnlyNested.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_only_nested/pkg/Test_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_only_nested/pkg/Test_uint32.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_simple/U32.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_simple/U32.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_simple/InstantiateSimple.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_simple/InstantiateSimple.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_sql_table/U32Table.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_sql_table/U32Table.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_template_argument/InstantiateTemplateArgument.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_template_argument/InstantiateTemplateArgument.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_template_argument/Other_Str.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_template_argument/Other_Str.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_template_argument/Str.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_template_argument/Str.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_choice_field/InstantiateTypeAsChoiceField.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_choice_field/InstantiateTypeAsChoiceField.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_choice_field/Test32.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_choice_field/Test32.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_parameter/InstantiateTypeAsParameter.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_parameter/InstantiateTypeAsParameter.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_parameter/P32.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_parameter/P32.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_parameter/Parameterized_P32.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_parameter/Parameterized_P32.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_sql_database_field/InstantiateTypeAsSqlDatabaseFieldDb.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_sql_database_field/InstantiateTypeAsSqlDatabaseFieldDb.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_sql_database_field/StringTable.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_sql_database_field/StringTable.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_sql_table_field/Test32Table.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_sql_table_field/Test32Table.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_sql_table_field/Test32.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_sql_table_field/Test32.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_struct_field/InstantiateTypeAsStructField.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_struct_field/InstantiateTypeAsStructField.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_struct_field/Test32.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_struct_field/Test32.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_template_argument/InstantiateTypeAsTemplateArgument.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_template_argument/InstantiateTypeAsTemplateArgument.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_template_argument/Other_Str.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_template_argument/Other_Str.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_template_argument/Str.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_as_template_argument/Str.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_type_imported_as_struct_field/InstantiateTypeImportedAsStructField.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_imported_as_struct_field/InstantiateTypeImportedAsStructField.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_imported_as_struct_field/pkg/Test32.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_imported_as_struct_field/pkg/Test32.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_type_on_parameterized_template/InstantiateTypeOnParameterizedTemplate.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_on_parameterized_template/InstantiateTypeOnParameterizedTemplate.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_on_parameterized_template/Parameterized.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_on_parameterized_template/Parameterized.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_on_parameterized_template/TestP.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_type_on_parameterized_template/TestP.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_unused/U32.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_unused/U32.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_via_import/InstantiateViaImport.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_via_import/InstantiateViaImport.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_via_import/pkg/U32.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_via_import/pkg/U32.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_via_import/pkg/Test_string.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_via_import/pkg/Test_string.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_via_single_import/InstantiateViaSingleImport.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_via_single_import/InstantiateViaSingleImport.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_via_single_import/pkg/U32.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_via_single_import/pkg/U32.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_via_single_import/pkg/Test_string.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_via_single_import/pkg/Test_string.h

    ${ZSERIO_GEN_DIR}/templates/instantiate_vs_default/TStr.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_vs_default/TStr.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_vs_default/InstantiateVsDefault.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_vs_default/InstantiateVsDefault.h
    ${ZSERIO_GEN_DIR}/templates/instantiate_vs_default/pkg/Test_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/instantiate_vs_default/pkg/Test_uint32.h

    ${ZSERIO_GEN_DIR}/templates/function_templated_return_type/TestStructure_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/function_templated_return_type/TestStructure_uint32.h
    ${ZSERIO_GEN_DIR}/templates/function_templated_return_type/TestStructure_string.cpp
    ${ZSERIO_GEN_DIR}/templates/function_templated_return_type/TestStructure_string.h
    ${ZSERIO_GEN_DIR}/templates/function_templated_return_type/TestStructure_float32.cpp
    ${ZSERIO_GEN_DIR}/templates/function_templated_return_type/TestStructure_float32.h
    ${ZSERIO_GEN_DIR}/templates/function_templated_return_type/Holder_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/function_templated_return_type/Holder_uint32.h
    ${ZSERIO_GEN_DIR}/templates/function_templated_return_type/Holder_string.cpp
    ${ZSERIO_GEN_DIR}/templates/function_templated_return_type/Holder_string.h
    ${ZSERIO_GEN_DIR}/templates/function_templated_return_type/Holder_float32.cpp
    ${ZSERIO_GEN_DIR}/templates/function_templated_return_type/Holder_float32.h
    ${ZSERIO_GEN_DIR}/templates/function_templated_return_type/FunctionTemplatedReturnType.cpp
    ${ZSERIO_GEN_DIR}/templates/function_templated_return_type/FunctionTemplatedReturnType.h

    ${ZSERIO_GEN_DIR}/templates/sql_table_templated_field/Union.cpp
    ${ZSERIO_GEN_DIR}/templates/sql_table_templated_field/Union.h
    ${ZSERIO_GEN_DIR}/templates/sql_table_templated_field/TemplatedTable_Union.cpp
    ${ZSERIO_GEN_DIR}/templates/sql_table_templated_field/TemplatedTable_Union.h
    ${ZSERIO_GEN_DIR}/templates/sql_table_templated_field/TemplatedTable_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/sql_table_templated_field/TemplatedTable_uint32.h
    ${ZSERIO_GEN_DIR}/templates/sql_table_templated_field/SqlTableTemplatedFieldDb.cpp
    ${ZSERIO_GEN_DIR}/templates/sql_table_templated_field/SqlTableTemplatedFieldDb.h
    ${ZSERIO_GEN_DIR}/templates/sql_table_templated_field/Data_Union.cpp
    ${ZSERIO_GEN_DIR}/templates/sql_table_templated_field/Data_Union.h
    ${ZSERIO_GEN_DIR}/templates/sql_table_templated_field/Data_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/sql_table_templated_field/Data_uint32.h

    ${ZSERIO_GEN_DIR}/templates/struct_full_and_short_template_argument/templated_struct/Storage.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_full_and_short_template_argument/templated_struct/Storage.h
    ${ZSERIO_GEN_DIR}/templates/struct_full_and_short_template_argument/templated_struct/StructShortNameTemplateArgument.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_full_and_short_template_argument/templated_struct/StructShortNameTemplateArgument.h
    ${ZSERIO_GEN_DIR}/templates/struct_full_and_short_template_argument/templated_struct/TemplatedStruct_Storage.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_full_and_short_template_argument/templated_struct/TemplatedStruct_Storage.h
    ${ZSERIO_GEN_DIR}/templates/struct_full_and_short_template_argument/StructFullNameTemplateArgument.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_full_and_short_template_argument/StructFullNameTemplateArgument.h

    ${ZSERIO_GEN_DIR}/templates/struct_full_name_template_argument/import_storage/Storage.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_full_name_template_argument/import_storage/Storage.h
    ${ZSERIO_GEN_DIR}/templates/struct_full_name_template_argument/Storage.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_full_name_template_argument/Storage.h
    ${ZSERIO_GEN_DIR}/templates/struct_full_name_template_argument/StructFullNameTemplateArgument.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_full_name_template_argument/StructFullNameTemplateArgument.h
    ${ZSERIO_GEN_DIR}/templates/struct_full_name_template_argument/TemplatedStruct_Storage_A3A4B101.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_full_name_template_argument/TemplatedStruct_Storage_A3A4B101.h
    ${ZSERIO_GEN_DIR}/templates/struct_full_name_template_argument/TemplatedStruct_Storage_C76E422F.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_full_name_template_argument/TemplatedStruct_Storage_C76E422F.h

    ${ZSERIO_GEN_DIR}/templates/struct_long_template_argument/StructLongTemplateArgument.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_long_template_argument/StructLongTemplateArgument.h
    ${ZSERIO_GEN_DIR}/templates/struct_long_template_argument/TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVery_.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_long_template_argument/TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVery_.h
    ${ZSERIO_GEN_DIR}/templates/struct_long_template_argument/ThisIsVeryVeryVeryLongNamedStructure.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_long_template_argument/ThisIsVeryVeryVeryLongNamedStructure.h

    ${ZSERIO_GEN_DIR}/templates/struct_long_template_argument_clash/StructLongTemplateArgumentClash.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_long_template_argument_clash/StructLongTemplateArgumentClash.h
    ${ZSERIO_GEN_DIR}/templates/struct_long_template_argument_clash/TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_Th_1B45EF08.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_long_template_argument_clash/TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_Th_1B45EF08.h
    ${ZSERIO_GEN_DIR}/templates/struct_long_template_argument_clash/TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_Th_619A1B35.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_long_template_argument_clash/TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_Th_619A1B35.h
    ${ZSERIO_GEN_DIR}/templates/struct_long_template_argument_clash/ThisIsVeryVeryVeryLongNamedStructure.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_long_template_argument_clash/ThisIsVeryVeryVeryLongNamedStructure.h
    ${ZSERIO_GEN_DIR}/templates/struct_long_template_argument_clash/ThisIsVeryVeryVeryLongNamedStructure_.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_long_template_argument_clash/ThisIsVeryVeryVeryLongNamedStructure_.h

    ${ZSERIO_GEN_DIR}/templates/struct_recursive_template/StructRecursiveTemplate.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_recursive_template/StructRecursiveTemplate.h
    ${ZSERIO_GEN_DIR}/templates/struct_recursive_template/Compound_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_recursive_template/Compound_uint32.h
    ${ZSERIO_GEN_DIR}/templates/struct_recursive_template/Compound_string.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_recursive_template/Compound_string.h
    ${ZSERIO_GEN_DIR}/templates/struct_recursive_template/Compound_Compound_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_recursive_template/Compound_Compound_uint32.h
    ${ZSERIO_GEN_DIR}/templates/struct_recursive_template/Compound_Compound_string.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_recursive_template/Compound_Compound_string.h
    ${ZSERIO_GEN_DIR}/templates/struct_recursive_template/Compound_Compound_Compound_string.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_recursive_template/Compound_Compound_Compound_string.h

    ${ZSERIO_GEN_DIR}/templates/struct_template_clash/A.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash/A.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash/A_B.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash/A_B.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash/B_C.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash/B_C.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash/C.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash/C.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash/InstantiationNameClash.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash/InstantiationNameClash.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash/Template_A_B_C_5EB4E3FC.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash/Template_A_B_C_5EB4E3FC.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash/Template_A_B_C_7FE93D34.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash/Template_A_B_C_7FE93D34.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash/TestStruct_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash/TestStruct_uint32.h

    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_across_packages/pkg1/InstantiationInPkg1.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_across_packages/pkg1/InstantiationInPkg1.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_across_packages/pkg1/Test.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_across_packages/pkg1/Test.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_across_packages/pkg2/InstantiationInPkg2.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_across_packages/pkg2/InstantiationInPkg2.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_across_packages/pkg2/Test.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_across_packages/pkg2/Test.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_across_packages/test_struct/TestStruct_Test_67B82BA5.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_across_packages/test_struct/TestStruct_Test_67B82BA5.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_across_packages/test_struct/TestStruct_Test_639610D0.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_across_packages/test_struct/TestStruct_Test_639610D0.h

    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_other_template/A_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_other_template/A_uint32.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_other_template/InstantiationNameClashOtherTemplate.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_other_template/InstantiationNameClashOtherTemplate.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_other_template/Test_A_uint32_5D68B0C2.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_other_template/Test_A_uint32_5D68B0C2.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_other_template/Test_A_uint32_FA82A3B7.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_other_template/Test_A_uint32_FA82A3B7.h

    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_other_type/InstantiationNameClashOtherType.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_other_type/InstantiationNameClashOtherType.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_other_type/Test_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_other_type/Test_uint32.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_other_type/Test_uint32_99604043.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_clash_other_type/Test_uint32_99604043.h

    ${ZSERIO_GEN_DIR}/templates/struct_templated_field/StructTemplatedField.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_templated_field/StructTemplatedField.h
    ${ZSERIO_GEN_DIR}/templates/struct_templated_field/Field_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_templated_field/Field_uint32.h
    ${ZSERIO_GEN_DIR}/templates/struct_templated_field/Field_string.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_templated_field/Field_string.h
    ${ZSERIO_GEN_DIR}/templates/struct_templated_field/Field_Compound.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_templated_field/Field_Compound.h
    ${ZSERIO_GEN_DIR}/templates/struct_templated_field/Compound.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_templated_field/Compound.h

    ${ZSERIO_GEN_DIR}/templates/struct_templated_template_argument/StructTemplatedTemplateArgument.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_templated_template_argument/StructTemplatedTemplateArgument.h
    ${ZSERIO_GEN_DIR}/templates/struct_templated_template_argument/Field_Compound_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_templated_template_argument/Field_Compound_uint32.h
    ${ZSERIO_GEN_DIR}/templates/struct_templated_template_argument/Compound_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_templated_template_argument/Compound_uint32.h

    ${ZSERIO_GEN_DIR}/templates/struct_templated_type_argument/StructTemplatedTypeArgument.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_templated_type_argument/StructTemplatedTypeArgument.h
    ${ZSERIO_GEN_DIR}/templates/struct_templated_type_argument/ParamHolder_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_templated_type_argument/ParamHolder_uint32.h
    ${ZSERIO_GEN_DIR}/templates/struct_templated_type_argument/Parameterized_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_templated_type_argument/Parameterized_uint32.h

    ${ZSERIO_GEN_DIR}/templates/struct_template_in_template/StructTemplateInTemplate.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_in_template/StructTemplateInTemplate.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_in_template/Field_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_in_template/Field_uint32.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_in_template/Field_string.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_in_template/Field_string.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_in_template/Compound_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_in_template/Compound_uint32.h
    ${ZSERIO_GEN_DIR}/templates/struct_template_in_template/Compound_string.cpp
    ${ZSERIO_GEN_DIR}/templates/struct_template_in_template/Compound_string.h

    ${ZSERIO_GEN_DIR}/templates/subtype_template_argument/AnotherCompoundType.h
    ${ZSERIO_GEN_DIR}/templates/subtype_template_argument/AnotherUInt32Type.h
    ${ZSERIO_GEN_DIR}/templates/subtype_template_argument/Compound.cpp
    ${ZSERIO_GEN_DIR}/templates/subtype_template_argument/Compound.h
    ${ZSERIO_GEN_DIR}/templates/subtype_template_argument/CompoundType.h
    ${ZSERIO_GEN_DIR}/templates/subtype_template_argument/Field_Compound.cpp
    ${ZSERIO_GEN_DIR}/templates/subtype_template_argument/Field_Compound.h
    ${ZSERIO_GEN_DIR}/templates/subtype_template_argument/Field_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/subtype_template_argument/Field_uint32.h
    ${ZSERIO_GEN_DIR}/templates/subtype_template_argument/SubtypeTemplateArgument.cpp
    ${ZSERIO_GEN_DIR}/templates/subtype_template_argument/SubtypeTemplateArgument.h
    ${ZSERIO_GEN_DIR}/templates/subtype_template_argument/UInt32Type.h

    ${ZSERIO_GEN_DIR}/templates/subtype_template_with_builtin/TestStructure_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/subtype_template_with_builtin/TestStructure_uint32.h
    ${ZSERIO_GEN_DIR}/templates/subtype_template_with_builtin/TestStructureSubtype.h
    ${ZSERIO_GEN_DIR}/templates/subtype_template_with_builtin/SubtypeTemplateWithBuiltin.cpp
    ${ZSERIO_GEN_DIR}/templates/subtype_template_with_builtin/SubtypeTemplateWithBuiltin.h

    ${ZSERIO_GEN_DIR}/templates/subtype_template_with_compound/TestStructure_Compound_TemplateCompound_Compound.cpp
    ${ZSERIO_GEN_DIR}/templates/subtype_template_with_compound/TestStructure_Compound_TemplateCompound_Compound.h
    ${ZSERIO_GEN_DIR}/templates/subtype_template_with_compound/TemplateCompound_Compound.cpp
    ${ZSERIO_GEN_DIR}/templates/subtype_template_with_compound/TemplateCompound_Compound.h
    ${ZSERIO_GEN_DIR}/templates/subtype_template_with_compound/SubtypeTemplateWithCompound.h
    ${ZSERIO_GEN_DIR}/templates/subtype_template_with_compound/Compound.cpp
    ${ZSERIO_GEN_DIR}/templates/subtype_template_with_compound/Compound.h

    ${ZSERIO_GEN_DIR}/templates/union_templated_field/UnionTemplatedField.cpp
    ${ZSERIO_GEN_DIR}/templates/union_templated_field/UnionTemplatedField.h
    ${ZSERIO_GEN_DIR}/templates/union_templated_field/TemplatedUnion_uint16_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/union_templated_field/TemplatedUnion_uint16_uint32.h
    ${ZSERIO_GEN_DIR}/templates/union_templated_field/TemplatedUnion_float32_float64.cpp
    ${ZSERIO_GEN_DIR}/templates/union_templated_field/TemplatedUnion_float32_float64.h
    ${ZSERIO_GEN_DIR}/templates/union_templated_field/TemplatedUnion_Compound_uint16_Compound_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/union_templated_field/TemplatedUnion_Compound_uint16_Compound_uint32.h
    ${ZSERIO_GEN_DIR}/templates/union_templated_field/Compound_uint32.cpp
    ${ZSERIO_GEN_DIR}/templates/union_templated_field/Compound_uint32.h
    ${ZSERIO_GEN_DIR}/templates/union_templated_field/Compound_uint16.cpp
    ${ZSERIO_GEN_DIR}/templates/union_templated_field/Compound_uint16.h
    ${ZSERIO_GEN_DIR}/templates/union_templated_field/Compound_float32.cpp
    ${ZSERIO_GEN_DIR}/templates/union_templated_field/Compound_float32.h
    ${ZSERIO_GEN_DIR}/templates/union_templated_field/Compound_Compound_uint16.cpp
    ${ZSERIO_GEN_DIR}/templates/union_templated_field/Compound_Compound_uint16.h
)

zserio_add_library(TARGET templates_zs
    SOURCE_DIR ${CMAKE_CURRENT_SOURCE_DIR}/zs
    MAIN_SOURCE templates.zs
    OUT_DIR ${ZSERIO_GEN_DIR}
    OUT_FILES ${ZSERIO_GENERATED_SOURCES}
    ZSERIO_CORE_DIR ${ZSERIO_JAR_DIR}
    ZSERIO_CPP_DIR ${ZSERIO_CPP_DISTR_DIR}
    ZSERIO_OPTIONS -withoutSourcesAmalgamation
)

target_include_directories(templates_zs SYSTEM PRIVATE ${SQLITE_INCDIR})

add_test_sources(
    cpp/ChoiceTemplatedEnumSelectorTest.cpp
    cpp/ChoiceTemplatedFieldTest.cpp
    cpp/ChoiceTemplatedSelectorTest.cpp
    cpp/ExpressionBitmaskTemplateArgumentTest.cpp
    cpp/ExpressionEnumTemplateArgumentTest.cpp
    cpp/ExpressionEnumTemplateArgumentConflictTest.cpp
    cpp/ExpressionFullTemplateArgumentTest.cpp
    cpp/InstantiateClashOtherTemplateTest.cpp
    cpp/InstantiateImportedTest.cpp
    cpp/InstantiateNestedTemplateTest.cpp
    cpp/InstantiateNotImportedTest.cpp
    cpp/InstantiateOnlyNestedTest.cpp
    cpp/InstantiateSimpleTest.cpp
    cpp/InstantiateSqlTableTest.cpp
    cpp/InstantiateTemplateArgumentTest.cpp
    cpp/InstantiateTypeAsChoiceFieldTest.cpp
    cpp/InstantiateTypeAsParameterTest.cpp
    cpp/InstantiateTypeAsSqlDatabaseFieldTest.cpp
    cpp/InstantiateTypeAsSqlTableFieldTest.cpp
    cpp/InstantiateTypeAsStructFieldTest.cpp
    cpp/InstantiateTypeAsTemplateArgumentTest.cpp
    cpp/InstantiateTypeImportedAsStructFieldTest.cpp
    cpp/InstantiateTypeOnParameterizedTemplateTest.cpp
    cpp/InstantiateUnusedTest.cpp
    cpp/InstantiateViaImportTest.cpp
    cpp/InstantiateViaSingleImportTest.cpp
    cpp/InstantiateVsDefaultTest.cpp
    cpp/FunctionTemplatedReturnTypeTest.cpp
    cpp/SqlTableTemplatedFieldTest.cpp
    cpp/StructFullAndShortTemplateArgumentTest.cpp
    cpp/StructFullNameTemplateArgumentTest.cpp
    cpp/StructLongTemplateArgumentTest.cpp
    cpp/StructLongTemplateArgumentClashTest.cpp
    cpp/StructRecursiveTemplateTest.cpp
    cpp/StructTemplateClashTest.cpp
    cpp/StructTemplateClashAcrossPackagesTest.cpp
    cpp/StructTemplateClashOtherTemplateTest.cpp
    cpp/StructTemplateClashOtherTypeTest.cpp
    cpp/StructTemplatedFieldTest.cpp
    cpp/StructTemplatedTemplateArgumentTest.cpp
    cpp/StructTemplatedTypeArgumentTest.cpp
    cpp/StructTemplateInTemplateTest.cpp
    cpp/SubtypeTemplateArgumentTest.cpp
    cpp/SubtypeTemplateWithBuiltinTest.cpp
    cpp/SubtypeTemplateWithCompoundTest.cpp
    cpp/UnionTemplatedFieldTest.cpp
)

add_test_dependencies(templates_zs)
