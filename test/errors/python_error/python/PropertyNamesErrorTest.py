import unittest

from testutils import compileErroneousZserio, assertErrorsPresent

class PropertyNamesErrorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.errors = {}

        compileErroneousZserio(__file__, "property_names/choice_function_property_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "property_names/choice_invalid_property_name_private_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "property_names/choice_invalid_property_name_reserved_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "property_names/choice_public_method_property_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "property_names/sql_database_connection_property_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "property_names/sql_database_invalid_property_name_reserved_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "property_names/sql_database_public_method_property_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "property_names/structure_function_property_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "property_names/structure_indicator_property_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "property_names/structure_invalid_property_name_private_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "property_names/structure_invalid_property_name_reserved_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "property_names/structure_public_method_property_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "property_names/template_invalid_property_name_private_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "property_names/template_public_method_property_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "property_names/union_choice_tag_property_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "property_names/union_function_property_clash_error.zs", cls.errors)
        compileErroneousZserio(__file__, "property_names/union_invalid_property_name_private_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "property_names/union_invalid_property_name_reserved_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "property_names/union_public_method_property_clash_error.zs",
                               cls.errors)

    def testChoiceFunctionPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/choice_function_property_clash_error.zs",
            [
                "3:8: Property name error detected in 'TestChoice'!",
                PropertyNamesErrorTest._getPropertyClashError("func_array")
            ]
        )

    def testChoiceInvalidPropertyNamePrivate(self):
        assertErrorsPresent(self,
            "property_names/choice_invalid_property_name_private_error.zs",
            [
                "3:8: Property name error detected in 'TestChoice'!",
                PropertyNamesErrorTest._getPropertyRuleError("_choice")
            ]
        )

    def testChoiceInvalidPropertyNameReserved(self):
        assertErrorsPresent(self,
            "property_names/choice_invalid_property_name_reserved_error.zs",
            [
                "3:8: Property name error detected in 'TestChoice'!",
                PropertyNamesErrorTest._getPropertyRuleError("__str__")
            ]
        )

    def testChoicePublicMethodPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/choice_public_method_property_clash_error.zs",
            [
                "3:8: Property name error detected in 'TestChoice'!",
                PropertyNamesErrorTest._getPropertyClashError("write")
            ]
        )

    def testSqlDatabaseConnectionPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/sql_database_connection_property_clash_error.zs",
            [
                "9:14: Property name error detected in 'TestDatabase'!",
                PropertyNamesErrorTest._getPropertyClashError("connection")
            ]
        )

    def testSqlDatabaseInvalidPropertyNameReserved(self):
        assertErrorsPresent(self,
            "property_names/sql_database_invalid_property_name_reserved_error.zs",
            [
                "9:14: Property name error detected in 'TestDatabase'!",
                PropertyNamesErrorTest._getPropertyRuleError("__init__")
            ]
        )

    def testSqlDatabasePublicMethodPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/sql_database_public_method_property_clash_error.zs",
            [
                "9:14: Property name error detected in 'TestDatabase'!",
                PropertyNamesErrorTest._getPropertyClashError("from_file")
            ]
        )

    def testStructureFunctionPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/structure_function_property_clash_error.zs",
            [
                "3:8: Property name error detected in 'TestStructure'!",
                PropertyNamesErrorTest._getPropertyClashError("func_test")
            ]
        )

    def testStructureIndicatorPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/structure_indicator_property_clash_error.zs",
            [
                "3:8: Property name error detected in 'TestStructure'!",
                PropertyNamesErrorTest._getPropertyClashError("is_field_used")
            ]
        )

    def testStructureInvalidPropertyNamePrivate(self):
        assertErrorsPresent(self,
            "property_names/structure_invalid_property_name_private_error.zs",
            [
                "3:8: Property name error detected in 'TestStructure'!",
                PropertyNamesErrorTest._getPropertyRuleError("_field")
            ]
        )

    def testStructureInvalidPropertyNameReserved(self):
        assertErrorsPresent(self,
            "property_names/structure_invalid_property_name_reserved_error.zs",
            [
                "3:8: Property name error detected in 'TestStructure'!",
                PropertyNamesErrorTest._getPropertyRuleError("__eq__")
            ]
        )

    def testStructurePublicMethodPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/structure_public_method_property_clash_error.zs",
            [
                "3:8: Property name error detected in 'TestStructure'!",
                PropertyNamesErrorTest._getPropertyClashError("read")
            ]
        )

    def testTemplateInvalidPropertyNamePrivate(self):
        assertErrorsPresent(self,
            "property_names/template_invalid_property_name_private_error.zs",
            [
                "3:8: Property name error detected in 'StringStruct'!",
                PropertyNamesErrorTest._getPropertyRuleError("_field")
            ]
        )

    def testTemplatePublicMethodPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/template_public_method_property_clash_error.zs",
            [
                "3:8: Property name error detected in 'TemplatedChoice_string'!",
                PropertyNamesErrorTest._getPropertyClashError("write")
            ]
        )

    def testUnionChoiceTagPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/union_choice_tag_property_clash_error.zs",
            [
                "3:7: Property name error detected in 'TestUnion'!",
                PropertyNamesErrorTest._getPropertyClashError("choice_tag")
            ]
        )

    def testUnionFunctionPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/union_function_property_clash_error.zs",
            [
                "3:7: Property name error detected in 'TestUnion'!",
                PropertyNamesErrorTest._getPropertyClashError("func_my_func")
            ]
        )

    def testUnionInvalidPropertyNamePrivate(self):
        assertErrorsPresent(self,
            "property_names/union_invalid_property_name_private_error.zs",
            [
                "3:7: Property name error detected in 'TestUnion'!",
                PropertyNamesErrorTest._getPropertyRuleError("_choice")
            ]
        )

    def testUnionInvalidPropertyNameReserved(self):
        assertErrorsPresent(self,
            "property_names/union_invalid_property_name_reserved_error.zs",
            [
                "3:7: Property name error detected in 'TestUnion'!",
                PropertyNamesErrorTest._getPropertyRuleError("__hash__")
            ]
        )

    def testUnionPublicMethodPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/union_public_method_property_clash_error.zs",
            [
                "3:7: Property name error detected in 'TestUnion'!",
                PropertyNamesErrorTest._getPropertyClashError("bitsizeof")
            ]
        )

    @staticmethod
    def _getPropertyClashError(propertyName):
        return ("[ERROR] Python Generator: Invalid property name '" + propertyName + "'! " +
                "Property name clashes with generated API!")

    @staticmethod
    def _getPropertyRuleError(propertyName):
        return ("[ERROR] Python Generator: Invalid property name '" + propertyName + "'! " +
                "Property names cannot start with '_'!")
