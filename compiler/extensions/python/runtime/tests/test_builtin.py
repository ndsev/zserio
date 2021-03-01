import unittest

from zserio.builtin import numbits

class BuiltinOperatorsTest(unittest.TestCase):

    def test_numbits(self):
        self.assertEqual(0, numbits(0))
        self.assertEqual(1, numbits(1))
        self.assertEqual(1, numbits(2))
        self.assertEqual(2, numbits(3))
        self.assertEqual(2, numbits(4))
        self.assertEqual(3, numbits(5))
        self.assertEqual(3, numbits(6))
        self.assertEqual(3, numbits(7))
        self.assertEqual(3, numbits(8))
        self.assertEqual(4, numbits(16))
        self.assertEqual(5, numbits(32))
        self.assertEqual(6, numbits(64))
        self.assertEqual(7, numbits(128))
        self.assertEqual(8, numbits(256))
        self.assertEqual(9, numbits(512))
        self.assertEqual(10, numbits(1024))
        self.assertEqual(11, numbits(2048))
        self.assertEqual(12, numbits(4096))
        self.assertEqual(13, numbits(8192))
        self.assertEqual(14, numbits(16384))
        self.assertEqual(15, numbits(32768))
        self.assertEqual(16, numbits(65536))
        self.assertEqual(24, numbits(1 << 24))
        self.assertEqual(25, numbits((1 << 24) + 1))
        self.assertEqual(32, numbits(1 << 32))
        self.assertEqual(33, numbits((1 << 32) + 1))
        self.assertEqual(63, numbits(1 << 63))
        self.assertEqual(64, numbits((1 << 63) + 1))
