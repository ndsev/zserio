import unittest

from testutils import compileErroneousZserio, assertErrorsPresent

class InnerClassesClashingErrorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.errors = {}

        compileErroneousZserio(__file__, "inner_classes_clashing/bitmask_values_class_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__,
                               "inner_classes_clashing/sql_table_i_parameter_provider_class_clash_error.zs",
                               cls.errors)
        compileErroneousZserio(__file__, "inner_classes_clashing/sql_table_rows_class_clash_error.zs",
                               cls.errors)

    def testBitmaskValuesClassClashError(self):
        assertErrorsPresent(self,
            "inner_classes_clashing/bitmask_values_class_clash_error.zs",
            [
                "bitmask_values_class_clash_error.zs:4:15: " +
                "Class name 'Values' generated for bitmask clashes with its inner class 'Values' " +
                "generated in Python code.",
                "[ERROR] Python Generator: Class name clash detected!"
            ]
        )

    def testSqlTableIParameterProviderClassClashError(self):
        assertErrorsPresent(self,
            "inner_classes_clashing/sql_table_i_parameter_provider_class_clash_error.zs",
            [
                "sql_table_i_parameter_provider_class_clash_error.zs:3:11: " +
                "Class name 'IParameterProvider' generated for SQL table clashes with its inner class " +
                "'IParameterProvider' generated in Python code.",
                "[ERROR] Python Generator: Class name clash detected!"
            ]
        )

    def testSqlTableRowsClassClashError(self):
        assertErrorsPresent(self,
            "inner_classes_clashing/sql_table_rows_class_clash_error.zs",
            [
                "sql_table_rows_class_clash_error.zs:3:11: " +
                "Class name 'Rows' generated for SQL table clashes with its inner class " +
                "'Rows' generated in Python code.",
                "[ERROR] Python Generator: Class name clash detected!"
            ]
        )
