import Expressions

class ParameterTypeTest(Expressions.TestCase):
    def testBitSizeOfWithOptional(self):
        parameterTypeExpression = self.api.ParameterTypeExpression(self.api.Color.RED,
                                                                   self.VALUE, True)
        self.assertEqual(self.PARAMETER_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL,
                         parameterTypeExpression.bitsizeof())

    def testBitSizeOfWithoutOptional(self):
        parameterTypeExpression = self.api.ParameterTypeExpression(self.api.Color.BLUE)
        parameterTypeExpression.value = self.VALUE
        self.assertEqual(self.PARAMETER_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL,
                         parameterTypeExpression.bitsizeof())

    VALUE = 0x3F

    PARAMETER_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 8
    PARAMETER_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 7
