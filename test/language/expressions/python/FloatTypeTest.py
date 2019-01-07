import unittest

from testutils import getZserioApi

class FloatTypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").float_type

    def testResult(self):
        floatTypeExpression = self.api.FloatTypeExpression.fromFields(self.FLOAT_VALUE)
        result = (self.FLOAT_VALUE * 2.0 + 1.0 / 0.5 > 1.0)
        self.assertEqual(result, floatTypeExpression.funcResult())

    FLOAT_VALUE = 15.5
