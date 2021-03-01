import unittest

from testutils import getZserioApi

class StringTypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").string_type

    def testAppend(self):
        stringTypeExpression = self.api.StringTypeExpression(self.VALUE)
        self.assertEqual(self.VALUE, stringTypeExpression.func_return_value())
        self.assertEqual("appendix", stringTypeExpression.func_appendix())
        self.assertEqual(self.api.STRING_CONSTANT + "_appendix", stringTypeExpression.func_append_to_const())

    VALUE = "value"
