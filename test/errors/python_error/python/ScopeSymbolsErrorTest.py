import unittest

from testutils import compileErroneousZserio, assertErrorsPresent

class ScopeSymbolsErrorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.errors = {}

        compileErroneousZserio(__file__, "scope_symbols/choice_field_names_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/choice_parameter_names_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/pubsub_message_names_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/service_method_names_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/sql_database_table_names_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/sql_table_column_names_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/structure_field_names_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/structure_field_with_parameter_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "scope_symbols/union_field_names_clash_error.zs", cls.errors)

    def testChoiceFieldNamesClash(self):
        assertErrorsPresent(self,
            "scope_symbols/choice_field_names_clash_error.zs",
            [
                ":8:17: Symbol name 'some_field' clashes with 'someField' " +
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

    def testUnionFieldNamesClash(self):
        assertErrorsPresent(self,
            "scope_symbols/union_field_names_clash_error.zs",
            [
                ":6:13: Symbol name 'someField' clashes with 'some_field' " +
                "since both are generated equally in Python code!",
                "[ERROR] Python Generator: Symbol name clash detected!"
            ]
        )
