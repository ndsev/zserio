import Expressions

class ConstTypeTest(Expressions.TestCase):
    def testBitSizeOfWithOptional(self):
        constTypeExpression = self.api.ConstTypeExpression(self.VALID_VALUE, self.ADDITIONAL_VALUE)
        self.assertEqual(self.CONST_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL, constTypeExpression.bitsizeof())

    def testBitSizeOfWithoutOptional(self):
        constTypeExpression = self.api.ConstTypeExpression()
        constTypeExpression.value = self.INVALID_VALUE
        self.assertEqual(self.CONST_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL, constTypeExpression.bitsizeof())

    CONST_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 10
    CONST_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 7

    VALID_VALUE = 0x01
    INVALID_VALUE = 0x00
    ADDITIONAL_VALUE = 0x03
