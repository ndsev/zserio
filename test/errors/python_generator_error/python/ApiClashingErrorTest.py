import unittest

from testutils import compileErroneousZserio, assertErrorsPresent

class ApiClashingErrorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.errors = {}
        compileErroneousZserio(__file__, "api_clashing/api_package_with_api_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "api_clashing/bitmask_with_api_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "api_clashing/choice_with_api_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "api_clashing/const_with_api_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "api_clashing/enumeration_with_api_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "api_clashing/instantiate_type_with_api_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "api_clashing/pubsub_with_api_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "api_clashing/service_with_api_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "api_clashing/sql_database_with_api_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "api_clashing/sql_table_with_api_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "api_clashing/structure_with_api_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "api_clashing/subtype_with_api_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "api_clashing/union_with_api_clash_error.zs", cls.errors)

    def testApiPackageWithApiClash(self):
        assertErrorsPresent(self,
            "api_clashing/api_package_with_api_clash_error.zs",
            [
                ":1:9: Cannot generate python package 'api' for package " +
                "'api_clashing.api_package_with_api_clash_error.api.some_package', " +
                "since it would clash with auto-generated 'api.py'! Please choose different package name.",
                "[ERROR] Python Generator: Clash in generated code detected!"
            ]
        )

    def testBitmaskWithApiClash(self):
        assertErrorsPresent(self,
            "api_clashing/bitmask_with_api_clash_error.zs",
            [
                ":3:15: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testChoiceWithApiClash(self):
        assertErrorsPresent(self,
            "api_clashing/choice_with_api_clash_error.zs",
            [
                ":3:8: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testConstWithApiClash(self):
        assertErrorsPresent(self,
            "api_clashing/const_with_api_clash_error.zs",
            [
                ":3:14: Cannot generate python source 'API.py' for symbol 'API', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testEnumerationWithApiClash(self):
        assertErrorsPresent(self,
            "api_clashing/enumeration_with_api_clash_error.zs",
            [
                ":3:12: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testInstantiateTypeWithApiClash(self):
        assertErrorsPresent(self,
            "api_clashing/instantiate_type_with_api_clash_error.zs",
            [
                ":8:33: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testPubsubWithApiClash(self):
        assertErrorsPresent(self,
            "api_clashing/pubsub_with_api_clash_error.zs",
            [
                ":15:8: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testServiceWithApiClash(self):
        assertErrorsPresent(self,
            "api_clashing/service_with_api_clash_error.zs",
            [
                ":13:9: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testSqlDatabaseWithApiClash(self):
        assertErrorsPresent(self,
            "api_clashing/sql_database_with_api_clash_error.zs",
            [
                ":9:14: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testSqlTableWithApiClash(self):
        assertErrorsPresent(self,
            "api_clashing/sql_table_with_api_clash_error.zs",
            [
                ":3:11: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testStructureWithApiClash(self):
        assertErrorsPresent(self,
            "api_clashing/structure_with_api_clash_error.zs",
            [
                ":3:8: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testSubtypeWithApiClash(self):
        assertErrorsPresent(self,
            "api_clashing/subtype_with_api_clash_error.zs",
            [
                ":3:17: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testUnionWithApiClash(self):
        assertErrorsPresent(self,
            "api_clashing/union_with_api_clash_error.zs",
            [
                ":3:7: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )
