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
                ":10:16: Property name 'func_array' generated for symbol 'funcArray' " +
                "clashes with generated method for function 'array' defined at 12:19!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )

    def testChoiceInvalidPropertyNamePrivate(self):
        assertErrorsPresent(self,
            "property_names/choice_invalid_property_name_private_error.zs",
            [
                ":6:16: Invalid property name '_choice' generated for symbol '_choice'. " +
                "Property names cannot start with '_'!",
                "[ERROR] Python Generator: Property name error detected!"
            ]
        )

    def testChoiceInvalidPropertyNameReserved(self):
        assertErrorsPresent(self,
            "property_names/choice_invalid_property_name_reserved_error.zs",
            [
                ":6:16: Invalid property name '__str__' generated for symbol '__str__'. " +
                "Property names cannot start with '_'!",
                "[ERROR] Python Generator: Property name error detected!"
            ]
        )

    def testChoicePublicMethodPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/choice_public_method_property_clash_error.zs",
            [
                ":6:14: Property name 'write' generated for symbol 'write' clashes with generated API method!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )

    def testSqlDatabaseConnectionPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/sql_database_connection_property_clash_error.zs",
            [
                ":11:15: Property name 'connection' generated for symbol 'connection' " +
                "clashes with generated API method!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )

    def testSqlDatabaseInvalidPropertyNameReserved(self):
        assertErrorsPresent(self,
            "property_names/sql_database_invalid_property_name_reserved_error.zs",
            [
                ":11:15: Invalid property name '__init__' generated for symbol '__init__'. " +
                "Property names cannot start with '_'!",
                "[ERROR] Python Generator: Property name error detected!"
            ]
        )

    def testSqlDatabasePublicMethodPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/sql_database_public_method_property_clash_error.zs",
            [
                ":11:15: Property name 'from_file' generated for symbol 'fromFile' " +
                "clashes with generated API method!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )

    def testStructureFunctionPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/structure_function_property_clash_error.zs",
            [
                ":5:12: Property name 'func_test' generated for symbol 'funcTest' " +
                "clashes with generated method for function 'test' defined at 7:19!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )

    def testStructureIndicatorPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/structure_indicator_property_clash_error.zs",
            [
                ":6:12: Property name 'is_field_used' generated for symbol 'isFieldUsed' " +
                "clashes with generated indicator for optional field 'field' defined at 5:21!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )

    def testStructureInvalidPropertyNamePrivate(self):
        assertErrorsPresent(self,
            "property_names/structure_invalid_property_name_private_error.zs",
            [
                ":6:12: Invalid property name '_field' generated for symbol '_field'. " +
                "Property names cannot start with '_'!",
                "[ERROR] Python Generator: Property name error detected!"
            ]
        )

    def testStructureInvalidPropertyNameReserved(self):
        assertErrorsPresent(self,
            "property_names/structure_invalid_property_name_reserved_error.zs",
            [
                ":5:12: Invalid property name '__eq__' generated for symbol '__eq__'. " +
                "Property names cannot start with '_'!",
                "[ERROR] Python Generator: Property name error detected!"
            ]
        )

    def testStructurePublicMethodPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/structure_public_method_property_clash_error.zs",
            [
                ":5:12: Property name 'read' generated for symbol 'read' clashes with generated API method!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )

    def testTemplateInvalidPropertyNamePrivate(self):
        assertErrorsPresent(self,
            "property_names/template_invalid_property_name_private_error.zs",
            [
                ":5:7: Invalid property name '_field' generated for symbol '_field'. " +
                "Property names cannot start with '_'!",
                "[ERROR] Python Generator: Property name error detected!"
            ]
        )

    def testTemplatePublicMethodPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/template_public_method_property_clash_error.zs",
            [
                ":3:33: Property name 'write' generated for symbol 'write' " +
                "clashes with generated API method!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )

    def testUnionChoiceTagPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/union_choice_tag_property_clash_error.zs",
            [
                ":6:12: Property name 'choice_tag' generated for symbol 'choiceTag' " +
                "clashes with generated API method!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )

    def testUnionFunctionPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/union_function_property_clash_error.zs",
            [
                ":5:12: Property name 'func_my_func' generated for symbol 'funcMyFunc' " +
                "clashes with generated method for function 'myFunc' defined at 7:21!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )

    def testUnionInvalidPropertyNamePrivate(self):
        assertErrorsPresent(self,
            "property_names/union_invalid_property_name_private_error.zs",
            [
                ":5:12: Invalid property name '_choice' generated for symbol '_choice'. " +
                "Property names cannot start with '_'!",
                "[ERROR] Python Generator: Property name error detected!"
            ]
        )

    def testUnionInvalidPropertyNameReserved(self):
        assertErrorsPresent(self,
            "property_names/union_invalid_property_name_reserved_error.zs",
            [
                ":6:12: Invalid property name '__hash__' generated for symbol '__hash__'. " +
                "Property names cannot start with '_'!",
                "[ERROR] Python Generator: Property name error detected!"
            ]
        )

    def testUnionPublicMethodPropertyClash(self):
        assertErrorsPresent(self,
            "property_names/union_public_method_property_clash_error.zs",
            [
                ":6:12: Property name 'bitsizeof' generated for symbol 'bitsizeof' " +
                "clashes with generated API method!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )
