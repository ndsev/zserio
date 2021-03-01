import unittest

from zserio.bitbuffer import BitBuffer
from zserio.bitreader import BitStreamReader
from zserio.bitsizeof import INT64_MIN
from zserio.exception import PythonRuntimeException

class BitStreamReaderTest(unittest.TestCase):

    def test_constructor(self):
        reader = BitStreamReader(bytes([0xAE, 0xEA, 0x80]), 17)
        self.assertEqual(0xAE, reader.read_bits(8))
        self.assertEqual(0xEA, reader.read_bits(8))
        self.assertEqual(0x01, reader.read_bits(1))
        with self.assertRaises(PythonRuntimeException):
            reader.read_bits(1) # no more bits available

    def test_constructor_wrong_bitsize(self):
        with self.assertRaises(PythonRuntimeException):
            BitStreamReader(bytes([0xAE]), 9)

    def test_from_bitbuffer(self):
        bitbuffer = BitBuffer(bytes([0xAE, 0xEA, 0x80]), 17)
        reader = BitStreamReader.from_bitbuffer(bitbuffer)
        self.assertEqual(bitbuffer.bitsize, reader.buffer_bitsize)
        self.assertEqual(0xAEE, reader.read_bits(12))
        self.assertEqual(0x0A, reader.read_bits(4))
        self.assertEqual(0x01, reader.read_bits(1))
        with self.assertRaises(PythonRuntimeException):
            reader.read_bits(1)

    def test_from_bitbuffer_overflow(self):
        bitbuffer = BitBuffer(bytes([0xFF, 0xFF, 0xF0]), 19)
        reader = BitStreamReader.from_bitbuffer(bitbuffer)
        self.assertEqual(bitbuffer.bitsize, reader.buffer_bitsize)
        with self.assertRaises(PythonRuntimeException):
            reader.read_bits(20)

    def test_read_unaligned_data(self):
        # number expected to read at offset
        test_value = 123

        for offset in range(65):
            buffer = bytearray((8 + offset + 7) // 8)

            # write test value at offset to data buffer
            buffer[offset // 8] = test_value >> (offset % 8)
            if offset % 8 != 0: # don't write behind the buffer
                buffer[offset // 8 + 1] = 0xff & test_value << (8 - offset % 8)

            bitbuffer = BitBuffer(buffer, 8 + offset)
            reader = BitStreamReader.from_bitbuffer(bitbuffer)

            # read offset bits
            self.assertEqual(0, reader.read_bits(offset))

            # read magic number
            self.assertEqual(test_value, reader.read_bits(8), msg=("Offset: " + str(offset)))

            # check eof
            with self.assertRaises(PythonRuntimeException):
                reader.read_bits(1)

    def test_read_bits(self):
        data = [0, 1, 255, 128, 127]
        reader = BitStreamReader(bytes(data))
        for byte in data:
            self.assertEqual(byte, reader.read_bits(8))

        with self.assertRaises(PythonRuntimeException):
            reader.read_bits(-1)

        self.assertEqual(0, reader.read_bits(0)) # read 0 bits

        with self.assertRaises(PythonRuntimeException):
            reader.read_bits(1) # no more bits available

    def test_read_signed_bits(self):
        data = [0, 0xff, 1, 127, 0x80]
        reader = BitStreamReader(bytes(data))
        self.assertEqual(0, reader.read_signed_bits(8))
        self.assertEqual(-1, reader.read_signed_bits(8)) # 0xff == -1
        self.assertEqual(1, reader.read_signed_bits(8))
        self.assertEqual(127, reader.read_signed_bits(8))
        self.assertEqual(-128, reader.read_signed_bits(8)) # 0x80 == -128

        self.assertEqual(0, reader.read_signed_bits(0)) # read 0 bits

        with self.assertRaises(PythonRuntimeException):
            reader.read_signed_bits(1) # no more bits available

        with self.assertRaises(PythonRuntimeException):
            reader.read_signed_bits(-1)

    def test_read_varint16(self):
        reader = BitStreamReader(bytes(1))
        self.assertEqual(0, reader.read_varint16())
        self.assertEqual(8, reader.bitposition)
        with self.assertRaises(PythonRuntimeException):
            reader.read_varint16()

    def test_read_varint32(self):
        reader = BitStreamReader(bytes(1))
        self.assertEqual(0, reader.read_varint32())
        self.assertEqual(8, reader.bitposition)
        with self.assertRaises(PythonRuntimeException):
            reader.read_varint32()

    def test_read_varint64(self):
        reader = BitStreamReader(bytes(1))
        self.assertEqual(0, reader.read_varint64())
        self.assertEqual(8, reader.bitposition)
        with self.assertRaises(PythonRuntimeException):
            reader.read_varint64()

    def test_read_varint(self):
        reader = BitStreamReader(b'\x00\x80')
        self.assertEqual(0, reader.read_varint())
        self.assertEqual(8, reader.bitposition)
        self.assertEqual(INT64_MIN, reader.read_varint())
        self.assertEqual(16, reader.bitposition)
        with self.assertRaises(PythonRuntimeException):
            reader.read_varint()

    def test_read_varuint16(self):
        reader = BitStreamReader(bytes(1))
        self.assertEqual(0, reader.read_varuint16())
        self.assertEqual(8, reader.bitposition)
        with self.assertRaises(PythonRuntimeException):
            reader.read_varuint16()

    def test_read_varuint32(self):
        reader = BitStreamReader(bytes(1))
        self.assertEqual(0, reader.read_varuint32())
        self.assertEqual(8, reader.bitposition)
        with self.assertRaises(PythonRuntimeException):
            reader.read_varuint32()

    def test_read_varuint64(self):
        reader = BitStreamReader(bytes(1))
        self.assertEqual(0, reader.read_varuint64())
        self.assertEqual(8, reader.bitposition)
        with self.assertRaises(PythonRuntimeException):
            reader.read_varuint64()

    def test_read_varuint(self):
        reader = BitStreamReader(bytes(1))
        self.assertEqual(0, reader.read_varuint())
        self.assertEqual(8, reader.bitposition)
        with self.assertRaises(PythonRuntimeException):
            reader.read_varuint()

    def test_read_varsize(self):
        # overflow, 2^32 - 1 is too much (b'\x83\xFF\xFF\xFF\xFF') is the maximum)
        reader = BitStreamReader(b'\x87\xFF\xFF\xFF\xFF')
        with self.assertRaises(PythonRuntimeException):
            reader.read_varsize()

        # overflow, 2^36 - 1 is too much (b'\x83\xFF\xFF\xFF\xFF') is the maximum)
        reader = BitStreamReader(b'\xFF\xFF\xFF\xFF\xFF')
        with self.assertRaises(PythonRuntimeException):
            reader.read_varsize()

    def test_read_float16(self):
        reader = BitStreamReader(bytes(2))
        self.assertEqual(0.0, reader.read_float16())
        self.assertEqual(16, reader.bitposition)
        with self.assertRaises(PythonRuntimeException):
            reader.read_float16()

    def test_read_float32(self):
        reader = BitStreamReader(bytes(4))
        self.assertEqual(0.0, reader.read_float32())
        self.assertEqual(32, reader.bitposition)
        with self.assertRaises(PythonRuntimeException):
            reader.read_float32()

    def test_read_float64(self):
        reader = BitStreamReader(bytes(8))
        self.assertEqual(0.0, reader.read_float64())
        self.assertEqual(64, reader.bitposition)
        with self.assertRaises(PythonRuntimeException):
            reader.read_float64()

    def test_read_string(self):
        reader = BitStreamReader(bytes(b'\x01\x41'))
        self.assertEqual("A", reader.read_string())
        self.assertEqual(16, reader.bitposition)
        with self.assertRaises(PythonRuntimeException):
            reader.read_string()

    def test_read_bool(self):
        reader = BitStreamReader(bytes(b'\xA8'))
        self.assertEqual(True, reader.read_bool())
        self.assertEqual(False, reader.read_bool())
        self.assertEqual(True, reader.read_bool())
        self.assertEqual(False, reader.read_bool())
        self.assertEqual(True, reader.read_bool())
        self.assertEqual(False, reader.read_bool())
        self.assertEqual(False, reader.read_bool())
        self.assertEqual(False, reader.read_bool())
        with self.assertRaises(PythonRuntimeException):
            reader.read_bool()

    def test_read_bitbuffer(self):
        reader = BitStreamReader(bytes(b'\x0B\xAB\xE1\xE0\x1F\xC0'))
        self.assertEqual(BitBuffer(bytes([0xAB, 0xE0]), 11), reader.read_bitbuffer())
        self.assertEqual(BitBuffer(bytes([0x00, 0xFE]), 15), reader.read_bitbuffer())
        with self.assertRaises(PythonRuntimeException):
            reader.read_bitbuffer()

    def test_bitposition(self):
        reader = BitStreamReader(bytes(1), 7)
        reader.bitposition = 0
        self.assertEqual(0, reader.bitposition)
        reader.bitposition = 7
        self.assertEqual(7, reader.bitposition)

        with self.assertRaises(PythonRuntimeException):
            reader.bitposition = 8
        with self.assertRaises(PythonRuntimeException):
            reader.bitposition = -1

        reader.bitposition = 0
        self.assertEqual(0, reader.bitposition)

    def test_alignto(self):
        reader = BitStreamReader(bytes(1))
        reader.alignto(1)
        self.assertEqual(0, reader.bitposition)
        reader.read_bits(1)
        self.assertEqual(1, reader.bitposition)
        reader.alignto(1)
        self.assertEqual(1, reader.bitposition)
        reader.alignto(4)
        self.assertEqual(4, reader.bitposition)
