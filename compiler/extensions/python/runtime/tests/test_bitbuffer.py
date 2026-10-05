import unittest

from zserio.bitbuffer import BitBuffer
from zserio.exception import PythonRuntimeException


class BitStreamReaderTest(unittest.TestCase):

    def test_buffer_constructor(self):
        bytesize = 2
        bitbuffer = BitBuffer(bytes([1, 2]))
        self.assertEqual(8 * bytesize, bitbuffer.bitsize)

        empty_bitsize = 0
        empty_bitbuffer = BitBuffer(bytes([]))
        self.assertEqual(empty_bitsize, empty_bitbuffer.bitsize)

    def test_buffer_bitsize_constructor(self):
        bitsize = 11
        bitbuffer = BitBuffer(bytes([0x01, 0xE0]), bitsize)
        self.assertEqual(bitsize, bitbuffer.bitsize)

        empty_bitsize = 0
        empty_bitbuffer = BitBuffer(bytes([]), empty_bitsize)
        self.assertEqual(empty_bitsize, empty_bitbuffer.bitsize)

        out_of_range_bitsize = 9
        with self.assertRaises(PythonRuntimeException):
            BitBuffer(bytes([1]), out_of_range_bitsize)  # throws!

    def test_eq(self):
        bitsize = 11
        bitbuffer1 = BitBuffer(bytes([0xAB, 0xE0]), bitsize)
        bitbuffer2 = BitBuffer(bytes([0xAB, 0xF0]), bitsize)
        self.assertEqual(bitbuffer1, bitbuffer2)

        bitbuffer3 = BitBuffer(bytes([0xAB, 0xFF]), bitsize)
        self.assertEqual(bitbuffer1, bitbuffer3)

        bitbuffer4 = BitBuffer(bytes([0xAB, 0xC0]), bitsize)
        self.assertNotEqual(bitbuffer1, bitbuffer4)

        bitbuffer5 = BitBuffer(bytes([0xBA, 0xE0]), bitsize)
        self.assertNotEqual(bitbuffer1, bitbuffer5)

        bitbuffer6 = BitBuffer(bytes([0xAB]))
        self.assertNotEqual(bitbuffer1, bitbuffer6)

        bitbuffer7 = BitBuffer(bytes())
        self.assertNotEqual(bitbuffer1, bitbuffer7)

        self.assertNotEqual(bitbuffer1, 1)

    def test_lt(self):
        bitbuffer_empty1 = BitBuffer(bytes())
        bitbuffer_empty2 = BitBuffer(bytes())
        self.assertFalse(bitbuffer_empty1 < bitbuffer_empty2)
        self.assertFalse(bitbuffer_empty2 < bitbuffer_empty1)

        bitbuffer_byte1 = BitBuffer(bytes([0xAB]), 8)
        bitbuffer_byte2 = BitBuffer(bytes([0xAC]), 8)
        self.assertTrue(bitbuffer_byte1 < bitbuffer_byte2)
        self.assertFalse(bitbuffer_byte2 < bitbuffer_byte1)

        bitsize = 11
        bitbuffer1 = BitBuffer(bytes([0xAB, 0xE0]), bitsize)
        self.assertTrue(bitbuffer_empty1 < bitbuffer1)
        self.assertTrue(bitbuffer_byte1 < bitbuffer1)
        self.assertFalse(bitbuffer1 < bitbuffer_empty1)
        self.assertFalse(bitbuffer1 < bitbuffer_byte1)

        bitbuffer1_copy = BitBuffer(bytes([0xAB, 0xE0]), bitsize)
        self.assertFalse(bitbuffer1 < bitbuffer1_copy)
        self.assertFalse(bitbuffer1_copy < bitbuffer1)

        bitbuffer2 = BitBuffer(bytes([0xAB, 0xF0]), bitsize)
        self.assertFalse(bitbuffer1 < bitbuffer2)
        self.assertFalse(bitbuffer2 < bitbuffer1)

        bitbuffer3 = BitBuffer(bytes([0xAB, 0x00]), bitsize)
        self.assertTrue(bitbuffer3 < bitbuffer1)
        self.assertFalse(bitbuffer1 < bitbuffer3)

        bitbuffer4 = BitBuffer(bytes([0x00, 0x00]), bitsize)
        self.assertTrue(bitbuffer4 < bitbuffer1)
        self.assertFalse(bitbuffer1 < bitbuffer4)

        bitbuffer5 = BitBuffer(bytes([0xAB, 0xE0, 0x00]), 20)
        self.assertTrue(bitbuffer1 < bitbuffer5)
        self.assertFalse(bitbuffer5 < bitbuffer1)

        bitbuffer6 = BitBuffer(bytes([0xA0]), 3)
        bitbuffer7 = BitBuffer(bytes([0xA0]), 4)
        self.assertNotEqual(bitbuffer6, bitbuffer7)
        self.assertTrue(bitbuffer6 < bitbuffer7)
        self.assertFalse(bitbuffer7 < bitbuffer6)

        self.assertEqual(
            [bitbuffer_empty1, bitbuffer_byte1, bitbuffer1, bitbuffer5],
            sorted([bitbuffer5, bitbuffer1, bitbuffer_empty1, bitbuffer_byte1]),
        )

        with self.assertRaises(TypeError):
            _ = bitbuffer1 < 1

    def test_hashcode(self):
        bitsize = 11
        bitbuffer1 = BitBuffer(bytes([0xAB, 0xE0]), bitsize)
        bitbuffer2 = BitBuffer(bytes([0xAB, 0xF0]), bitsize)
        self.assertEqual(hash(bitbuffer1), hash(bitbuffer2))

        bitbuffer3 = BitBuffer(bytes([0xAB, 0xFF]), bitsize)
        self.assertEqual(hash(bitbuffer1), hash(bitbuffer3))

        bitbuffer4 = BitBuffer(bytes([0xAB, 0xC0]), bitsize)
        self.assertNotEqual(hash(bitbuffer1), hash(bitbuffer4))

        bitbuffer5 = BitBuffer(bytes([0xBA, 0xE0]), bitsize)
        self.assertNotEqual(hash(bitbuffer1), hash(bitbuffer5))

        bitbuffer6 = BitBuffer(bytes([0xAB]))
        self.assertNotEqual(hash(bitbuffer1), hash(bitbuffer6))

        bitbuffer7 = BitBuffer(bytes())
        self.assertNotEqual(hash(bitbuffer1), hash(bitbuffer7))

    def test_buffer(self):
        bitsize = 11
        buffer = bytes([0xAB, 0xE0])
        bitbuffer = BitBuffer(buffer, bitsize)
        self.assertEqual(buffer, bitbuffer.buffer)

    def test_bitsize(self):
        bitsize = 11
        bitbuffer = BitBuffer(bytes([0xAB, 0xE0]), bitsize)
        self.assertEqual(bitsize, bitbuffer.bitsize)
