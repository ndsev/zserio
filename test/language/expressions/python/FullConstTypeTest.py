import unittest

from testutils import getZserioApi

class FullConstTypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").full_const_type

    def testBitSizeOfWithOptional(self):
        fullConstTypeExpression = self.api.FullConstTypeExpression(self.FULL_VALID_VALUE,
                                                                   self.FULL_ADDITIONAL_VALUE)
        self.assertEqual(self.FULL_CONST_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL,
                         fullConstTypeExpression.bitSizeOf())

    def testBitSizeOfWithoutOptional(self):
        fullConstTypeExpression = self.api.FullConstTypeExpression()
        fullConstTypeExpression.setValue(self.FULL_INVALID_VALUE)
        self.assertEqual(self.FULL_CONST_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL,
                         fullConstTypeExpression.bitSizeOf())

    FULL_CONST_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 10
    FULL_CONST_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 7

    FULL_VALID_VALUE = 0x01
    FULL_INVALID_VALUE = 0x00

    FULL_ADDITIONAL_VALUE = 0x03
