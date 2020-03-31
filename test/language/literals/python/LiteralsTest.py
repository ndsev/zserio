import unittest

from testutils import getZserioApi

class BitfieldEnumTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "literals.zs")

    def testBoolean(self):
        self.assertEqual(True, self.api.BOOLEAN_TRUE)
        self.assertEqual(False, self.api.BOOLEAN_FALSE)

    def testDecimal(self):
        self.assertEqual(255, self.api.DECIMAL_POSITIVE)
        self.assertEqual(255, self.api.DECIMAL_POSITIVE_WITH_SIGN)
        self.assertEqual(-255, self.api.DECIMAL_NEGATIVE)
        self.assertEqual(0, self.api.DECIMAL_ZERO)

    def testHexadecimal(self):
        self.assertEqual(255, self.api.HEXADECIMAL_POSITIVE)
        self.assertEqual(255, self.api.HEXADECIMAL_POSITIVE_WITH_CAPITAL_X)
        self.assertEqual(255, self.api.HEXADECIMAL_POSITIVE_WITH_SIGN)
        self.assertEqual(-255, self.api.HEXADECIMAL_NEGATIVE)

    def testOctal(self):
        self.assertEqual(255, self.api.OCTAL_POSITIVE)
        self.assertEqual(255, self.api.OCTAL_POSITIVE_WITH_SIGN)
        self.assertEqual(-255, self.api.OCTAL_NEGATIVE)
        self.assertEqual(0, self.api.OCTAL_ZERO)

    def testBinary(self):
        self.assertEqual(255, self.api.BINARY_POSITIVE)
        self.assertEqual(255, self.api.BINARY_POSITIVE_WITH_CAPITAL_B)
        self.assertEqual(255, self.api.BINARY_POSITIVE_WITH_SIGN)
        self.assertEqual(-255, self.api.BINARY_NEGATIVE)

    def testFloat16Literal(self):
        self.assertEqual(15.2, self.api.FLOAT16)

    def testFloat32Literal(self):
        self.assertEqual(15.23, self.api.FLOAT32)

    def testFloat64Literal(self):
        self.assertEqual(15.234, self.api.FLOAT64)

    def testString(self):
        self.assertEqual("String with escaped values \u0031 \x32 \063 \n \t \f \r \\ \"", self.api.STRING)
