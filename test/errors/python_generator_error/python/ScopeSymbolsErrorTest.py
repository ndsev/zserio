import unittest

from testutils import compileErroneousZserio, assertErrorsPresent

class ScopeSymbolsErrorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.errors = {}

        compileErroneousZserio(__file__, "scope_symbols/bitmask_value_camel_case_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/bitmask_value_pascal_case_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/bitmask_value_snake_case_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/choice_field_names_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/choice_parameter_names_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/choice_parameter_with_function_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/enum_item_camel_case_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/enum_item_pascal_case_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/enum_item_snake_case_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/pubsub_message_names_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/service_method_names_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/sql_database_table_names_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/sql_table_column_names_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/structure_field_names_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/structure_field_with_parameter_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/structure_function_names_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/union_field_names_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/union_field_with_function_clash_error.zs", cls.errors)

    def testBitmaskValueCamelCaseClash(self):
        assertErrorsPresent(self,
            "scope_symbols/bitmask_value_camel_case_clash_error.zs",
            [
                ":9:5: Symbol name 'createPermission' clashes with 'CREATE_PERMISSION' " +
                "since both are generated equally in Python code!",
                "[ERROR] Python Generator: Symbol name clash detected!"
            ]
        )

    def testBitmaskValuePascalCaseClash(self):
        assertErrorsPresent(self,
            "scope_symbols/bitmask_value_pascal_case_clash_error.zs",
            [
                ":9:5: Symbol name 'CreatePermission' clashes with 'CREATE_PERMISSION' " +
                "since both are generated equally in Python code!",
                "[ERROR] Python Generator: Symbol name clash detected!"
            ]
        )

    def testBitmaskValueSnakeCaseClash(self):
        assertErrorsPresent(self,
            "scope_symbols/bitmask_value_snake_case_clash_error.zs",
            [
                ":9:5: Symbol name 'create_permission' clashes with 'CREATE_PERMISSION' " +
                "since both are generated equally in Python code!",
                "[ERROR] Python Generator: Symbol name clash detected!"
            ]
        )

    def testChoiceFieldNamesClash(self):
        assertErrorsPresent(self,
            "scope_symbols/choice_field_names_clash_error.zs",
            [
                ":8:17: Symbol name 'some_field' clashes with 'someField' " +
                "since both are generated equally in Python code!",
                "[ERROR] Python Generator: Symbol name clash detected!"
            ]
        )

    def testChoiceParameterWithFunctionClash(self):
        assertErrorsPresent(self,
            "scope_symbols/choice_parameter_with_function_clash_error.zs",
            [
                ":12:19: Symbol name 'func_array' clashes with 'funcArray' " +
                "since both are generated equally in Python code!",
                "[ERROR] Python Generator: Symbol name clash detected!"
            ]
        )

    def testChoiceParameterNamesClash(self):
        assertErrorsPresent(self,
            "scope_symbols/choice_parameter_names_clash_error.zs",
            [
                ":3:42: Symbol name 'some_param' clashes with 'someParam' " +
                "since both are generated equally in Python code!",
                "[ERROR] Python Generator: Symbol name clash detected!"
            ]
        )

    def testEnumItemCamelCaseClash(self):
        assertErrorsPresent(self,
            "scope_symbols/enum_item_camel_case_clash_error.zs",
            [
                ":9:5: Symbol name 'darkGreen' clashes with 'DARK_GREEN' " +
                "since both are generated equally in Python code!",
                "[ERROR] Python Generator: Symbol name clash detected!"
            ]
        )

    def testEnumItemPascalCaseClash(self):
        assertErrorsPresent(self,
            "scope_symbols/enum_item_pascal_case_clash_error.zs",
            [
                ":9:5: Symbol name 'DarkGreen' clashes with 'DARK_GREEN' " +
                "since both are generated equally in Python code!",
                "[ERROR] Python Generator: Symbol name clash detected!"
            ]
        )

    def testEnumItemSnakeCaseClash(self):
        assertErrorsPresent(self,
            "scope_symbols/enum_item_snake_case_clash_error.zs",
            [
                ":9:5: Symbol name 'dark_green' clashes with 'DARK_GREEN' " +
                "since both are generated equally in Python code!",
                "[ERROR] Python Generator: Symbol name clash detected!"
            ]
        )

    def testPubsubMessageNamesClash(self):
        assertErrorsPresent(self,
            "scope_symbols/pubsub_message_names_clash_error.zs",
            [
                ":16:60: Symbol name 'powerOfTwo' clashes with 'power_of_two' " +
                "since both are generated equally in Python code!",
                "[ERROR] Python Generator: Symbol name clash detected!"
            ]
        )

    def testServiceMethodNamesClash(self):
        assertErrorsPresent(self,
            "scope_symbols/service_method_names_clash_error.zs",
            [
                ":16:14: Symbol name 'power_of_two' clashes with 'powerOfTwo' " +
                "since both are generated equally in Python code!",
                "[ERROR] Python Generator: Symbol name clash detected!"
            ]
        )

    def testSqlDatabaseTableNamesClash(self):
        assertErrorsPresent(self,
            "scope_symbols/sql_database_table_names_clash_error.zs",
            [
                ":12:15: Symbol name 'test_table' clashes with 'testTable' " +
                "since both are generated equally in Python code!",
                "[ERROR] Python Generator: Symbol name clash detected!"
            ]
        )

    def testSqlTableColumnNamesClash(self):
        assertErrorsPresent(self,
            "scope_symbols/sql_table_column_names_clash_error.zs",
            [
                ":6:12: Symbol name 'some_id' clashes with 'someId' " +
                "since both are generated equally in Python code!",
                "[ERROR] Python Generator: Symbol name clash detected!"
            ]
        )

    def testStructureFieldNamesNamesClash(self):
        assertErrorsPresent(self,
            "scope_symbols/structure_field_names_clash_error.zs",
            [
                ":6:13: Symbol name 'someField' clashes with 'some_field' " +
                "since both are generated equally in Python code!",
                "[ERROR] Python Generator: Symbol name clash detected!"
            ]
        )

    def testStructureFieldWithParameterClash(self):
        assertErrorsPresent(self,
            "scope_symbols/structure_field_with_parameter_clash_error.zs",
            [
                ":5:12: Symbol name 'some_identifier' clashes with 'someIdentifier' " +
                "since both are generated equally in Python code!",
                "[ERROR] Python Generator: Symbol name clash detected!"
            ]
        )

    def testStructureFunctionNamesNamesClash(self):
        assertErrorsPresent(self,
            "scope_symbols/structure_function_names_clash_error.zs",
            [
                ":10:19: Symbol name 'someName' clashes with 'some_name' " +
                "since both are generated equally in Python code!",
                "[ERROR] Python Generator: Symbol name clash detected!"
            ]
        )

    def testUnionFieldNamesClash(self):
        assertErrorsPresent(self,
            "scope_symbols/union_field_names_clash_error.zs",
            [
                ":6:13: Symbol name 'someField' clashes with 'some_field' " +
                "since both are generated equally in Python code!",
                "[ERROR] Python Generator: Symbol name clash detected!"
            ]
        )

    def testUnionFieldWithFunctionClash(self):
        assertErrorsPresent(self,
            "scope_symbols/union_field_with_function_clash_error.zs",
            [
                ":7:21: Symbol name 'someField' clashes with 'some_field' " +
                "since both are generated equally in Python code!",
                "[ERROR] Python Generator: Symbol name clash detected!"
            ]
        )
