import unittest

from testutils import getZserioApi

class ArrayTypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").array_type

    def testBitSizeOfWithOptional(self):
        arrayTypeExpression = self.api.ArrayTypeExpression.fromFields([0, 0], True)
        self.assertEqual(self.ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL, arrayTypeExpression.bitSizeOf())

    def testBitSizeOfWithoutOptional(self):
        arrayTypeExpression = self.api.ArrayTypeExpression()
        arrayTypeExpression.setArray([1, 1])
        self.assertEqual(self.ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL, arrayTypeExpression.bitSizeOf())

    ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 17
    ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 16
