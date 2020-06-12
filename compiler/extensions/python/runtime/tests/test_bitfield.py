import unittest

from zserio.bitfield import (getBitFieldLowerBound, getBitFieldUpperBound, getSignedBitFieldLowerBound,
                             getSignedBitFieldUpperBound)
from zserio.exception import PythonRuntimeException

class BitFieldTest(unittest.TestCase):

    def testGetBitFieldLowerBound(self):
        with self.assertRaises(PythonRuntimeException):
            getBitFieldLowerBound(0)
        with self.assertRaises(PythonRuntimeException):
            getBitFieldLowerBound(65)

        self.assertEqual(0, getBitFieldLowerBound(1))
        self.assertEqual(0, getBitFieldLowerBound(2))
        self.assertEqual(0, getBitFieldLowerBound(8))
        self.assertEqual(0, getBitFieldLowerBound(16))
        self.assertEqual(0, getBitFieldLowerBound(32))
        self.assertEqual(0, getBitFieldLowerBound(64))

    def testGetBitFieldUpperBound(self):
        with self.assertRaises(PythonRuntimeException):
            getBitFieldUpperBound(0)
        with self.assertRaises(PythonRuntimeException):
            getBitFieldUpperBound(65)

        self.assertEqual(1, getBitFieldUpperBound(1))
        self.assertEqual(3, getBitFieldUpperBound(2))
        self.assertEqual(255, getBitFieldUpperBound(8))
        self.assertEqual(65535, getBitFieldUpperBound(16))
        self.assertEqual(4294967295, getBitFieldUpperBound(32))
        self.assertEqual(0xFFFFFFFFFFFFFFFF, getBitFieldUpperBound(64))

    def testGetSignedBitFieldLowerBound(self):
        with self.assertRaises(PythonRuntimeException):
            getSignedBitFieldLowerBound(0)
        with self.assertRaises(PythonRuntimeException):
            getSignedBitFieldLowerBound(65)

        self.assertEqual(-1, getSignedBitFieldLowerBound(1))
        self.assertEqual(-2, getSignedBitFieldLowerBound(2))
        self.assertEqual(-128, getSignedBitFieldLowerBound(8))
        self.assertEqual(-32768, getSignedBitFieldLowerBound(16))
        self.assertEqual(-2147483648, getSignedBitFieldLowerBound(32))
        self.assertEqual(-9223372036854775808, getSignedBitFieldLowerBound(64))

    def testGetSignedBitFieldUpperBound(self):
        with self.assertRaises(PythonRuntimeException):
            getSignedBitFieldUpperBound(0)
        with self.assertRaises(PythonRuntimeException):
            getSignedBitFieldUpperBound(65)

        self.assertEqual(0, getSignedBitFieldUpperBound(1))
        self.assertEqual(1, getSignedBitFieldUpperBound(2))
        self.assertEqual(127, getSignedBitFieldUpperBound(8))
        self.assertEqual(32767, getSignedBitFieldUpperBound(16))
        self.assertEqual(2147483647, getSignedBitFieldUpperBound(32))
        self.assertEqual(9223372036854775807, getSignedBitFieldUpperBound(64))
