import unittest

from testutils import getZserioApi

class NegationOperatorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").negation_operator

    def testNegatedValue(self):
        negationOperatorExpression = self.api.NegationOperatorExpression(True)
        self.assertEqual(False, negationOperatorExpression.negated_value())

        negationOperatorExpression.value = False
        self.assertEqual(True, negationOperatorExpression.negated_value())
