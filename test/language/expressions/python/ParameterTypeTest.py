import unittest

from testutils import getZserioApi

class ParameterTypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").parameter_type

    def testBitSizeOfWithOptional(self):
        parameterTypeExpression = self.api.ParameterTypeExpression(self.api.Color.RED,
                                                                   self.VALUE, True)
        self.assertEqual(self.PARAMETER_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL,
                         parameterTypeExpression.bitSizeOf())

    def testBitSizeOfWithoutOptional(self):
        parameterTypeExpression = self.api.ParameterTypeExpression(self.api.Color.BLUE)
        parameterTypeExpression.setValue(self.VALUE)
        self.assertEqual(self.PARAMETER_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL,
                         parameterTypeExpression.bitSizeOf())

    VALUE = 0x7B

    PARAMETER_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 8
    PARAMETER_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 7
