import unittest

from zserio.bitbuffer import BitBuffer
from zserio.bitwriter import BitStreamWriter
from zserio.exception import PythonRuntimeException
from zserio.limits import (
    VARINT16_MIN,
    VARINT16_MAX,
    VARINT32_MIN,
    VARINT32_MAX,
    VARINT64_MIN,
    VARINT64_MAX,
    VARINT_MIN,
    VARINT_MAX,
    VARUINT16_MIN,
    VARUINT16_MAX,
    VARUINT32_MIN,
    VARUINT32_MAX,
    VARUINT64_MIN,
    VARUINT64_MAX,
    VARUINT_MIN,
    VARUINT_MAX,
    VARSIZE_MIN,
    VARSIZE_MAX,
)


class BitStreamWriterTest(unittest.TestCase):

    def test_write_unaligned_data(self):
        # number expected to be written at offset
        test_value = 123

        for offset in range(65):
            writer = BitStreamWriter()

            if offset != 0:
                writer.write_bits(0, offset)
            writer.write_bits(test_value, 8)

            # check written value
            buffer = writer.byte_array
            written_test_value = buffer[offset // 8] << (offset % 8)
            if offset % 8 != 0:
                written_test_value |= buffer[offset // 8 + 1] >> (8 - (offset % 8))
            self.assertEqual(test_value, written_test_value, msg="Offset: " + str(offset))

    def test_write_bits(self):
        writer = BitStreamWriter()
        writer.write_bits(0, 8)
        writer.write_bits(255, 8)
        writer.write_bits(1, 1)
        writer.write_bits(0x3F, 6)
        writer.write_bits(1, 1)
        self.assertEqual(b"\x00\xff\xff", writer.byte_array)
        self.assertEqual(3 * 8, writer.bitposition)
        writer.write_bits(0xFF, 8)
        self.assertEqual(b"\x00\xff\xff\xff", writer.byte_array)
        self.assertEqual(4 * 8, writer.bitposition)
        writer.write_bits(0, 4)
        self.assertEqual(b"\x00\xff\xff\xff\x00", writer.byte_array)
        self.assertEqual(4 * 8 + 4, writer.bitposition)
        writer.write_bits(0x0F, 4)
        self.assertEqual(b"\x00\xff\xff\xff\x0f", writer.byte_array)
        self.assertEqual(5 * 8, writer.bitposition)
        writer.write_bits(0x80, 8)
        self.assertEqual(b"\x00\xff\xff\xff\x0f\x80", writer.byte_array)
        self.assertEqual(6 * 8, writer.bitposition)

        with self.assertRaises(PythonRuntimeException):
            writer.write_bits(1, 0)  # zero bits!

        with self.assertRaises(PythonRuntimeException):
            writer.write_bits(1, -1)  # negative number of bits!

        with self.assertRaises(PythonRuntimeException):
            writer.write_bits(256, 8)  # above the upper bound

        with self.assertRaises(PythonRuntimeException):
            writer.write_bits(-1, 8)  # below the lower bound

    def test_write_signed_bits(self):
        writer = BitStreamWriter()
        writer.write_signed_bits(0, 1)
        writer.write_signed_bits(-1, 2)
        writer.write_signed_bits(-1, 5)
        self.assertEqual(b"\x7f", writer.byte_array)
        self.assertEqual(8, writer.bitposition)
        writer.write_signed_bits(-1, 1)
        writer.write_signed_bits(-1, 7)
        self.assertEqual(b"\x7f\xff", writer.byte_array)
        self.assertEqual(16, writer.bitposition)

        with self.assertRaises(PythonRuntimeException):
            writer.write_signed_bits(1, 0)  # zero bits!

        with self.assertRaises(PythonRuntimeException):
            writer.write_signed_bits(1, 1)  # above the upper bound

        with self.assertRaises(PythonRuntimeException):
            writer.write_signed_bits(128, 8)  # above the upper bound

        with self.assertRaises(PythonRuntimeException):
            writer.write_signed_bits(-129, 8)  # below the lower bound

    def test_write_varint16(self):
        writer = BitStreamWriter()
        writer.write_varint16(0)
        self.assertEqual(8, writer.bitposition)
        self.assertEqual(b"\x00", writer.byte_array)
        with self.assertRaises(PythonRuntimeException):
            writer.write_varint16(VARINT16_MIN - 1)
        with self.assertRaises(PythonRuntimeException):
            writer.write_varint16(VARINT16_MAX + 1)

    def test_write_varint32(self):
        writer = BitStreamWriter()
        writer.write_varint32(0)
        self.assertEqual(8, writer.bitposition)
        self.assertEqual(b"\x00", writer.byte_array)
        with self.assertRaises(PythonRuntimeException):
            writer.write_varint32(VARINT32_MIN - 1)
        with self.assertRaises(PythonRuntimeException):
            writer.write_varint32(VARINT32_MAX + 1)

    def test_write_varint64(self):
        writer = BitStreamWriter()
        writer.write_varint64(0)
        self.assertEqual(8, writer.bitposition)
        self.assertEqual(b"\x00", writer.byte_array)
        with self.assertRaises(PythonRuntimeException):
            writer.write_varint64(VARINT64_MIN - 1)
        with self.assertRaises(PythonRuntimeException):
            writer.write_varint64(VARINT64_MAX + 1)

    def test_write_varint(self):
        writer = BitStreamWriter()
        writer.write_varint(0)
        self.assertEqual(b"\x00", writer.byte_array)
        self.assertEqual(8, writer.bitposition)
        writer.write_varint(VARINT_MIN)
        self.assertEqual(16, writer.bitposition)
        self.assertEqual(b"\x00\x80", writer.byte_array)  # INT64_MIN is encoded as -0
        with self.assertRaises(PythonRuntimeException):
            writer.write_varint(VARINT_MIN - 1)
        with self.assertRaises(PythonRuntimeException):
            writer.write_varint(VARINT_MAX + 1)

    def test_write_varuint16(self):
        writer = BitStreamWriter()
        writer.write_varuint16(0)
        self.assertEqual(8, writer.bitposition)
        self.assertEqual(b"\x00", writer.byte_array)
        with self.assertRaises(PythonRuntimeException):
            writer.write_varuint16(VARUINT16_MIN - 1)
        with self.assertRaises(PythonRuntimeException):
            writer.write_varuint16(VARUINT16_MAX + 1)

    def test_write_varuint32(self):
        writer = BitStreamWriter()
        writer.write_varuint32(0)
        self.assertEqual(8, writer.bitposition)
        self.assertEqual(b"\x00", writer.byte_array)
        with self.assertRaises(PythonRuntimeException):
            writer.write_varuint32(VARUINT32_MIN - 1)
        with self.assertRaises(PythonRuntimeException):
            writer.write_varuint32(VARUINT32_MAX + 1)

    def test_write_varuint64(self):
        writer = BitStreamWriter()
        writer.write_varuint64(0)
        self.assertEqual(8, writer.bitposition)
        self.assertEqual(b"\x00", writer.byte_array)
        with self.assertRaises(PythonRuntimeException):
            writer.write_varuint64(VARUINT64_MIN - 1)
        with self.assertRaises(PythonRuntimeException):
            writer.write_varuint64(VARUINT64_MAX + 1)

    def test_write_varuint(self):
        writer = BitStreamWriter()
        writer.write_varuint(0)
        self.assertEqual(8, writer.bitposition)
        self.assertEqual(b"\x00", writer.byte_array)
        with self.assertRaises(PythonRuntimeException):
            writer.write_varuint(VARUINT_MIN - 1)
        with self.assertRaises(PythonRuntimeException):
            writer.write_varuint(VARUINT_MAX + 1)

    def test_write_varsize(self):
        writer = BitStreamWriter()
        writer.write_varsize(0)
        self.assertEqual(8, writer.bitposition)
        self.assertEqual(b"\x00", writer.byte_array)
        with self.assertRaises(PythonRuntimeException):
            writer.write_varsize(VARSIZE_MIN - 1)
        with self.assertRaises(PythonRuntimeException):
            writer.write_varsize(VARSIZE_MAX + 1)

    def test_write_float16(self):
        writer = BitStreamWriter()
        writer.write_float16(0)
        self.assertEqual(16, writer.bitposition)
        self.assertEqual(b"\x00\x00", writer.byte_array)

    def test_write_float32(self):
        writer = BitStreamWriter()
        writer.write_float32(0)
        self.assertEqual(32, writer.bitposition)
        self.assertEqual(b"\x00\x00\x00\x00", writer.byte_array)

    def test_write_float64(self):
        writer = BitStreamWriter()
        writer.write_float64(0)
        self.assertEqual(64, writer.bitposition)
        self.assertEqual(b"\x00\x00\x00\x00\x00\x00\x00\x00", writer.byte_array)

    def test_write_string(self):
        writer = BitStreamWriter()
        writer.write_string("")
        self.assertEqual(8, writer.bitposition)  # length 0
        self.assertEqual(b"\x00", writer.byte_array)

    def test_write_bool(self):
        writer = BitStreamWriter()
        writer.write_bool(True)
        writer.write_bool(False)
        writer.write_bool(True)
        writer.write_bool(False)
        writer.write_bool(True)
        writer.write_bool(False)
        self.assertEqual(6, writer.bitposition)
        self.assertEqual(b"\xA8", writer.byte_array)

    def test_write_bitbuffer(self):
        writer = BitStreamWriter()
        writer.write_bitbuffer(BitBuffer(bytes([0xAB, 0xE0]), 11))
        writer.write_bitbuffer(BitBuffer(bytes([0x00, 0xFE]), 15))
        self.assertEqual(8 + 11 + 8 + 15, writer.bitposition)
        self.assertEqual(b"\x0B\xAB\xE1\xE0\x1F\xC0", writer.byte_array)

    def test_byte_array(self):
        writer = BitStreamWriter()
        self.assertEqual(b"", writer.byte_array)

    def test_bitposition(self):
        writer = BitStreamWriter()
        self.assertEqual(0, writer.bitposition)

    def test_alignto(self):
        writer = BitStreamWriter()
        writer.alignto(8)
        self.assertEqual(0, writer.bitposition)
        writer.alignto(2)
        self.assertEqual(0, writer.bitposition)
        writer.write_bool(True)
        writer.alignto(8)
        self.assertEqual(8, writer.bitposition)
        writer.write_bool(True)
        writer.alignto(2)
        self.assertEqual(10, writer.bitposition)
