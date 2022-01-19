import unittest

from testutils import compileErroneousZserio, assertErrorsPresent

class ModuleNamesErrorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.errors = {}

        compileErroneousZserio(__file__, "module_names/bitmask_with_enum_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "module_names/choice_with_union_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "module_names/const_with_const_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "module_names/const_with_structure_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "module_names/instantiate_type_with_structure_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "module_names/instantiation_with_structure_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "module_names/pubsub_with_service_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "module_names/sql_database_with_sql_table_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "module_names/structure_with_service_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "module_names/structure_with_subtype_clash_error.zs", cls.errors)

    def testBitmaskWithEnumClashError(self):
        assertErrorsPresent(self,
            "module_names/bitmask_with_enum_clash_error.zs",
            [
                ":9:12: Module 'color_info' generated for package symbol 'Color_Info' " +
                "clashes with module generated for package symbol 'ColorInfo' defined at 3:15!",
                "[ERROR] Python Generator: Module name clashing detected!"
            ]
        )

    def testChoiceWithUnionClashError(self):
        assertErrorsPresent(self,
            "module_names/choice_with_union_clash_error.zs",
            [
                ":11:7: Module 'some_name' generated for package symbol 'Some_Name' " +
                "clashes with module generated for package symbol 'SomeName' defined at 3:8!",
                "[ERROR] Python Generator: Module name clashing detected!"
            ]
        )

    def testConstWithConstClashError(self):
        assertErrorsPresent(self,
            "module_names/const_with_const_clash_error.zs",
            [
                ":4:14: Module 'some_name' generated for package symbol 'SomeName' " +
                "clashes with module generated for package symbol 'SOME_NAME' defined at 3:14!",
                "[ERROR] Python Generator: Module name clashing detected!"
            ]
        )

    def testConstWithStructureClashError(self):
        assertErrorsPresent(self,
            "module_names/const_with_structure_clash_error.zs",
            [
                ":5:8: Module 'some_name' generated for package symbol 'SomeName' " +
                "clashes with module generated for package symbol 'SOME_NAME' defined at 3:14!",
                "[ERROR] Python Generator: Module name clashing detected!"
            ]
        )

    def testInstantiateTypeWithStructureClashError(self):
        assertErrorsPresent(self,
            "module_names/instantiate_type_with_structure_clash_error.zs",
            [
                ":13:13: In instantiation of 'Other' required from here",
                ":8:8: Module 'some_name' generated for package symbol 'SomeName' " +
                "clashes with module generated for package symbol 'Some_Name' defined at 3:8!",
                "[ERROR] Python Generator: Module name clashing detected!"
            ]
        )

    def testInstantiationWithStructureClashError(self):
        assertErrorsPresent(self,
            "module_names/instantiation_with_structure_clash_error.zs",
            [
                ":5:5: In instantiation of 'Some' required from here",
                ":8:8: Module 'some_name' generated for package symbol 'Some_Name' " +
                "clashes with module generated for package symbol 'SomeName' defined at 3:8!",
                "[ERROR] Python Generator: Module name clashing detected!"
            ]
        )

    def testPubsubWithServiceClashError(self):
        assertErrorsPresent(self,
            "module_names/pubsub_with_service_clash_error.zs",
            [
                ":18:8: Module 'math_service' generated for package symbol 'MathService' " +
                "clashes with module generated for package symbol 'Math_Service' defined at 13:9!",
                "[ERROR] Python Generator: Module name clashing detected!"
            ]
        )

    def testSqlDatabaseWithSqlTableClashError(self):
        assertErrorsPresent(self,
            "module_names/sql_database_with_sql_table_clash_error.zs",
            [
                ":9:14: Module 'some_good_name' generated for package symbol 'SomeGoodName' " +
                "clashes with module generated for package symbol 'Some_Good_Name' defined at 3:11!",
                "[ERROR] Python Generator: Module name clashing detected!"
            ]
        )

    def testStructureWithServiceClashError(self):
        assertErrorsPresent(self,
            "module_names/structure_with_service_clash_error.zs",
            [
                ":8:9: Module 'some_name' generated for package symbol 'Some_Name' " +
                "clashes with module generated for package symbol 'SomeName' defined at 3:8!",
                "[ERROR] Python Generator: Module name clashing detected!"
            ]
        )

    def testStructureWithSubtypeClashError(self):
        assertErrorsPresent(self,
            "module_names/structure_with_subtype_clash_error.zs",
            [
                ":8:24: Module 'some_good_name' generated for package symbol 'SomeGoodName' " +
                "clashes with module generated for package symbol 'SOME_GOOD_NAME' defined at 3:8!",
                "[ERROR] Python Generator: Module name clashing detected!"
            ]
        )
