import Expressions


class FunctionTypeTest(Expressions.TestCase):
    def testBitSizeOfWithOptional(self):
        functionTypeExpression = self.api.FunctionTypeExpression(self.api.Color.RED, True)
        self.assertEqual(
            self.FUNCTION_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL, functionTypeExpression.bitsizeof()
        )

    def testBitSizeOfWithoutOptional(self):
        functionTypeExpression = self.api.FunctionTypeExpression()
        functionTypeExpression.color = self.api.Color.BLUE
        self.assertEqual(
            self.FUNCTION_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL, functionTypeExpression.bitsizeof()
        )

    FUNCTION_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 9
    FUNCTION_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 8
