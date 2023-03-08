import unittest

from zserio.bitbuffer import BitBuffer
from zserio.bitreader import BitStreamReader
from zserio.bitsizeof import INT64_MIN
from zserio.bitwriter import BitStreamWriter

class BitStreamTest(unittest.TestCase):

    def test_bits(self):
        for numbits in range(1, 65):
            max_value = (1 << numbits) - 1
            values = [
                max_value,
                max_value >> 1,
                max_value >> 2,
                1,
                0,
                1,
                max_value >> 2,
                max_value >> 1,
                max_value
            ]
            self._test_bits_impl(BitStreamWriter.write_bits, BitStreamReader.read_bits, values, numbits)

    def test_signed_bits(self):
        for numbits in range(1, 65):
            min_value = -1 << (numbits - 1)
            max_value = (1 << (numbits - 1)) - 1
            values = [
                min_value,
                max_value,
                min_value >> 1,
                max_value >> 1,
                min_value >> 2,
                max_value >> 2,
                - 1,
                (1 if numbits != 1 else -1),
                0,
                (1 if numbits != 1 else -1),
                - 1,
                max_value >> 2,
                min_value >> 2,
                max_value >> 1,
                min_value >> 1,
                max_value,
                min_value
            ]
            self._test_bits_impl(BitStreamWriter.write_signed_bits, BitStreamReader.read_signed_bits, values,
                                 numbits)

    def test_varint16(self):
        values = [
            # 1 byte
            0,
            - 1,
            + 1,
            - ((1 << (6)) - 1),
            + ((1 << (6)) - 1),
            # 2 bytes
            - ((1 << (6))),
            + ((1 << (6))),
            - ((1 << (6 + 8)) - 1),
            + ((1 << (6 + 8)) - 1),
        ]

        self._test_impl(BitStreamWriter.write_varint16, BitStreamReader.read_varint16, values, 15)

    def test_varint32(self):
        values = [
            # 1 byte
            0,
            - ((1)),
            + ((1)),
            - ((1 << (6)) - 1),
            + ((1 << (6)) - 1),
            # 2 bytes
            - ((1 << (6))),
            + ((1 << (6))),
            - ((1 << (6 + 7)) - 1),
            + ((1 << (6 + 7)) - 1),
            # 3 bytes
            - ((1 << (6 + 7))),
            + ((1 << (6 + 7))),
            - ((1 << (6 + 7 + 7)) - 1),
            + ((1 << (6 + 7 + 7)) - 1),
            # 4 bytes
            - ((1 << (6 + 7 + 7))),
            + ((1 << (6 + 7 + 7))),
            - ((1 << (6 + 7 + 7 + 8)) - 1),
            + ((1 << (6 + 7 + 7 + 8)) - 1)
        ]

        self._test_impl(BitStreamWriter.write_varint32, BitStreamReader.read_varint32, values, 31)

    def test_varint64(self):
        values = [
            # 1 byte
            0,
            - ((1)),
            + ((1)),
            - ((1 << (6)) - 1),
            + ((1 << (6)) - 1),
            # 2 bytes
            - ((1 << (6))),
            + ((1 << (6))),
            - ((1 << (6 + 7)) - 1),
            + ((1 << (6 + 7)) - 1),
            # 3 bytes
            - ((1 << (6 + 7))),
            + ((1 << (6 + 7))),
            - ((1 << (6 + 7 + 7)) - 1),
            + ((1 << (6 + 7 + 7)) - 1),
            # 4 bytes
            - ((1 << (6 + 7 + 7))),
            + ((1 << (6 + 7 + 7))),
            - ((1 << (6 + 7 + 7 + 8)) - 1),
            + ((1 << (6 + 7 + 7 + 8)) - 1)
            # 5 bytes
            - ((1 << (6 + 7 + 7 + 7))),
            + ((1 << (6 + 7 + 7 + 7))),
            - ((1 << (6 + 7 + 7 + 7 + 7)) - 1),
            + ((1 << (6 + 7 + 7 + 7 + 7)) - 1),
            # 6 bytes
            - ((1 << (6 + 7 + 7 + 7 + 7))),
            + ((1 << (6 + 7 + 7 + 7 + 7))),
            - ((1 << (6 + 7 + 7 + 7 + 7 + 7)) - 1),
            + ((1 << (6 + 7 + 7 + 7 + 7 + 7)) - 1),
            # 7 bytes
            - ((1 << (6 + 7 + 7 + 7 + 7 + 7))),
            + ((1 << (6 + 7 + 7 + 7 + 7 + 7))),
            - ((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1),
            + ((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1),
            # 8 bytes
            - ((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7))),
            + ((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7))),
            - ((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1),
            + ((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1),
        ]

        self._test_impl(BitStreamWriter.write_varint64, BitStreamReader.read_varint64, values, 63)

    def test_varint(self):
        values = [
            # 1 byte
            0,
            - ((1)),
            + ((1)),
            - ((1 << (6)) - 1),
            + ((1 << (6)) - 1),
            # 2 bytes
            - ((1 << (6))),
            + ((1 << (6))),
            - ((1 << (6 + 7)) - 1),
            + ((1 << (6 + 7)) - 1),
            # 3 bytes
            - ((1 << (6 + 7))),
            + ((1 << (6 + 7))),
            - ((1 << (6 + 7 + 7)) - 1),
            + ((1 << (6 + 7 + 7)) - 1),
            # 4 bytes
            - ((1 << (6 + 7 + 7))),
            + ((1 << (6 + 7 + 7))),
            - ((1 << (6 + 7 + 7 + 8)) - 1),
            + ((1 << (6 + 7 + 7 + 8)) - 1)
            # 5 bytes
            - ((1 << (6 + 7 + 7 + 7))),
            + ((1 << (6 + 7 + 7 + 7))),
            - ((1 << (6 + 7 + 7 + 7 + 7)) - 1),
            + ((1 << (6 + 7 + 7 + 7 + 7)) - 1),
            # 6 bytes
            - ((1 << (6 + 7 + 7 + 7 + 7))),
            + ((1 << (6 + 7 + 7 + 7 + 7))),
            - ((1 << (6 + 7 + 7 + 7 + 7 + 7)) - 1),
            + ((1 << (6 + 7 + 7 + 7 + 7 + 7)) - 1),
            # 7 bytes
            - ((1 << (6 + 7 + 7 + 7 + 7 + 7))),
            + ((1 << (6 + 7 + 7 + 7 + 7 + 7))),
            - ((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1),
            + ((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1),
            # 8 bytes
            - ((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7))),
            + ((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7))),
            - ((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1),
            + ((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1),
            # 9 bytes
            - ((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 7))),
            + ((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 7))),
            - ((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1),
            + ((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1),
            # 1 byte
            INT64_MIN # special case, stored as -0
        ]

        self._test_impl(BitStreamWriter.write_varint, BitStreamReader.read_varint, values, 71)

    def test_varuint16(self):
        values = [
            # 1 byte
            0,
            1,
            ((1 << (7)) - 1),
            # 2 bytes
            ((1 << (7))),
            ((1 << (7 + 8)) - 1),
        ]

        self._test_impl(BitStreamWriter.write_varuint16, BitStreamReader.read_varuint16, values, 15)

    def test_varuint32(self):
        values = [
            # 1 byte
            ((0)),
            ((1)),
            ((1 << (7)) - 1),
            # 2 bytes
            ((1 << (7))),
            ((1 << (7 + 7)) - 1),
            # 3 bytes
            ((1 << (7 + 7))),
            ((1 << (7 + 7 + 7)) - 1),
            # 4 bytes
            ((1 << (7 + 7 + 7))),
            ((1 << (7 + 7 + 7 + 8)) - 1)
        ]

        self._test_impl(BitStreamWriter.write_varuint32, BitStreamReader.read_varuint32, values, 31)

    def test_varuint64(self):
        values = [
            # 1 byte
            ((0)),
            ((1)),
            ((1 << (7)) - 1),
            # 2 bytes
            ((1 << (7))),
            ((1 << (7 + 7)) - 1),
            # 3 bytes
            ((1 << (7 + 7))),
            ((1 << (7 + 7 + 7)) - 1),
            # 4 bytes
            ((1 << (7 + 7 + 7))),
            ((1 << (7 + 7 + 7 + 8)) - 1),
            # 5 bytes
            ((1 << (7 + 7 + 7 + 7))),
            ((1 << (7 + 7 + 7 + 7 + 7)) - 1),
            # 6 bytes
            ((1 << (7 + 7 + 7 + 7 + 7))),
            ((1 << (7 + 7 + 7 + 7 + 7 + 7)) - 1),
            # 7 bytes
            ((1 << (7 + 7 + 7 + 7 + 7 + 7))),
            ((1 << (7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1),
            # 8 bytes
            ((1 << (7 + 7 + 7 + 7 + 7 + 7 + 7))),
            ((1 << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1),
        ]

        self._test_impl(BitStreamWriter.write_varuint64, BitStreamReader.read_varuint64, values, 63)

    def test_varuint(self):
        values = [
            # 1 byte
            ((0)),
            ((1)),
            ((1 << (7)) - 1),
            # 2 bytes
            ((1 << (7))),
            ((1 << (7 + 7)) - 1),
            # 3 bytes
            ((1 << (7 + 7))),
            ((1 << (7 + 7 + 7)) - 1),
            # 4 bytes
            ((1 << (7 + 7 + 7))),
            ((1 << (7 + 7 + 7 + 8)) - 1),
            # 5 bytes
            ((1 << (7 + 7 + 7 + 7))),
            ((1 << (7 + 7 + 7 + 7 + 7)) - 1),
            # 6 bytes
            ((1 << (7 + 7 + 7 + 7 + 7))),
            ((1 << (7 + 7 + 7 + 7 + 7 + 7)) - 1),
            # 7 bytes
            ((1 << (7 + 7 + 7 + 7 + 7 + 7))),
            ((1 << (7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1),
            # 8 bytes
            ((1 << (7 + 7 + 7 + 7 + 7 + 7 + 7))),
            ((1 << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1),
            # 9 bytes
            ((1 << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 7))),
            ((1 << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1),
        ]

        self._test_impl(BitStreamWriter.write_varuint, BitStreamReader.read_varuint, values, 71)

    def test_varsize(self):
        values = [
            # 1 byte
            ((0)),
            ((1)),
            ((1 << (7)) - 1),
            # 2 bytes
            ((1 << (7))),
            ((1 << (7 + 7)) - 1),
            # 3 bytes
            ((1 << (7 + 7))),
            ((1 << (7 + 7 + 7)) - 1),
            # 4 bytes
            ((1 << (7 + 7 + 7))),
            ((1 << (7 + 7 + 7 + 7)) - 1),
            # 5 bytes
            ((1 << (7 + 7 + 7 + 7))),
            ((1 << (2 + 7 + 7 + 7 + 8)) - 1)
        ]

        self._test_impl(BitStreamWriter.write_varsize, BitStreamReader.read_varsize, values, 39)

    def test_float16(self):
        values = [
            - 42.5,
            - 2.0,
            0.0,
            0.6171875,
            0.875,
            2.0,
            9.875,
            42.5
        ]

        self._test_impl(BitStreamWriter.write_float16, BitStreamReader.read_float16, values, 15)

    def test_float32(self):
        values = [
            - 42.5,
            - 2.0,
            0.0,
            0.6171875,
            0.875,
            2.0,
            9.875,
            42.5
        ]

        self._test_impl(BitStreamWriter.write_float32, BitStreamReader.read_float32, values, 31)

    def test_float64(self):
        values = [
            - 42.5,
            - 2.0,
            0.0,
            0.6171875,
            0.875,
            2.0,
            9.875,
            42.5
        ]

        self._test_impl(BitStreamWriter.write_float64, BitStreamReader.read_float64, values, 63)

    def test_string(self):
        values = [
            "Hello World",
            "\n\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\"\'Hello World2\0nonWrittenPart",
            b"Price: \xE2\x82\xAC 3 what's this? -> \xC2\xA2".decode("utf-8")
        ]

        self._test_impl(BitStreamWriter.write_string, BitStreamReader.read_string, values, 7)

    def test_bool(self):
        values = [
            False,
            True,
            True,
            False,
            False,
            True,
            False,
            True,
            False,
            False,
            True,
            True,
            False
        ]

        self._test_impl(BitStreamWriter.write_bool, BitStreamReader.read_bool, values, 1)

    def test_bitbuffer(self):
        values = [
            BitBuffer(bytes([0xAB, 0x07]), 11),
            BitBuffer(bytes([0xAB, 0xCD, 0x7F]), 23)
        ]

        self._test_impl(BitStreamWriter.write_bitbuffer, BitStreamReader.read_bitbuffer, values, 7)

    def test_bytes(self):
        values = [
            bytearray([0, 255]),
            bytearray([1, 127, 128, 254])
        ]

        self._test_impl(BitStreamWriter.write_bytes, BitStreamReader.read_bytes, values, 7)

    def test_bitposition(self):
        writer = BitStreamWriter()
        writer.write_bits(0xaaaa, 16)
        self.assertEqual(16, writer.bitposition)
        writer.write_bits(0xff, 8)
        self.assertEqual(24, writer.bitposition)

        reader = BitStreamReader(buffer=writer.byte_array)
        self.assertEqual(0xaaaa, reader.read_bits(16))
        self.assertEqual(16, reader.bitposition)
        reader.bitposition = 8
        self.assertEqual(8, reader.bitposition)
        self.assertEqual(0xaaff, reader.read_bits(16))
        reader.bitposition = 13
        self.assertEqual(13, reader.bitposition)
        self.assertEqual(0x02, reader.read_bits(3))
        self.assertEqual(16, reader.bitposition)
        self.assertEqual(0xff, reader.read_bits(8))
        self.assertEqual(24, reader.bitposition)
        reader.bitposition = 0
        self.assertEqual(0, reader.bitposition)
        self.assertEqual(0xaaaaff, reader.read_bits(24))

    def test_alignto(self):
        writer = BitStreamWriter()
        writer.write_bits(5, 3)
        writer.alignto(8)
        self.assertEqual(8, writer.bitposition)
        writer.write_bits(0, 1)
        writer.alignto(16)
        self.assertEqual(16, writer.bitposition)
        writer.write_bits(0xaa, 9)
        writer.alignto(32)
        self.assertEqual(32, writer.bitposition)
        writer.write_bits(0xaca, 13)
        writer.alignto(64)
        self.assertEqual(64, writer.bitposition)
        writer.write_bits(0xcafe, 16)

        reader = BitStreamReader(buffer=writer.byte_array)
        self.assertEqual(5, reader.read_bits(3))
        reader.alignto(8)
        self.assertEqual(8, reader.bitposition)
        self.assertEqual(0, reader.read_bits(1))
        reader.alignto(16)
        self.assertEqual(16, reader.bitposition)
        self.assertEqual(0xaa, reader.read_bits(9))
        reader.alignto(32)
        self.assertEqual(32, reader.bitposition)
        self.assertEqual(0xaca, reader.read_bits(13))
        reader.alignto(64)
        self.assertEqual(64, reader.bitposition)
        self.assertEqual(0xcafe, reader.read_bits(16))

    def test_file(self):
        test_filename = "BitStreamTest.bin"
        writer = BitStreamWriter()
        writer.write_bits(13, 7)
        writer.write_string(test_filename)
        writer.write_varint(-123456)
        writer.to_file(test_filename)

        reader = BitStreamReader.from_file(test_filename)
        self.assertEqual(13, reader.read_bits(7))
        self.assertEqual(test_filename, reader.read_string())
        self.assertEqual(-123456, reader.read_varint())

    def _test_bits_impl(self, write_method, read_method, values, numbits):
        for bit_pos in range(numbits):
            writer = BitStreamWriter()
            if bit_pos > 0:
                writer.write_bits(0, bit_pos)
            for value in values:
                write_method(writer, value, numbits)

            reader = BitStreamReader(buffer=writer.byte_array)
            if bit_pos > 0:
                reader.read_bits(bit_pos)
            for value in values:
                self.assertEqual(value, read_method(reader, numbits),
                                 f"[numbits={numbits}, bit_pos={bit_pos}]")

    def _test_impl(self, write_method, read_method, values, max_start_bit_pos):
        for bit_pos in range(max_start_bit_pos):
            writer = BitStreamWriter()
            if bit_pos > 64:
                writer.write_bits(0, 64)
                writer.write_bits(0, bit_pos - 64)
            elif bit_pos > 0:
                writer.write_bits(0, bit_pos)
            for value in values:
                write_method(writer, value)

            reader = BitStreamReader(buffer=writer.byte_array)
            if bit_pos > 64:
                reader.read_bits(64)
                reader.read_bits(bit_pos - 64)
            elif bit_pos > 0:
                reader.read_bits(bit_pos)
            for value in values:
                self.assertEqual(value, read_method(reader), f"[bit_pos={bit_pos}]")
