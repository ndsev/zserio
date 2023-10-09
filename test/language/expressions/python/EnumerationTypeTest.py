import Expressions

class EnumerationTypeTest(Expressions.TestCase):
    def testBitSizeOfWithOptional(self):
        enumerationTypeExpression = self.api.EnumerationTypeExpression(self.api.Color.RED, is_color_red_=True)
        self.assertEqual(self.ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL,
                         enumerationTypeExpression.bitsizeof())

    def testBitSizeOfWithoutOptional(self):
        enumerationTypeExpression = self.api.EnumerationTypeExpression()
        enumerationTypeExpression.color = self.api.Color.BLUE
        self.assertEqual(self.ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL,
                         enumerationTypeExpression.bitsizeof())

    ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 9
    ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 8
