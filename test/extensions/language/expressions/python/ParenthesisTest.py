import Expressions


class ParenthesisTest(Expressions.TestCase):
    def testResult(self):
        parenthesisExpression = self.api.ParenthesisExpression(self.FIRST_VALUE, self.SECOND_VALUE)

        self.assertEqual(self.FIRST_VALUE * (self.SECOND_VALUE + 1), parenthesisExpression.result())

    FIRST_VALUE = 0x11
    SECOND_VALUE = 0x22
