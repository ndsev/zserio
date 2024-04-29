import unittest

from zserio.bitfield import (
    bitfield_lowerbound,
    bitfield_upperbound,
    signed_bitfield_lowerbound,
    signed_bitfield_upperbound,
)
from zserio.exception import PythonRuntimeException


class BitFieldTest(unittest.TestCase):

    def test_bitfield_lowerbound(self):
        with self.assertRaises(PythonRuntimeException):
            bitfield_lowerbound(0)
        with self.assertRaises(PythonRuntimeException):
            bitfield_lowerbound(65)

        self.assertEqual(0, bitfield_lowerbound(1))
        self.assertEqual(0, bitfield_lowerbound(2))
        self.assertEqual(0, bitfield_lowerbound(8))
        self.assertEqual(0, bitfield_lowerbound(16))
        self.assertEqual(0, bitfield_lowerbound(32))
        self.assertEqual(0, bitfield_lowerbound(64))

    def test_bitfield_upperbound(self):
        with self.assertRaises(PythonRuntimeException):
            bitfield_upperbound(0)
        with self.assertRaises(PythonRuntimeException):
            bitfield_upperbound(65)

        self.assertEqual(1, bitfield_upperbound(1))
        self.assertEqual(3, bitfield_upperbound(2))
        self.assertEqual(255, bitfield_upperbound(8))
        self.assertEqual(65535, bitfield_upperbound(16))
        self.assertEqual(4294967295, bitfield_upperbound(32))
        self.assertEqual(0xFFFFFFFFFFFFFFFF, bitfield_upperbound(64))

    def test_signed_bitfield_lowerbound(self):
        with self.assertRaises(PythonRuntimeException):
            signed_bitfield_lowerbound(0)
        with self.assertRaises(PythonRuntimeException):
            signed_bitfield_lowerbound(65)

        self.assertEqual(-1, signed_bitfield_lowerbound(1))
        self.assertEqual(-2, signed_bitfield_lowerbound(2))
        self.assertEqual(-128, signed_bitfield_lowerbound(8))
        self.assertEqual(-32768, signed_bitfield_lowerbound(16))
        self.assertEqual(-2147483648, signed_bitfield_lowerbound(32))
        self.assertEqual(-9223372036854775808, signed_bitfield_lowerbound(64))

    def test_signed_bitfield_upperbound(self):
        with self.assertRaises(PythonRuntimeException):
            signed_bitfield_upperbound(0)
        with self.assertRaises(PythonRuntimeException):
            signed_bitfield_upperbound(65)

        self.assertEqual(0, signed_bitfield_upperbound(1))
        self.assertEqual(1, signed_bitfield_upperbound(2))
        self.assertEqual(127, signed_bitfield_upperbound(8))
        self.assertEqual(32767, signed_bitfield_upperbound(16))
        self.assertEqual(2147483647, signed_bitfield_upperbound(32))
        self.assertEqual(9223372036854775807, signed_bitfield_upperbound(64))
