import unittest

from zserio.bitbuffer import BitBuffer
from zserio.compare import compare


class CompareTest(unittest.TestCase):

    def test_none(self):
        self.assertEqual(0, compare(None, None))
        self.assertTrue(compare(None, 0) < 0)
        self.assertTrue(compare(0, None) > 0)

    def test_int(self):
        self.assertEqual(0, compare(1, 1))
        self.assertTrue(compare(1, 2) < 0)
        self.assertTrue(compare(2, 1) > 0)
        self.assertTrue(compare(False, True) < 0)

    def test_float(self):
        self.assertEqual(0, compare(1.5, 1.5))
        self.assertTrue(compare(-1.5, 1.5) < 0)
        self.assertTrue(compare(1.5, -1.5) > 0)

    def test_string(self):
        self.assertEqual(0, compare("a", "a"))
        self.assertTrue(compare("a", "b") < 0)
        self.assertTrue(compare("b", "a") > 0)
        self.assertTrue(compare("z", "ä") < 0)

    def test_bytes(self):
        self.assertEqual(0, compare(bytearray([0x01, 0xFF]), bytearray([0x01, 0xFF])))
        self.assertTrue(compare(bytearray([0x01]), bytearray([0xFF])) < 0)
        self.assertTrue(compare(bytearray([0x01, 0x00]), bytearray([0x01])) > 0)

    def test_bitbuffer(self):
        self.assertEqual(0, compare(BitBuffer(bytes([0xAB, 0xE0]), 11), BitBuffer(bytes([0xAB, 0xF0]), 11)))
        self.assertTrue(compare(BitBuffer(bytes([0xAB, 0x00]), 11), BitBuffer(bytes([0xAB, 0xE0]), 11)) < 0)
        self.assertTrue(compare(BitBuffer(bytes([0xAB, 0xE0]), 11), BitBuffer(bytes([0xAB, 0x00]), 11)) > 0)
