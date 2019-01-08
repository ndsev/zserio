import unittest

from testutils import getZserioApi

class EnumerationTypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").enumeration_type

    def testBitSizeOfWithOptional(self):
        enumerationTypeExpression = self.api.EnumerationTypeExpression.fromFields(self.api.Color.RED, True)
        self.assertEqual(self.ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL,
                         enumerationTypeExpression.bitSizeOf())

    def testBitSizeOfWithoutOptional(self):
        enumerationTypeExpression = self.api.EnumerationTypeExpression()
        enumerationTypeExpression.setColor(self.api.Color.BLUE)
        self.assertEqual(self.ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL,
                         enumerationTypeExpression.bitSizeOf())

    ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 9
    ENUMERATION_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 8
