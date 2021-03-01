import unittest

from testutils import getZserioApi

class FullEnumerationTypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").full_enumeration_type

    def testBitSizeOfWithOptional(self):
        fullEnumerationTypeExpression = self.api.FullEnumerationTypeExpression(self.api.Color.RED, True)
        self.assertEqual(self.FULL_ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL,
                         fullEnumerationTypeExpression.bitsizeof())

    def testBitSizeOfWithoutOptional(self):
        fullEnumerationTypeExpression = self.api.FullEnumerationTypeExpression()
        fullEnumerationTypeExpression.color = self.api.Color.BLUE
        self.assertEqual(self.FULL_ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL,
                         fullEnumerationTypeExpression.bitsizeof())

    FULL_ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 9
    FULL_ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 8
