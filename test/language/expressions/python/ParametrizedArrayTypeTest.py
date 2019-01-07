import unittest

from testutils import getZserioApi

class ParametrizedArrayTypeTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").parametrized_array_type

    def testBitSizeOfWithOptional(self):
        array = [self.api.ParametrizedArrayElement.fromFields(False, 0, None),
                 self.api.ParametrizedArrayElement.fromFields(False, 0, None)]
        parametrizedArrayHolder = self.api.ParametrizedArrayHolder.fromFields(False, array)
        parametrizedArrayTypeExpression = (self.api.ParametrizedArrayTypeExpression.
                                           fromFields(parametrizedArrayHolder, True))

        self.assertEqual(self.PARAMETRIZED_ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL,
                         parametrizedArrayTypeExpression.bitSizeOf())

    def testBitSizeOfWithoutOptional(self):
        array = [self.api.ParametrizedArrayElement.fromFields(False, 1, None),
                 self.api.ParametrizedArrayElement.fromFields(False, 1, None)]
        parametrizedArrayHolder = self.api.ParametrizedArrayHolder.fromFields(False, array)
        parametrizedArrayTypeExpression = (self.api.ParametrizedArrayTypeExpression.
                                           fromFields(parametrizedArrayHolder, False))

        self.assertEqual(self.PARAMETRIZED_ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL,
                         parametrizedArrayTypeExpression.bitSizeOf())

    PARAMETRIZED_ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITH_OPTIONAL = 33
    PARAMETRIZED_ARRAY_TYPE_EXPRESSION_BIT_SIZE_WITHOUT_OPTIONAL = 32
