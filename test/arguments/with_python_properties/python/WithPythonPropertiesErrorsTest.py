import os
import shutil
import unittest

from testutils import getZserioApi, getApiDir, ZserioCompilerError

class WithPythonPropertiesErrorsTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.errors = {}
        cls._compileErroneousZserio("errors/choice_function_property_clash_error.zs")
        cls._compileErroneousZserio("errors/choice_invalid_property_name_private_error.zs")
        cls._compileErroneousZserio("errors/choice_invalid_property_name_reserved_error.zs")
        cls._compileErroneousZserio("errors/choice_public_method_property_clash_error.zs")
        cls._compileErroneousZserio("errors/structure_function_property_clash_error.zs")
        cls._compileErroneousZserio("errors/structure_indicator_property_clash_error.zs")
        cls._compileErroneousZserio("errors/structure_invalid_property_name_private_error.zs")
        cls._compileErroneousZserio("errors/structure_invalid_property_name_reserved_error.zs")
        cls._compileErroneousZserio("errors/structure_public_method_property_clash_error.zs")
        cls._compileErroneousZserio("errors/sql_database_connection_property_clash_error.zs")
        cls._compileErroneousZserio("errors/sql_database_invalid_property_name_reserved_error.zs")
        cls._compileErroneousZserio("errors/sql_database_table_name_constant_property_clash_error.zs")
        cls._compileErroneousZserio("errors/sql_database_public_method_property_clash_error.zs")
        cls._compileErroneousZserio("errors/union_choice_tag_property_clash_error.zs")
        cls._compileErroneousZserio("errors/union_function_property_clash_error.zs")
        cls._compileErroneousZserio("errors/union_invalid_property_name_private_error.zs")
        cls._compileErroneousZserio("errors/union_invalid_property_name_reserved_error.zs")
        cls._compileErroneousZserio("errors/union_public_method_property_clash_error.zs")

    def testChoiceFunctionPropertyClash(self):
        self._assertErrorsPresent(
            "errors/choice_function_property_clash_error.zs",
            [
                "3:8: Property name error detected in 'TestChoice'!",
                WithPythonPropertiesErrorsTest._getPropertyClashError("funcArray")
            ]
        )

    def testChoiceInvalidPropertyNamePrivate(self):
        self._assertErrorsPresent(
            "errors/choice_invalid_property_name_private_error.zs",
            [
                "3:8: Property name error detected in 'TestChoice'!",
                WithPythonPropertiesErrorsTest._getPropertyRuleError("_choice")
            ]
        )

    def testChoiceInvalidPropertyNameReserved(self):
        self._assertErrorsPresent(
            "errors/choice_invalid_property_name_reserved_error.zs",
            [
                "3:8: Property name error detected in 'TestChoice'!",
                WithPythonPropertiesErrorsTest._getPropertyRuleError("__str__")
            ]
        )

    def testChoicePublicMethodPropertyClash(self):
        self._assertErrorsPresent(
            "errors/choice_public_method_property_clash_error.zs",
            [
                "3:8: Property name error detected in 'TestChoice'!",
                WithPythonPropertiesErrorsTest._getPropertyClashError("write")
            ]
        )

    def testStructureFunctionPropertyClash(self):
        self._assertErrorsPresent(
            "errors/structure_function_property_clash_error.zs",
            [
                "3:8: Property name error detected in 'TestStructure'!",
                WithPythonPropertiesErrorsTest._getPropertyClashError("funcTest")
            ]
        )

    def testStructureIndicatorPropertyClash(self):
        self._assertErrorsPresent(
            "errors/structure_indicator_property_clash_error.zs",
            [
                "3:8: Property name error detected in 'TestStructure'!",
                WithPythonPropertiesErrorsTest._getPropertyClashError("isFieldUsed")
            ]
        )

    def testStructureInvalidPropertyNamePrivate(self):
        self._assertErrorsPresent(
            "errors/structure_invalid_property_name_private_error.zs",
            [
                "3:8: Property name error detected in 'TestStructure'!",
                WithPythonPropertiesErrorsTest._getPropertyRuleError("_field")
            ]
        )

    def testStructureInvalidPropertyNameReserved(self):
        self._assertErrorsPresent(
            "errors/structure_invalid_property_name_reserved_error.zs",
            [
                "3:8: Property name error detected in 'TestStructure'!",
                WithPythonPropertiesErrorsTest._getPropertyRuleError("__eq__")
            ]
        )

    def testStructurePublicMethodPropertyClash(self):
        self._assertErrorsPresent(
            "errors/structure_public_method_property_clash_error.zs",
            [
                "3:8: Property name error detected in 'TestStructure'!",
                WithPythonPropertiesErrorsTest._getPropertyClashError("read")
            ]
        )

    def testSqlDatabaseConnectionPropertyClash(self):
        self._assertErrorsPresent(
            "errors/sql_database_connection_property_clash_error.zs",
            [
                "9:14: Property name error detected in 'TestDatabase'!",
                WithPythonPropertiesErrorsTest._getPropertyClashError("connection")
            ]
        )

    def testSqlDatabaseInvalidPropertyNameReserved(self):
        self._assertErrorsPresent(
            "errors/sql_database_invalid_property_name_reserved_error.zs",
            [
                "9:14: Property name error detected in 'TestDatabase'!",
                WithPythonPropertiesErrorsTest._getPropertyRuleError("__init__")
            ]
        )

    def testSqlDatabaseTableNameConstantPropertyClash(self):
        self._assertErrorsPresent(
            "errors/sql_database_table_name_constant_property_clash_error.zs",
            [
                "9:14: Property name error detected in 'TestDatabase'!",
                WithPythonPropertiesErrorsTest._getPropertyClashError("TABLE_NAME_first")
            ]
        )

    def testSqlDatabasePublicMethodPropertyClash(self):
        self._assertErrorsPresent(
            "errors/sql_database_public_method_property_clash_error.zs",
            [
                "9:14: Property name error detected in 'TestDatabase'!",
                WithPythonPropertiesErrorsTest._getPropertyClashError("fromFile")
            ]
        )

    def testUnionChoiceTagPropertyClash(self):
        self._assertErrorsPresent(
            "errors/union_choice_tag_property_clash_error.zs",
            [
                "3:7: Property name error detected in 'TestUnion'!",
                WithPythonPropertiesErrorsTest._getPropertyClashError("choiceTag")
            ]
        )

    def testUnionFunctionPropertyClash(self):
        self._assertErrorsPresent(
            "errors/union_function_property_clash_error.zs",
            [
                "3:7: Property name error detected in 'TestUnion'!",
                WithPythonPropertiesErrorsTest._getPropertyClashError("funcMyFunc")
            ]
        )

    def testUnionInvalidPropertyNamePrivate(self):
        self._assertErrorsPresent(
            "errors/union_invalid_property_name_private_error.zs",
            [
                "3:7: Property name error detected in 'TestUnion'!",
                WithPythonPropertiesErrorsTest._getPropertyRuleError("_choice")
            ]
        )

    def testUnionInvalidPropertyNameReserved(self):
        self._assertErrorsPresent(
            "errors/union_invalid_property_name_reserved_error.zs",
            [
                "3:7: Property name error detected in 'TestUnion'!",
                WithPythonPropertiesErrorsTest._getPropertyRuleError("__hash__")
            ]
        )

    def testUnionPublicMethodPropertyClash(self):
        self._assertErrorsPresent(
            "errors/union_public_method_property_clash_error.zs",
            [
                "3:7: Property name error detected in 'TestUnion'!",
                WithPythonPropertiesErrorsTest._getPropertyClashError("bitSizeOf")
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
            getZserioApi(__file__, mainZsFile, extraArgs=["-withPythonProperties"])
        except ZserioCompilerError as zserioCompilerError:
            cls.errors[mainZsFile] = zserioCompilerError.stderr
            # cleanup partially generated api to prevent pylint/mypy from checking it
            apiDir = os.path.join(getApiDir(os.path.dirname(__file__)), "errors")
            if os.path.exists(apiDir):
                shutil.rmtree(apiDir)

    @staticmethod
    def _getPropertyClashError(propertyName):
        return ("[ERROR] Python Generator: Invalid property name '" + propertyName + "'! " +
                "Property name clashes with generated API!")

    @staticmethod
    def _getPropertyRuleError(propertyName):
        return ("[ERROR] Python Generator: Invalid property name '" + propertyName + "'! " +
                "Property names cannot start with '_'!")
