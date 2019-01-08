import unittest

from testutils import getZserioApi

class StringTypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").string_type

    def testAppend(self):
        stringTypeExpression = self.api.StringTypeExpression.fromFields(self.FIRST_VALUE, self.SECOND_VALUE)
        self.assertEqual(self.FIRST_VALUE + self.SECOND_VALUE + "_appendix", stringTypeExpression.funcAppend())

    FIRST_VALUE = "first"
    SECOND_VALUE = "second"
