import unittest

from testutils import compileErroneousZserio, assertErrorsPresent

class GeneratedSymbolsErrorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.errors = {}

        compileErroneousZserio(__file__, "generated_symbols/choice_choice_tag_property_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/choice_invalid_function_name_private_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/choice_invalid_property_name_private_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/choice_invalid_property_name_reserved_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/choice_public_method_property_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/service_invalid_method_name_private_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/service_invalid_method_name_reserved_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/sql_database_connection_property_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__,
                               "generated_symbols/sql_database_invalid_property_name_reserved_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/sql_database_public_method_property_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/structure_indicator_function_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/structure_indicator_property_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/structure_invalid_function_name_reserved_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/structure_invalid_property_name_private_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/structure_invalid_property_name_reserved_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/structure_public_method_property_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/template_invalid_property_name_private_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/template_public_method_property_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/union_choice_tag_property_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/union_invalid_property_name_private_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/union_invalid_property_name_reserved_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/union_public_method_function_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "generated_symbols/union_public_method_property_clash_error.zs",
                               cls.errors)

    def testChoiceChoiceTagPropertyClash(self):
        assertErrorsPresent(self,
            "generated_symbols/choice_choice_tag_property_clash_error.zs",
            [
                ":8:16: Property name 'choice_tag' generated for symbol 'choiceTag' " +
                "clashes with generated API method!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )

    def testChoiceInvalidFunctionNamePrivate(self):
        assertErrorsPresent(self,
            "generated_symbols/choice_invalid_function_name_private_error.zs",
            [
                ":10:21: Invalid function name '_choice' generated for symbol '_choice'. " +
                "Function names cannot start with '_'!",
                "[ERROR] Python Generator: Function name error detected!"
            ]
        )

    def testChoiceInvalidPropertyNamePrivate(self):
        assertErrorsPresent(self,
            "generated_symbols/choice_invalid_property_name_private_error.zs",
            [
                ":6:16: Invalid property name '_choice' generated for symbol '_choice'. " +
                "Property names cannot start with '_'!",
                "[ERROR] Python Generator: Property name error detected!"
            ]
        )

    def testChoiceInvalidPropertyNameReserved(self):
        assertErrorsPresent(self,
            "generated_symbols/choice_invalid_property_name_reserved_error.zs",
            [
                ":6:16: Invalid property name '__str__' generated for symbol '__str__'. " +
                "Property names cannot start with '_'!",
                "[ERROR] Python Generator: Property name error detected!"
            ]
        )

    def testChoicePublicMethodPropertyClash(self):
        assertErrorsPresent(self,
            "generated_symbols/choice_public_method_property_clash_error.zs",
            [
                ":6:14: Property name 'write' generated for symbol 'write' clashes with generated API method!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )

    def testServiceInvalidMethodNamePrivate(self):
        assertErrorsPresent(self,
            "generated_symbols/service_invalid_method_name_private_error.zs",
            [
                ":11:14: Invalid method name '_service' generated for symbol '_service'. " +
                "Method names cannot start with '_'!",
                "[ERROR] Python Generator: Method name error detected!"
            ]
        )

    def testServiceInvalidMethodNameReserved(self):
        assertErrorsPresent(self,
            "generated_symbols/service_invalid_method_name_reserved_error.zs",
            [
                ":11:14: Invalid method name '__eq__' generated for symbol '__eq__'. " +
                "Method names cannot start with '_'!",
                "[ERROR] Python Generator: Method name error detected!"
            ]
        )

    def testSqlDatabaseConnectionPropertyClash(self):
        assertErrorsPresent(self,
            "generated_symbols/sql_database_connection_property_clash_error.zs",
            [
                ":11:15: Property name 'connection' generated for symbol 'connection' " +
                "clashes with generated API method!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )

    def testSqlDatabaseInvalidPropertyNameReserved(self):
        assertErrorsPresent(self,
            "generated_symbols/sql_database_invalid_property_name_reserved_error.zs",
            [
                ":11:15: Invalid property name '__init__' generated for symbol '__init__'. " +
                "Property names cannot start with '_'!",
                "[ERROR] Python Generator: Property name error detected!"
            ]
        )

    def testSqlDatabasePublicMethodPropertyClash(self):
        assertErrorsPresent(self,
            "generated_symbols/sql_database_public_method_property_clash_error.zs",
            [
                ":11:15: Property name 'from_file' generated for symbol 'fromFile' " +
                "clashes with generated API method!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )

    def testStructureIndicatorFunctionClash(self):
        assertErrorsPresent(self,
            "generated_symbols/structure_indicator_function_clash_error.zs",
            [
                ":7:19: Function name 'is_field_used' generated for symbol 'isFieldUsed' " +
                "clashes with generated indicator for optional field 'field' defined at 5:21!",
                "[ERROR] Python Generator: Function name clash detected!"
            ]
        )

    def testStructureIndicatorPropertyClash(self):
        assertErrorsPresent(self,
            "generated_symbols/structure_indicator_property_clash_error.zs",
            [
                ":6:12: Property name 'is_field_used' generated for symbol 'isFieldUsed' " +
                "clashes with generated indicator for optional field 'field' defined at 5:21!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )

    def testStructureInvalidFunctionNameReserved(self):
        assertErrorsPresent(self,
            "generated_symbols/structure_invalid_function_name_reserved_error.zs",
            [
                ":7:21: Invalid function name '__eq__' generated for symbol '__eq__'. " +
                "Function names cannot start with '_'!",
                "[ERROR] Python Generator: Function name error detected!"
            ]
        )

    def testStructureInvalidPropertyNamePrivate(self):
        assertErrorsPresent(self,
            "generated_symbols/structure_invalid_property_name_private_error.zs",
            [
                ":6:12: Invalid property name '_field' generated for symbol '_field'. " +
                "Property names cannot start with '_'!",
                "[ERROR] Python Generator: Property name error detected!"
            ]
        )

    def testStructureInvalidPropertyNameReserved(self):
        assertErrorsPresent(self,
            "generated_symbols/structure_invalid_property_name_reserved_error.zs",
            [
                ":5:12: Invalid property name '__eq__' generated for symbol '__eq__'. " +
                "Property names cannot start with '_'!",
                "[ERROR] Python Generator: Property name error detected!"
            ]
        )

    def testStructurePublicMethodPropertyClash(self):
        assertErrorsPresent(self,
            "generated_symbols/structure_public_method_property_clash_error.zs",
            [
                ":5:12: Property name 'read' generated for symbol 'read' clashes with generated API method!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )

    def testTemplateInvalidPropertyNamePrivate(self):
        assertErrorsPresent(self,
            "generated_symbols/template_invalid_property_name_private_error.zs",
            [
                ":5:7: Invalid property name '_field' generated for symbol '_field'. " +
                "Property names cannot start with '_'!",
                "[ERROR] Python Generator: Property name error detected!"
            ]
        )

    def testTemplatePublicMethodPropertyClash(self):
        assertErrorsPresent(self,
            "generated_symbols/template_public_method_property_clash_error.zs",
            [
                ":3:33: Property name 'write' generated for symbol 'write' " +
                "clashes with generated API method!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )

    def testUnionChoiceTagPropertyClash(self):
        assertErrorsPresent(self,
            "generated_symbols/union_choice_tag_property_clash_error.zs",
            [
                ":6:12: Property name 'choice_tag' generated for symbol 'choiceTag' " +
                "clashes with generated API method!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )

    def testUnionInvalidPropertyNamePrivate(self):
        assertErrorsPresent(self,
            "generated_symbols/union_invalid_property_name_private_error.zs",
            [
                ":5:12: Invalid property name '_choice' generated for symbol '_choice'. " +
                "Property names cannot start with '_'!",
                "[ERROR] Python Generator: Property name error detected!"
            ]
        )

    def testUnionInvalidPropertyNameReserved(self):
        assertErrorsPresent(self,
            "generated_symbols/union_invalid_property_name_reserved_error.zs",
            [
                ":6:12: Invalid property name '__hash__' generated for symbol '__hash__'. " +
                "Property names cannot start with '_'!",
                "[ERROR] Python Generator: Property name error detected!"
            ]
        )

    def testUnionPublicMethodFunctionClash(self):
        assertErrorsPresent(self,
            "generated_symbols/union_public_method_function_clash_error.zs",
            [
                ":7:21: Function name 'bitsizeof' generated for symbol 'bitsizeof' " +
                "clashes with generated API method!",
                "[ERROR] Python Generator: Function name clash detected!"
            ]
        )

    def testUnionPublicMethodPropertyClash(self):
        assertErrorsPresent(self,
            "generated_symbols/union_public_method_property_clash_error.zs",
            [
                ":6:12: Property name 'bitsizeof' generated for symbol 'bitsizeof' " +
                "clashes with generated API method!",
                "[ERROR] Python Generator: Property name clash detected!"
            ]
        )
