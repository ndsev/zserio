import unittest

from testutils import getZserioApi

class NegationOperatorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").negation_operator

    def testNegatedValue(self):
        negationOperatorExpression = self.api.NegationOperatorExpression.fromFields(True)
        self.assertEqual(False, negationOperatorExpression.funcNegatedValue())

        negationOperatorExpression.setValue(False)
        self.assertEqual(True, negationOperatorExpression.funcNegatedValue())
