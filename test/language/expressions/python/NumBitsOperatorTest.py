import unittest

from testutils import getZserioApi

class NumBitsOperatorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").numbits_operator

    def testGetNumBits8(self):
        numBitsFunctions = self.api.NumBitsFunctions()
        for value8 in range(1, 256):
            numBitsFunctions.setValue8(value8)
            self.assertEqual(NumBitsOperatorTest._calcNumBits(value8), numBitsFunctions.funcGetNumBits8())

    def testGetNumBits16(self):
        numBitsFunctions = self.api.NumBitsFunctions()
        for value16 in range(1, 65536):
            numBitsFunctions.setValue16(value16)
            self.assertEqual(NumBitsOperatorTest._calcNumBits(value16), numBitsFunctions.funcGetNumBits16())

    def testGetNumBits32(self):
        numBitsFunctions = self.api.NumBitsFunctions()
        for power in range(1, 33):
            # value32 = 2**power - 1
            value32 = 2 ** power - 1
            numBitsFunctions.setValue32(value32)
            self.assertEqual(NumBitsOperatorTest._calcNumBits(value32), numBitsFunctions.funcGetNumBits32())

    def testGetNumBits64(self):
        numBitsFunctions = self.api.NumBitsFunctions()
        for power in range(1, 49):
            # value64 = 2**power - 1
            value64 = 2 ** power - 1
            numBitsFunctions.setValue64(value64)
            self.assertEqual(NumBitsOperatorTest._calcNumBits(value64), numBitsFunctions.funcGetNumBits64())

    @staticmethod
    def _calcNumBits(value):
        if value <= 0:
            return 0
        if value == 1:
            return 1

        return (value - 1).bit_length()
