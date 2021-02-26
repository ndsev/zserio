import unittest

from testutils import getZserioApi

class UsedBeforeTypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").used_before_type

    def testBitSizeOfWithOptional(self):
        usedBeforeTypeExpression = self.api.UsedBeforeTypeExpression(self.api.Color.RED, True)
        self.assertEqual(self.USED_BEFORE_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL,
                         usedBeforeTypeExpression.bitSizeOf())

    def testBitSizeOfWithoutOptional(self):
        usedBeforeTypeExpression = self.api.UsedBeforeTypeExpression()
        usedBeforeTypeExpression.color = self.api.Color.BLUE
        self.assertEqual(self.USED_BEFORE_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL,
                         usedBeforeTypeExpression.bitSizeOf())

    USED_BEFORE_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 8
    USED_BEFORE_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 7
