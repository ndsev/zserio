import unittest

from testutils import getZserioApi

class ParenthesisTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").parenthesis

    def testResult(self):
        parenthesisExpression = self.api.ParenthesisExpression(self.FIRST_VALUE, self.SECOND_VALUE)

        self.assertEqual(self.FIRST_VALUE * (self.SECOND_VALUE + 1), parenthesisExpression.funcResult())

    FIRST_VALUE = 0x11
    SECOND_VALUE = 0x22
