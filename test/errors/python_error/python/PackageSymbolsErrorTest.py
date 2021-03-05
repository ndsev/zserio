import unittest

from testutils import compileErroneousZserio, assertErrorsPresent

class PackageSymbolsErrorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.errors = {}

        compileErroneousZserio(__file__, "package_symbols/choice_with_union_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "package_symbols/const_with_structure_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "package_symbols/instantiate_type_with_structure_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "package_symbols/instantiation_with_structure_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "package_symbols/pubsub_with_service_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "package_symbols/sql_database_with_sql_table_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "package_symbols/structure_with_service_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "package_symbols/structure_with_structure_clash_error.zs", cls.errors)

    def testChoiceWithUnionClashError(self):
        assertErrorsPresent(self,
            "package_symbols/choice_with_union_clash_error.zs",
            [
                ":11:7: Module 'some_name' generated for package symbol 'Some_Name' " +
                "clashes with module generated for package symbol 'SomeName' defined at 3:8!",
                "[ERROR] Python Generator: Module name clashing detected!"
            ]
        )

    def testConstWithStructureClashError(self):
        assertErrorsPresent(self,
            "package_symbols/const_with_structure_clash_error.zs",
            [
                ":5:8: Module 'some_name' generated for package symbol 'SomeName' " +
                "clashes with module generated for package symbol 'SOME_NAME' defined at 3:14!",
                "[ERROR] Python Generator: Module name clashing detected!"
            ]
        )

    def testInstantiateTypeWithStructureClashError(self):
        assertErrorsPresent(self,
            "package_symbols/instantiate_type_with_structure_clash_error.zs",
            [
                ":8:8: Module 'some_name' generated for package symbol 'SomeName' " +
                "clashes with module generated for package symbol 'Some_Name' defined at 3:8!",
                "[ERROR] Python Generator: Module name clashing detected!"
            ]
        )

    def testInstantiationWithStructureClashError(self):
        assertErrorsPresent(self,
            "package_symbols/instantiation_with_structure_clash_error.zs",
            [
                ":8:8: Module 'some_name' generated for package symbol 'Some_Name' " +
                "clashes with module generated for package symbol 'SomeName' defined at 3:8!",
                "[ERROR] Python Generator: Module name clashing detected!"
            ]
        )

    def testPubsubWithServiceClashError(self):
        assertErrorsPresent(self,
            "package_symbols/pubsub_with_service_clash_error.zs",
            [
                ":18:8: Module 'math_service' generated for package symbol 'MathService' " +
                "clashes with module generated for package symbol 'Math_Service' defined at 13:9!",
                "[ERROR] Python Generator: Module name clashing detected!"
            ]
        )

    def testSqlDatabaseWithSqlTableClashError(self):
        assertErrorsPresent(self,
            "package_symbols/sql_database_with_sql_table_clash_error.zs",
            [
                ":9:14: Module 'some_good_name' generated for package symbol 'SomeGoodName' " +
                "clashes with module generated for package symbol 'Some_Good_Name' defined at 3:11!",
                "[ERROR] Python Generator: Module name clashing detected!"
            ]
        )

    def testStructureWithServiceClashError(self):
        assertErrorsPresent(self,
            "package_symbols/structure_with_service_clash_error.zs",
            [
                ":8:9: Module 'some_name' generated for package symbol 'Some_Name' " +
                "clashes with module generated for package symbol 'SomeName' defined at 3:8!",
                "[ERROR] Python Generator: Module name clashing detected!"
            ]
        )

    def StructureWithStructureClashError(self):
        assertErrorsPresent(self,
            "package_symbols/structure_with_structure_clash_error.zs",
            [
                ":8:8: Module 'some_name' generated for package symbol 'Some_Good_Name' " +
                "clashes with module generated for package symbol 'SOME_GOOD_NAME' defined at 3:8!",
                "[ERROR] Python Generator: Module name clashing detected!"
            ]
        )
