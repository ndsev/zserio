import unittest

from zserio.bitbuffer import BitBuffer
from zserio.bitsizeof import (
    bitsizeof_varint16,
    bitsizeof_varint32,
    bitsizeof_varint64,
    bitsizeof_varint,
    bitsizeof_varuint16,
    bitsizeof_varuint32,
    bitsizeof_varuint64,
    bitsizeof_varuint,
    bitsizeof_varsize,
    bitsizeof_string,
    bitsizeof_bitbuffer,
)
from zserio.exception import PythonRuntimeException
from zserio.limits import INT64_MIN


class BitSizeOfTest(unittest.TestCase):

    def test_bitsizeof_varint16(self):
        self.assertEqual(1 * 8, bitsizeof_varint16(0))

        self.assertEqual(1 * 8, bitsizeof_varint16(1 << (0)))
        self.assertEqual(1 * 8, bitsizeof_varint16(-(1 << (0))))
        self.assertEqual(1 * 8, bitsizeof_varint16((1 << (6)) - 1))
        self.assertEqual(1 * 8, bitsizeof_varint16(-((1 << (6)) - 1)))

        self.assertEqual(2 * 8, bitsizeof_varint16(1 << (6)))
        self.assertEqual(2 * 8, bitsizeof_varint16(-(1 << (6))))
        self.assertEqual(2 * 8, bitsizeof_varint16((1 << (6 + 8)) - 1))
        self.assertEqual(2 * 8, bitsizeof_varint16(-((1 << (6 + 8)) - 1)))

        with self.assertRaises(PythonRuntimeException):
            bitsizeof_varint16(-(1 << (6 + 8)))  # below the lower bound

        with self.assertRaises(PythonRuntimeException):
            bitsizeof_varint16(1 << (6 + 8))  # above the upper bound

    def test_bitsizeof_varint32(self):
        self.assertEqual(1 * 8, bitsizeof_varint32(0))

        self.assertEqual(1 * 8, bitsizeof_varint32(1 << (0)))
        self.assertEqual(1 * 8, bitsizeof_varint32(-(1 << (0))))
        self.assertEqual(1 * 8, bitsizeof_varint32((1 << (6)) - 1))
        self.assertEqual(1 * 8, bitsizeof_varint32(-((1 << (6)) - 1)))

        self.assertEqual(2 * 8, bitsizeof_varint32(1 << (6)))
        self.assertEqual(2 * 8, bitsizeof_varint32(-(1 << (6))))
        self.assertEqual(2 * 8, bitsizeof_varint32((1 << (6 + 7)) - 1))
        self.assertEqual(2 * 8, bitsizeof_varint32(-((1 << (6 + 7)) - 1)))

        self.assertEqual(3 * 8, bitsizeof_varint32(1 << (6 + 7)))
        self.assertEqual(3 * 8, bitsizeof_varint32(-(1 << (6 + 7))))
        self.assertEqual(3 * 8, bitsizeof_varint32((1 << (6 + 7 + 7)) - 1))
        self.assertEqual(3 * 8, bitsizeof_varint32(-((1 << (6 + 7 + 7)) - 1)))

        self.assertEqual(4 * 8, bitsizeof_varint32(1 << (6 + 7 + 7)))
        self.assertEqual(4 * 8, bitsizeof_varint32(-(1 << (6 + 7 + 7))))
        self.assertEqual(4 * 8, bitsizeof_varint32((1 << (6 + 7 + 7 + 8)) - 1))
        self.assertEqual(4 * 8, bitsizeof_varint32(-((1 << (6 + 7 + 7 + 8)) - 1)))

        with self.assertRaises(PythonRuntimeException):
            bitsizeof_varint32(-(1 << (6 + 7 + 7 + 8)))  # below the lower bound

        with self.assertRaises(PythonRuntimeException):
            bitsizeof_varint32(1 << (6 + 7 + 7 + 8))  # above the upper bound

    def test_bitsizeof_varint64(self):
        self.assertEqual(1 * 8, bitsizeof_varint64(0))

        self.assertEqual(1 * 8, bitsizeof_varint64(1 << (0)))
        self.assertEqual(1 * 8, bitsizeof_varint64(-(1 << (0))))
        self.assertEqual(1 * 8, bitsizeof_varint64((1 << (6)) - 1))
        self.assertEqual(1 * 8, bitsizeof_varint64(-((1 << (6)) - 1)))

        self.assertEqual(2 * 8, bitsizeof_varint64(1 << (6)))
        self.assertEqual(2 * 8, bitsizeof_varint64(-(1 << (6))))
        self.assertEqual(2 * 8, bitsizeof_varint64((1 << (6 + 7)) - 1))
        self.assertEqual(2 * 8, bitsizeof_varint64(-((1 << (6 + 7)) - 1)))

        self.assertEqual(3 * 8, bitsizeof_varint64(1 << (6 + 7)))
        self.assertEqual(3 * 8, bitsizeof_varint64(-(1 << (6 + 7))))
        self.assertEqual(3 * 8, bitsizeof_varint64((1 << (6 + 7 + 7)) - 1))
        self.assertEqual(3 * 8, bitsizeof_varint64(-((1 << (6 + 7 + 7)) - 1)))

        self.assertEqual(4 * 8, bitsizeof_varint64(1 << (6 + 7 + 7)))
        self.assertEqual(4 * 8, bitsizeof_varint64(-(1 << (6 + 7 + 7))))
        self.assertEqual(4 * 8, bitsizeof_varint64((1 << (6 + 7 + 7 + 7)) - 1))
        self.assertEqual(4 * 8, bitsizeof_varint64(-((1 << (6 + 7 + 7 + 7)) - 1)))

        self.assertEqual(5 * 8, bitsizeof_varint64(1 << (6 + 7 + 7 + 7)))
        self.assertEqual(5 * 8, bitsizeof_varint64(-(1 << (6 + 7 + 7 + 7))))
        self.assertEqual(5 * 8, bitsizeof_varint64((1 << (6 + 7 + 7 + 7 + 7)) - 1))
        self.assertEqual(5 * 8, bitsizeof_varint64(-((1 << (6 + 7 + 7 + 7 + 7)) - 1)))

        self.assertEqual(6 * 8, bitsizeof_varint64(1 << (6 + 7 + 7 + 7 + 7)))
        self.assertEqual(6 * 8, bitsizeof_varint64(-(1 << (6 + 7 + 7 + 7 + 7))))
        self.assertEqual(6 * 8, bitsizeof_varint64((1 << (6 + 7 + 7 + 7 + 7 + 7)) - 1))
        self.assertEqual(6 * 8, bitsizeof_varint64(-((1 << (6 + 7 + 7 + 7 + 7 + 7)) - 1)))

        self.assertEqual(7 * 8, bitsizeof_varint64(1 << (6 + 7 + 7 + 7 + 7 + 7)))
        self.assertEqual(7 * 8, bitsizeof_varint64(-(1 << (6 + 7 + 7 + 7 + 7 + 7))))
        self.assertEqual(7 * 8, bitsizeof_varint64((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1))
        self.assertEqual(7 * 8, bitsizeof_varint64(-((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1)))

        self.assertEqual(8 * 8, bitsizeof_varint64(1 << (6 + 7 + 7 + 7 + 7 + 7 + 7)))
        self.assertEqual(8 * 8, bitsizeof_varint64(-(1 << (6 + 7 + 7 + 7 + 7 + 7 + 7))))
        self.assertEqual(8 * 8, bitsizeof_varint64((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1))
        self.assertEqual(8 * 8, bitsizeof_varint64(-((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1)))

        with self.assertRaises(PythonRuntimeException):
            bitsizeof_varint64(-(1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)))  # below the lower bound

        with self.assertRaises(PythonRuntimeException):
            bitsizeof_varint64(1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8))  # above the upper bound

    def test_bitsizeof_varint(self):
        self.assertEqual(8, bitsizeof_varint(0))
        self.assertEqual(8, bitsizeof_varint(-(1 << 6) + 1))
        self.assertEqual(8, bitsizeof_varint((1 << 6) - 1))
        self.assertEqual(16, bitsizeof_varint(-(1 << 6)))
        self.assertEqual(16, bitsizeof_varint((1 << 6)))
        self.assertEqual(16, bitsizeof_varint(-(1 << 13) + 1))
        self.assertEqual(16, bitsizeof_varint((1 << 13) - 1))
        self.assertEqual(24, bitsizeof_varint(-(1 << 13)))
        self.assertEqual(24, bitsizeof_varint((1 << 13)))
        self.assertEqual(24, bitsizeof_varint(-(1 << 20) + 1))
        self.assertEqual(24, bitsizeof_varint((1 << 20) - 1))
        self.assertEqual(32, bitsizeof_varint(-(1 << 20)))
        self.assertEqual(32, bitsizeof_varint((1 << 20)))
        self.assertEqual(32, bitsizeof_varint(-(1 << 27) + 1))
        self.assertEqual(32, bitsizeof_varint((1 << 27) - 1))
        self.assertEqual(40, bitsizeof_varint(-(1 << 27)))
        self.assertEqual(40, bitsizeof_varint((1 << 27)))
        self.assertEqual(40, bitsizeof_varint(-(1 << 34) + 1))
        self.assertEqual(40, bitsizeof_varint((1 << 34) - 1))
        self.assertEqual(48, bitsizeof_varint(-(1 << 34)))
        self.assertEqual(48, bitsizeof_varint((1 << 34)))
        self.assertEqual(48, bitsizeof_varint(-(1 << 41) + 1))
        self.assertEqual(48, bitsizeof_varint((1 << 41) - 1))
        self.assertEqual(56, bitsizeof_varint(-(1 << 41)))
        self.assertEqual(56, bitsizeof_varint((1 << 41)))
        self.assertEqual(56, bitsizeof_varint(-(1 << 48) + 1))
        self.assertEqual(56, bitsizeof_varint((1 << 48) - 1))
        self.assertEqual(64, bitsizeof_varint(-(1 << 48)))
        self.assertEqual(64, bitsizeof_varint((1 << 48)))
        self.assertEqual(64, bitsizeof_varint(-(1 << 55) + 1))
        self.assertEqual(64, bitsizeof_varint((1 << 55) - 1))
        self.assertEqual(72, bitsizeof_varint(-(1 << 55)))
        self.assertEqual(72, bitsizeof_varint((1 << 55)))
        self.assertEqual(72, bitsizeof_varint(-(1 << 63) + 1))
        self.assertEqual(72, bitsizeof_varint((1 << 63) - 1))

        # special case, INT64_MIN is stored as -0
        self.assertEqual(8, bitsizeof_varint(INT64_MIN))

        with self.assertRaises(PythonRuntimeException):
            bitsizeof_varint(INT64_MIN - 1)  # below the lower bound

        with self.assertRaises(PythonRuntimeException):
            bitsizeof_varint(1 << 63)  # above the upper bound

    def test_bitsizeof_varuint16(self):
        self.assertEqual(1 * 8, bitsizeof_varuint16(0))

        self.assertEqual(1 * 8, bitsizeof_varuint16(1 << (0)))
        self.assertEqual(1 * 8, bitsizeof_varuint16((1 << (7)) - 1))

        self.assertEqual(2 * 8, bitsizeof_varuint16(1 << (7)))
        self.assertEqual(2 * 8, bitsizeof_varuint16((1 << (7 + 8)) - 1))

        with self.assertRaises(PythonRuntimeException):
            bitsizeof_varuint16(-1)  # below the lower bound

        with self.assertRaises(PythonRuntimeException):
            bitsizeof_varuint16(1 << (7 + 8))  # above the upper bound

    def test_bitsizeof_varuint32(self):
        self.assertEqual(1 * 8, bitsizeof_varuint32(0))

        self.assertEqual(1 * 8, bitsizeof_varuint32(1 << (0)))
        self.assertEqual(1 * 8, bitsizeof_varuint32((1 << (7)) - 1))

        self.assertEqual(2 * 8, bitsizeof_varuint32(1 << (7)))
        self.assertEqual(2 * 8, bitsizeof_varuint32((1 << (7 + 7)) - 1))

        self.assertEqual(3 * 8, bitsizeof_varuint32(1 << (7 + 7)))
        self.assertEqual(3 * 8, bitsizeof_varuint32((1 << (7 + 7 + 7)) - 1))

        self.assertEqual(4 * 8, bitsizeof_varuint32(1 << (7 + 7 + 7)))
        self.assertEqual(4 * 8, bitsizeof_varuint32((1 << (7 + 7 + 7 + 8)) - 1))

        with self.assertRaises(PythonRuntimeException):
            bitsizeof_varuint32(-1)  # below the lower bound

        with self.assertRaises(PythonRuntimeException):
            bitsizeof_varuint32(1 << (7 + 7 + 7 + 8))  # above the upper bound

    def test_bitsizeof_varuint64(self):
        self.assertEqual(1 * 8, bitsizeof_varuint64(0))

        self.assertEqual(1 * 8, bitsizeof_varuint64(1 << (0)))
        self.assertEqual(1 * 8, bitsizeof_varuint64((1 << (7)) - 1))

        self.assertEqual(2 * 8, bitsizeof_varuint64(1 << (7)))
        self.assertEqual(2 * 8, bitsizeof_varuint64((1 << (7 + 7)) - 1))

        self.assertEqual(3 * 8, bitsizeof_varuint64(1 << (7 + 7)))
        self.assertEqual(3 * 8, bitsizeof_varuint64((1 << (7 + 7 + 7)) - 1))

        self.assertEqual(4 * 8, bitsizeof_varuint64(1 << (7 + 7 + 7)))
        self.assertEqual(4 * 8, bitsizeof_varuint64((1 << (7 + 7 + 7 + 7)) - 1))

        self.assertEqual(5 * 8, bitsizeof_varuint64(1 << (7 + 7 + 7 + 7)))
        self.assertEqual(5 * 8, bitsizeof_varuint64((1 << (7 + 7 + 7 + 7 + 7)) - 1))

        self.assertEqual(6 * 8, bitsizeof_varuint64(1 << (7 + 7 + 7 + 7 + 7)))
        self.assertEqual(6 * 8, bitsizeof_varuint64((1 << (7 + 7 + 7 + 7 + 7 + 7)) - 1))

        self.assertEqual(7 * 8, bitsizeof_varuint64(1 << (7 + 7 + 7 + 7 + 7 + 7)))
        self.assertEqual(7 * 8, bitsizeof_varuint64((1 << (7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1))

        self.assertEqual(8 * 8, bitsizeof_varuint64(1 << (7 + 7 + 7 + 7 + 7 + 7 + 7)))
        self.assertEqual(8 * 8, bitsizeof_varuint64((1 << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1))

        with self.assertRaises(PythonRuntimeException):
            bitsizeof_varuint64(-1)  # below the lower bound

        with self.assertRaises(PythonRuntimeException):
            bitsizeof_varuint64(1 << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 8))  # above the upper bound

    def test_bitsizeof_varuint(self):
        self.assertEqual(8, bitsizeof_varuint(0))
        self.assertEqual(8, bitsizeof_varuint((1 << 7) - 1))
        self.assertEqual(16, bitsizeof_varuint((1 << 7)))
        self.assertEqual(16, bitsizeof_varuint((1 << 14) - 1))
        self.assertEqual(24, bitsizeof_varuint((1 << 14)))
        self.assertEqual(24, bitsizeof_varuint((1 << 21) - 1))
        self.assertEqual(32, bitsizeof_varuint((1 << 21)))
        self.assertEqual(32, bitsizeof_varuint((1 << 28) - 1))
        self.assertEqual(40, bitsizeof_varuint((1 << 28)))
        self.assertEqual(40, bitsizeof_varuint((1 << 35) - 1))
        self.assertEqual(48, bitsizeof_varuint((1 << 35)))
        self.assertEqual(48, bitsizeof_varuint((1 << 42) - 1))
        self.assertEqual(56, bitsizeof_varuint((1 << 42)))
        self.assertEqual(56, bitsizeof_varuint((1 << 49) - 1))
        self.assertEqual(64, bitsizeof_varuint((1 << 49)))
        self.assertEqual(64, bitsizeof_varuint((1 << 56) - 1))
        self.assertEqual(72, bitsizeof_varuint((1 << 56)))
        self.assertEqual(72, bitsizeof_varuint((1 << 64) - 1))

        with self.assertRaises(PythonRuntimeException):
            bitsizeof_varuint(-1)  # below the lower bound

        with self.assertRaises(PythonRuntimeException):
            bitsizeof_varuint(1 << 64)  # above the upper bound

    def test_bitsizeof_varsize(self):
        self.assertEqual(1 * 8, bitsizeof_varsize(0))

        self.assertEqual(1 * 8, bitsizeof_varsize(1 << (0)))
        self.assertEqual(1 * 8, bitsizeof_varsize((1 << (7)) - 1))

        self.assertEqual(2 * 8, bitsizeof_varsize(1 << (7)))
        self.assertEqual(2 * 8, bitsizeof_varsize((1 << (7 + 7)) - 1))

        self.assertEqual(3 * 8, bitsizeof_varsize(1 << (7 + 7)))
        self.assertEqual(3 * 8, bitsizeof_varsize((1 << (7 + 7 + 7)) - 1))

        self.assertEqual(4 * 8, bitsizeof_varsize(1 << (7 + 7 + 7)))
        self.assertEqual(4 * 8, bitsizeof_varsize((1 << (7 + 7 + 7 + 7)) - 1))

        self.assertEqual(5 * 8, bitsizeof_varsize(1 << (7 + 7 + 7 + 7)))
        self.assertEqual(5 * 8, bitsizeof_varsize((1 << (2 + 7 + 7 + 7 + 8)) - 1))

        with self.assertRaises(PythonRuntimeException):
            bitsizeof_varsize(-1)  # below the lower bound

        with self.assertRaises(PythonRuntimeException):
            bitsizeof_varsize(1 << (2 + 7 + 7 + 7 + 8))  # above the upper bound

    def test_bitsizeof_string(self):
        self.assertEqual((1 + 1) * 8, bitsizeof_string("T"))
        self.assertEqual((1 + 4) * 8, bitsizeof_string("TEST"))

        test_string_length = 1 << 7  # 2 bytes per character!
        test_string = (b"\xc2\xAB" * test_string_length).decode("utf-8")
        self.assertEqual((2 + 2 * test_string_length) * 8, bitsizeof_string(test_string))

    def test_bitsizeof_bitbuffer(self):
        test_bitbuffer1 = BitBuffer(bytes([0xAB, 0x03]), 8)
        self.assertEqual(8 + 8, bitsizeof_bitbuffer(test_bitbuffer1))

        test_bitbuffer2 = BitBuffer(bytes([0xAB, 0x03]), 11)
        self.assertEqual(8 + 11, bitsizeof_bitbuffer(test_bitbuffer2))

        test_bitbuffer3 = BitBuffer(bytes([0xAB, 0xCD]), 16)
        self.assertEqual(8 + 16, bitsizeof_bitbuffer(test_bitbuffer3))

        test_bitbuffer4 = BitBuffer(bytes([0xAB, 0xCD]))
        self.assertEqual(8 + 16, bitsizeof_bitbuffer(test_bitbuffer4))

        test_bitbuffer5 = BitBuffer(bytes(16 * [1]), 127)
        self.assertEqual(8 + 15 * 8 + 7, bitsizeof_bitbuffer(test_bitbuffer5))

        test_bitbuffer6 = BitBuffer(bytes(16 * [1]), 128)
        self.assertEqual(16 + 16 * 8, bitsizeof_bitbuffer(test_bitbuffer6))
