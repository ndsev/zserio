import unittest

from testutils import getZserioApi, ZserioCompilerError

class ApiClashingErrorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.errors = {}
        cls._compileErroneousZserio("api_clashing/bitmask_with_api_clash_error.zs")
        cls._compileErroneousZserio("api_clashing/choice_with_api_clash_error.zs")
        cls._compileErroneousZserio("api_clashing/const_with_api_clash_error.zs")
        cls._compileErroneousZserio("api_clashing/enumeration_with_api_clash_error.zs")
        cls._compileErroneousZserio("api_clashing/instantiate_type_with_api_clash_error.zs")
        cls._compileErroneousZserio("api_clashing/pubsub_with_api_clash_error.zs")
        cls._compileErroneousZserio("api_clashing/service_with_api_clash_error.zs")
        cls._compileErroneousZserio("api_clashing/sql_database_with_api_clash_error.zs")
        cls._compileErroneousZserio("api_clashing/sql_table_with_api_clash_error.zs")
        cls._compileErroneousZserio("api_clashing/structure_with_api_clash_error.zs")
        cls._compileErroneousZserio("api_clashing/subtype_with_api_clash_error.zs")
        cls._compileErroneousZserio("api_clashing/union_with_api_clash_error.zs")

    def testBitmaskWithApiClash(self):
        self._assertErrorsPresent(
            "api_clashing/bitmask_with_api_clash_error.zs",
            [
                "3:15: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testChoiceWithApiClash(self):
        self._assertErrorsPresent(
            "api_clashing/choice_with_api_clash_error.zs",
            [
                "3:8: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testConstWithApiClash(self):
        self._assertErrorsPresent(
            "api_clashing/const_with_api_clash_error.zs",
            [
                "3:14: Cannot generate python source 'API.py' for symbol 'API', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testEnumerationWithApiClash(self):
        self._assertErrorsPresent(
            "api_clashing/enumeration_with_api_clash_error.zs",
            [
                "3:12: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testInstantiateTypeWithApiClash(self):
        self._assertErrorsPresent(
            "api_clashing/instantiate_type_with_api_clash_error.zs",
            [
                "8:33: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testPubsubWithApiClash(self):
        self._assertErrorsPresent(
            "api_clashing/pubsub_with_api_clash_error.zs",
            [
                "15:8: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testServiceWithApiClash(self):
        self._assertErrorsPresent(
            "api_clashing/service_with_api_clash_error.zs",
            [
                "13:9: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testSqlDatabaseWithApiClash(self):
        self._assertErrorsPresent(
            "api_clashing/sql_database_with_api_clash_error.zs",
            [
                "9:14: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testSqlTableWithApiClash(self):
        self._assertErrorsPresent(
            "api_clashing/sql_table_with_api_clash_error.zs",
            [
                "3:11: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testStructureWithApiClash(self):
        self._assertErrorsPresent(
            "api_clashing/structure_with_api_clash_error.zs",
            [
                "3:8: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testSubtypeWithApiClash(self):
        self._assertErrorsPresent(
            "api_clashing/subtype_with_api_clash_error.zs",
            [
                "3:17: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def testUnionWithApiClash(self):
        self._assertErrorsPresent(
            "api_clashing/union_with_api_clash_error.zs",
            [
                "3:7: Cannot generate python source 'Api.py' for symbol 'Api', " +
                "since it would clash with auto-generated 'api.py'! Please choose different name.",
                "Python Generator: Clash in generated code detected!"
            ]
        )

    def _assertErrorsPresent(self, mainZsFile, expectedErrors):
        self.assertIn(mainZsFile, self.errors, msg=("No error found for '%s'!" % mainZsFile))
        error = self.errors[mainZsFile]
        for expectedError in expectedErrors:
            self.assertIn(expectedError, error)

    @classmethod
    def _compileErroneousZserio(cls, mainZsFile):
        try:
            getZserioApi(__file__, mainZsFile)
        except ZserioCompilerError as zserioCompilerError:
            cls.errors[mainZsFile] = zserioCompilerError.stderr
