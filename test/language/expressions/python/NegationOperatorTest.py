import Expressions

class NegationOperatorTest(Expressions.TestCase):
    def testNegatedValue(self):
        negationOperatorExpression = self.api.NegationOperatorExpression(True)
        self.assertEqual(False, negationOperatorExpression.negated_value())

        negationOperatorExpression.value = False
        self.assertEqual(True, negationOperatorExpression.negated_value())
