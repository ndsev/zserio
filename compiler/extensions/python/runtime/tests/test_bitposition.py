import unittest

from zserio.bitposition import (alignTo, bitsToBytes, bytesToBits, PythonRuntimeException)

class BitPositionTest(unittest.TestCase):

    def testAlignTo(self):
        bitPosition = 5
        self.assertEqual(5, alignTo(0, bitPosition))
        self.assertEqual(5, alignTo(1, bitPosition))
        self.assertEqual(6, alignTo(2, bitPosition))
        self.assertEqual(6, alignTo(3, bitPosition))
        self.assertEqual(8, alignTo(4, bitPosition))
        self.assertEqual(5, alignTo(5, bitPosition))
        self.assertEqual(6, alignTo(6, bitPosition))
        self.assertEqual(7, alignTo(7, bitPosition))
        self.assertEqual(8, alignTo(8, bitPosition))

    def testBitsToBytes(self):
        self.assertEqual(1, bitsToBytes(8))
        self.assertEqual(3, bitsToBytes(24))
        with self.assertRaises(PythonRuntimeException):
            bitsToBytes(4)
        with self.assertRaises(PythonRuntimeException):
            bitsToBytes(9)

    def testBytesToBits(self):
        self.assertEqual(0, bytesToBits(0))
        self.assertEqual(8, bytesToBits(1))
        self.assertEqual(16, bytesToBits(2))
