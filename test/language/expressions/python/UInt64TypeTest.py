import unittest

from testutils import getZserioApi

class UInt64TypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").uint64_type

    def testBitSizeOfWithOptional(self):
        uint64TypeExpression = self.api.UInt64TypeExpression(self.UINT32_VALUE,
                                                             self.UINT64_VALUE_WITH_OPTIONAL,
                                                             self.BOOLEAN_VALUE,
                                                             self.ADDITIONAL_VALUE)
        self.assertEqual(self.UINT64_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL, uint64TypeExpression.bitsizeof())

    def testBitSizeOfWithoutOptional(self):
        uint64TypeExpression = self.api.UInt64TypeExpression(self.UINT32_VALUE,
                                                             self.UINT64_VALUE_WITHOUT_OPTIONAL,
                                                             self.BOOLEAN_VALUE,
                                                             None)
        self.assertEqual(self.UINT64_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL,
                         uint64TypeExpression.bitsizeof())

    UINT64_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 100
    UINT64_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 97

    UINT32_VALUE = 8
    UINT64_VALUE_WITH_OPTIONAL = 2
    UINT64_VALUE_WITHOUT_OPTIONAL = 1
    BOOLEAN_VALUE = True
    ADDITIONAL_VALUE = 0x03
