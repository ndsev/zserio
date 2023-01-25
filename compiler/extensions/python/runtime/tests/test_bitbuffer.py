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
            BitBuffer(bytes([1]), out_of_range_bitsize) # throws!

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
