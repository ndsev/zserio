import Expressions

class FloatTypeTest(Expressions.TestCase):
    def testResult(self):
        floatTypeExpression = self.api.FloatTypeExpression(self.FLOAT_VALUE)
        result = self.FLOAT_VALUE * 2.0 + 1.0 / 0.5 > 1.0
        self.assertEqual(result, floatTypeExpression.result())

    FLOAT_VALUE = 15.5
