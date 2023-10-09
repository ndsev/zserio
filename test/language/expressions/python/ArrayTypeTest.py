import Expressions

class ArrayTypeTest(Expressions.TestCase):
    def testBitSizeOfWithOptional(self):
        arrayTypeExpression = self.api.ArrayTypeExpression([0, 0], True)
        self.assertEqual(self.ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL, arrayTypeExpression.bitsizeof())

    def testBitSizeOfWithoutOptional(self):
        arrayTypeExpression = self.api.ArrayTypeExpression()
        arrayTypeExpression.array = [1, 1]
        self.assertEqual(self.ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL, arrayTypeExpression.bitsizeof())

    ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 17
    ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 16
