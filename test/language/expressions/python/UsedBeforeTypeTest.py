import Expressions


class UsedBeforeTypeTest(Expressions.TestCase):
    def testBitSizeOfWithOptional(self):
        usedBeforeTypeExpression = self.api.UsedBeforeTypeExpression(self.api.Color.RED, True)
        self.assertEqual(
            self.USED_BEFORE_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL, usedBeforeTypeExpression.bitsizeof()
        )

    def testBitSizeOfWithoutOptional(self):
        usedBeforeTypeExpression = self.api.UsedBeforeTypeExpression()
        usedBeforeTypeExpression.color = self.api.Color.BLUE
        self.assertEqual(
            self.USED_BEFORE_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL, usedBeforeTypeExpression.bitsizeof()
        )

    USED_BEFORE_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 8
    USED_BEFORE_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 7
