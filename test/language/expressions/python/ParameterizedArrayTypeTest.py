import unittest

from testutils import getZserioApi

class ParameterizedArrayTypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").parameterized_array_type

    def testBitSizeOfWithOptional(self):
        array = [self.api.ParameterizedArrayElement.fromFields(False, 0, None),
                 self.api.ParameterizedArrayElement.fromFields(False, 0, None)]
        parameterizedArrayHolder = self.api.ParameterizedArrayHolder.fromFields(False, array)
        parameterizedArrayTypeExpression = (self.api.ParameterizedArrayTypeExpression.
                                            fromFields(parameterizedArrayHolder, True))

        self.assertEqual(self.PARAMETERIZED_ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL,
                         parameterizedArrayTypeExpression.bitSizeOf())

    def testBitSizeOfWithoutOptional(self):
        array = [self.api.ParameterizedArrayElement.fromFields(False, 1, None),
                 self.api.ParameterizedArrayElement.fromFields(False, 1, None)]
        parameterizedArrayHolder = self.api.ParameterizedArrayHolder.fromFields(False, array)
        parameterizedArrayTypeExpression = (self.api.ParameterizedArrayTypeExpression.
                                            fromFields(parameterizedArrayHolder, False))

        self.assertEqual(self.PARAMETERIZED_ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL,
                         parameterizedArrayTypeExpression.bitSizeOf())

    PARAMETERIZED_ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 33
    PARAMETERIZED_ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 32
