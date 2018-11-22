import unittest

from zserio.builtin import getNumBits

class BuiltinOperatorsTest(unittest.TestCase):

    def testNumBits(self):
        self.assertEqual(0, getNumBits(0))
        self.assertEqual(1, getNumBits(1))
        self.assertEqual(1, getNumBits(2))
        self.assertEqual(2, getNumBits(3))
        self.assertEqual(2, getNumBits(4))
        self.assertEqual(3, getNumBits(5))
        self.assertEqual(3, getNumBits(6))
        self.assertEqual(3, getNumBits(7))
        self.assertEqual(3, getNumBits(8))
        self.assertEqual(4, getNumBits(16))
        self.assertEqual(5, getNumBits(32))
        self.assertEqual(6, getNumBits(64))
        self.assertEqual(7, getNumBits(128))
        self.assertEqual(8, getNumBits(256))
        self.assertEqual(9, getNumBits(512))
        self.assertEqual(10, getNumBits(1024))
        self.assertEqual(11, getNumBits(2048))
        self.assertEqual(12, getNumBits(4096))
        self.assertEqual(13, getNumBits(8192))
        self.assertEqual(14, getNumBits(16384))
        self.assertEqual(15, getNumBits(32768))
        self.assertEqual(16, getNumBits(65536))
        self.assertEqual(24, getNumBits(1 << 24))
        self.assertEqual(25, getNumBits((1 << 24) + 1))
        self.assertEqual(32, getNumBits(1 << 32))
        self.assertEqual(33, getNumBits((1 << 32) + 1))
        self.assertEqual(63, getNumBits(1 << 63))
        self.assertEqual(64, getNumBits((1 << 63) + 1))
