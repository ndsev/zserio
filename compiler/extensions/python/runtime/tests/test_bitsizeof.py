import unittest

from zserio.bitbuffer import BitBuffer
from zserio.bitsizeof import (getBitSizeOfVarInt16, getBitSizeOfVarInt32,
                              getBitSizeOfVarInt64, getBitSizeOfVarInt,
                              getBitSizeOfVarUInt16, getBitSizeOfVarUInt32,
                              getBitSizeOfVarUInt64, getBitSizeOfVarUInt,
                              getBitSizeOfString, getBitSizeOfBitBuffer)
from zserio.exception import PythonRuntimeException
from zserio.limits import INT64_MIN

class BitSizeOfTest(unittest.TestCase):

    def testGetBitSizeOfVarInt16(self):
        self.assertEqual(1 * 8, getBitSizeOfVarInt16(0))

        self.assertEqual(1 * 8, getBitSizeOfVarInt16(1 << (0)))
        self.assertEqual(1 * 8, getBitSizeOfVarInt16(-(1 << (0))))
        self.assertEqual(1 * 8, getBitSizeOfVarInt16((1 << (6)) - 1))
        self.assertEqual(1 * 8, getBitSizeOfVarInt16(-((1 << (6)) - 1)))

        self.assertEqual(2 * 8, getBitSizeOfVarInt16(1 << (6)))
        self.assertEqual(2 * 8, getBitSizeOfVarInt16(-(1 << (6))))
        self.assertEqual(2 * 8, getBitSizeOfVarInt16((1 << (6 + 8)) - 1))
        self.assertEqual(2 * 8, getBitSizeOfVarInt16(-((1 << (6 + 8)) - 1)))

        with self.assertRaises(PythonRuntimeException):
            getBitSizeOfVarInt16(-(1 << (6 + 8))) # below the lower bound

        with self.assertRaises(PythonRuntimeException):
            getBitSizeOfVarInt16(1 << (6 + 8)) # above the upper bound

    def testGetBitSizeOfVarInt32(self):
        self.assertEqual(1 * 8, getBitSizeOfVarInt32(0))

        self.assertEqual(1 * 8, getBitSizeOfVarInt32(1 << (0)))
        self.assertEqual(1 * 8, getBitSizeOfVarInt32(-(1 << (0))))
        self.assertEqual(1 * 8, getBitSizeOfVarInt32((1 << (6)) - 1))
        self.assertEqual(1 * 8, getBitSizeOfVarInt32(-((1 << (6)) - 1)))

        self.assertEqual(2 * 8, getBitSizeOfVarInt32(1 << (6)))
        self.assertEqual(2 * 8, getBitSizeOfVarInt32(-(1 << (6))))
        self.assertEqual(2 * 8, getBitSizeOfVarInt32((1 << (6 + 7)) - 1))
        self.assertEqual(2 * 8, getBitSizeOfVarInt32(-((1 << (6 + 7)) - 1)))

        self.assertEqual(3 * 8, getBitSizeOfVarInt32(1 << (6 + 7)))
        self.assertEqual(3 * 8, getBitSizeOfVarInt32(-(1 << (6 + 7))))
        self.assertEqual(3 * 8, getBitSizeOfVarInt32((1 << (6 + 7 + 7)) - 1))
        self.assertEqual(3 * 8, getBitSizeOfVarInt32(-((1 << (6 + 7 + 7)) - 1)))

        self.assertEqual(4 * 8, getBitSizeOfVarInt32(1 << (6 + 7 + 7)))
        self.assertEqual(4 * 8, getBitSizeOfVarInt32(-(1 << (6 + 7 + 7))))
        self.assertEqual(4 * 8, getBitSizeOfVarInt32((1 << (6 + 7 + 7 + 8)) - 1))
        self.assertEqual(4 * 8, getBitSizeOfVarInt32(-((1 << (6 + 7 + 7 + 8)) - 1)))

        with self.assertRaises(PythonRuntimeException):
            getBitSizeOfVarInt32(-(1 << (6 + 7 + 7 + 8))) # below the lower bound

        with self.assertRaises(PythonRuntimeException):
            getBitSizeOfVarInt32(1 << (6 + 7 + 7 + 8)) # above the upper bound

    def testGetBitSizeOfVarInt64(self):
        self.assertEqual(1 * 8, getBitSizeOfVarInt64(0))

        self.assertEqual(1 * 8, getBitSizeOfVarInt64(1 << (0)))
        self.assertEqual(1 * 8, getBitSizeOfVarInt64(-(1 << (0))))
        self.assertEqual(1 * 8, getBitSizeOfVarInt64((1 << (6)) - 1))
        self.assertEqual(1 * 8, getBitSizeOfVarInt64(-((1 << (6)) - 1)))

        self.assertEqual(2 * 8, getBitSizeOfVarInt64(1 << (6)))
        self.assertEqual(2 * 8, getBitSizeOfVarInt64(-(1 << (6))))
        self.assertEqual(2 * 8, getBitSizeOfVarInt64((1 << (6 + 7)) - 1))
        self.assertEqual(2 * 8, getBitSizeOfVarInt64(-((1 << (6 + 7)) - 1)))

        self.assertEqual(3 * 8, getBitSizeOfVarInt64(1 << (6 + 7)))
        self.assertEqual(3 * 8, getBitSizeOfVarInt64(-(1 << (6 + 7))))
        self.assertEqual(3 * 8, getBitSizeOfVarInt64((1 << (6 + 7 + 7)) - 1))
        self.assertEqual(3 * 8, getBitSizeOfVarInt64(-((1 << (6 + 7 + 7)) - 1)))

        self.assertEqual(4 * 8, getBitSizeOfVarInt64(1 << (6 + 7 + 7)))
        self.assertEqual(4 * 8, getBitSizeOfVarInt64(-(1 << (6 + 7 + 7))))
        self.assertEqual(4 * 8, getBitSizeOfVarInt64((1 << (6 + 7 + 7 + 7)) - 1))
        self.assertEqual(4 * 8, getBitSizeOfVarInt64(-((1 << (6 + 7 + 7 + 7)) - 1)))

        self.assertEqual(5 * 8, getBitSizeOfVarInt64(1 << (6 + 7 + 7 + 7)))
        self.assertEqual(5 * 8, getBitSizeOfVarInt64(-(1 << (6 + 7 + 7 + 7))))
        self.assertEqual(5 * 8, getBitSizeOfVarInt64((1 << (6 + 7 + 7 + 7 + 7)) - 1))
        self.assertEqual(5 * 8, getBitSizeOfVarInt64(-((1 << (6 + 7 + 7 + 7 + 7)) - 1)))

        self.assertEqual(6 * 8, getBitSizeOfVarInt64(1 << (6 + 7 + 7 + 7 + 7)))
        self.assertEqual(6 * 8, getBitSizeOfVarInt64(-(1 << (6 + 7 + 7 + 7 + 7))))
        self.assertEqual(6 * 8, getBitSizeOfVarInt64((1 << (6 + 7 + 7 + 7 + 7 + 7)) - 1))
        self.assertEqual(6 * 8, getBitSizeOfVarInt64(-((1 << (6 + 7 + 7 + 7 + 7 + 7)) - 1)))

        self.assertEqual(7 * 8, getBitSizeOfVarInt64(1 << (6 + 7 + 7 + 7 + 7 + 7)))
        self.assertEqual(7 * 8, getBitSizeOfVarInt64(-(1 << (6 + 7 + 7 + 7 + 7 + 7))))
        self.assertEqual(7 * 8, getBitSizeOfVarInt64((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1))
        self.assertEqual(7 * 8, getBitSizeOfVarInt64(-((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7)) - 1)))

        self.assertEqual(8 * 8, getBitSizeOfVarInt64(1 << (6 + 7 + 7 + 7 + 7 + 7 + 7)))
        self.assertEqual(8 * 8, getBitSizeOfVarInt64(-(1 << (6 + 7 + 7 + 7 + 7 + 7 + 7))))
        self.assertEqual(8 * 8, getBitSizeOfVarInt64((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1))
        self.assertEqual(8 * 8, getBitSizeOfVarInt64(-((1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1)))

        with self.assertRaises(PythonRuntimeException):
            getBitSizeOfVarInt64(-(1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8))) # below the lower bound

        with self.assertRaises(PythonRuntimeException):
            getBitSizeOfVarInt64(1 << (6 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) # above the upper bound

    def testGetBitSizeOfVarInt(self):
        self.assertEqual(8, getBitSizeOfVarInt(0))
        self.assertEqual(8, getBitSizeOfVarInt(-(1 << 6) + 1))
        self.assertEqual(8, getBitSizeOfVarInt((1 << 6) - 1))
        self.assertEqual(16, getBitSizeOfVarInt(-(1 << 6)))
        self.assertEqual(16, getBitSizeOfVarInt((1 << 6)))
        self.assertEqual(16, getBitSizeOfVarInt(-(1 << 13) + 1))
        self.assertEqual(16, getBitSizeOfVarInt((1 << 13) - 1))
        self.assertEqual(24, getBitSizeOfVarInt(-(1 << 13)))
        self.assertEqual(24, getBitSizeOfVarInt((1 << 13)))
        self.assertEqual(24, getBitSizeOfVarInt(-(1 << 20) + 1))
        self.assertEqual(24, getBitSizeOfVarInt((1 << 20) - 1))
        self.assertEqual(32, getBitSizeOfVarInt(-(1 << 20)))
        self.assertEqual(32, getBitSizeOfVarInt((1 << 20)))
        self.assertEqual(32, getBitSizeOfVarInt(-(1 << 27) + 1))
        self.assertEqual(32, getBitSizeOfVarInt((1 << 27) - 1))
        self.assertEqual(40, getBitSizeOfVarInt(-(1 << 27)))
        self.assertEqual(40, getBitSizeOfVarInt((1 << 27)))
        self.assertEqual(40, getBitSizeOfVarInt(-(1 << 34) + 1))
        self.assertEqual(40, getBitSizeOfVarInt((1 << 34) - 1))
        self.assertEqual(48, getBitSizeOfVarInt(-(1 << 34)))
        self.assertEqual(48, getBitSizeOfVarInt((1 << 34)))
        self.assertEqual(48, getBitSizeOfVarInt(-(1 << 41) + 1))
        self.assertEqual(48, getBitSizeOfVarInt((1 << 41) - 1))
        self.assertEqual(56, getBitSizeOfVarInt(-(1 << 41)))
        self.assertEqual(56, getBitSizeOfVarInt((1 << 41)))
        self.assertEqual(56, getBitSizeOfVarInt(-(1 << 48) + 1))
        self.assertEqual(56, getBitSizeOfVarInt((1 << 48) - 1))
        self.assertEqual(64, getBitSizeOfVarInt(-(1 << 48)))
        self.assertEqual(64, getBitSizeOfVarInt((1 << 48)))
        self.assertEqual(64, getBitSizeOfVarInt(-(1 << 55) + 1))
        self.assertEqual(64, getBitSizeOfVarInt((1 << 55) - 1))
        self.assertEqual(72, getBitSizeOfVarInt(-(1 << 55)))
        self.assertEqual(72, getBitSizeOfVarInt((1 << 55)))
        self.assertEqual(72, getBitSizeOfVarInt(-(1 << 63) + 1))
        self.assertEqual(72, getBitSizeOfVarInt((1 << 63) - 1))

        # special case, INT64_MIN is stored as -0
        self.assertEqual(8, getBitSizeOfVarInt(INT64_MIN))

        with self.assertRaises(PythonRuntimeException):
            getBitSizeOfVarInt(INT64_MIN - 1) # below the lower bound

        with self.assertRaises(PythonRuntimeException):
            getBitSizeOfVarInt(1 << 63) # above the upper bound

    def testGetBitSizeOfVarUInt16(self):
        self.assertEqual(1 * 8, getBitSizeOfVarUInt16(0))

        self.assertEqual(1 * 8, getBitSizeOfVarUInt16(1 << (0)))
        self.assertEqual(1 * 8, getBitSizeOfVarUInt16((1 << (7)) - 1))

        self.assertEqual(2 * 8, getBitSizeOfVarUInt16(1 << (7)))
        self.assertEqual(2 * 8, getBitSizeOfVarUInt16((1 << (7 + 8)) - 1))

        with self.assertRaises(PythonRuntimeException):
            getBitSizeOfVarUInt16(-1) # below the lower bound

        with self.assertRaises(PythonRuntimeException):
            getBitSizeOfVarUInt16(1 << (7 + 8)) # above the upper bound

    def testGetBitSizeOfVarUInt32(self):
        self.assertEqual(1 * 8, getBitSizeOfVarUInt32(0))

        self.assertEqual(1 * 8, getBitSizeOfVarUInt32(1 << (0)))
        self.assertEqual(1 * 8, getBitSizeOfVarUInt32((1 << (7)) - 1))

        self.assertEqual(2 * 8, getBitSizeOfVarUInt32(1 << (7)))
        self.assertEqual(2 * 8, getBitSizeOfVarUInt32((1 << (7 + 7)) - 1))

        self.assertEqual(3 * 8, getBitSizeOfVarUInt32(1 << (7 + 7)))
        self.assertEqual(3 * 8, getBitSizeOfVarUInt32((1 << (7 + 7 + 7)) - 1))

        self.assertEqual(4 * 8, getBitSizeOfVarUInt32(1 << (7 + 7 + 7)))
        self.assertEqual(4 * 8, getBitSizeOfVarUInt32((1 << (7 + 7 + 7 + 8)) - 1))

        with self.assertRaises(PythonRuntimeException):
            getBitSizeOfVarUInt32(-1) # below the lower bound

        with self.assertRaises(PythonRuntimeException):
            getBitSizeOfVarUInt32(1 << (7 + 7 + 7 + 8)) # above the upper bound

    def testGetBitSizeOfVarUInt64(self):
        self.assertEqual(1 * 8, getBitSizeOfVarUInt64(0))

        self.assertEqual(1 * 8, getBitSizeOfVarUInt64(1 << (0)))
        self.assertEqual(1 * 8, getBitSizeOfVarUInt64((1 << (7)) - 1))

        self.assertEqual(2 * 8, getBitSizeOfVarUInt64(1 << (7)))
        self.assertEqual(2 * 8, getBitSizeOfVarUInt64((1 << (7 + 7)) - 1))

        self.assertEqual(3 * 8, getBitSizeOfVarUInt64(1 << (7 + 7)))
        self.assertEqual(3 * 8, getBitSizeOfVarUInt64((1 << (7 + 7 + 7)) - 1))

        self.assertEqual(4 * 8, getBitSizeOfVarUInt64(1 << (7 + 7 + 7)))
        self.assertEqual(4 * 8, getBitSizeOfVarUInt64((1 << (7 + 7 + 7 + 7)) - 1))

        self.assertEqual(5 * 8, getBitSizeOfVarUInt64(1 << (7 + 7 + 7 + 7)))
        self.assertEqual(5 * 8, getBitSizeOfVarUInt64((1 << (7 + 7 + 7 + 7 + 7)) - 1))

        self.assertEqual(6 * 8, getBitSizeOfVarUInt64(1 << (7 + 7 + 7 + 7 + 7)))
        self.assertEqual(6 * 8, getBitSizeOfVarUInt64((1 << (7 + 7 + 7 + 7 + 7 + 7)) - 1))

        self.assertEqual(7 * 8, getBitSizeOfVarUInt64(1 << (7 + 7 + 7 + 7 + 7 + 7)))
        self.assertEqual(7 * 8, getBitSizeOfVarUInt64((1 << (7 + 7 + 7 + 7 + 7 + 7 + 7)) - 1))

        self.assertEqual(8 * 8, getBitSizeOfVarUInt64(1 << (7 + 7 + 7 + 7 + 7 + 7 + 7)))
        self.assertEqual(8 * 8, getBitSizeOfVarUInt64((1 << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) - 1))

        with self.assertRaises(PythonRuntimeException):
            getBitSizeOfVarUInt64(-1) # below the lower bound

        with self.assertRaises(PythonRuntimeException):
            getBitSizeOfVarUInt64(1 << (7 + 7 + 7 + 7 + 7 + 7 + 7 + 8)) # above the upper bound

    def testGetBitSizeOfVarUInt(self):
        self.assertEqual(8, getBitSizeOfVarUInt(0))
        self.assertEqual(8, getBitSizeOfVarUInt((1 << 7) - 1))
        self.assertEqual(16, getBitSizeOfVarUInt((1 << 7)))
        self.assertEqual(16, getBitSizeOfVarUInt((1 << 14) - 1))
        self.assertEqual(24, getBitSizeOfVarUInt((1 << 14)))
        self.assertEqual(24, getBitSizeOfVarUInt((1 << 21) - 1))
        self.assertEqual(32, getBitSizeOfVarUInt((1 << 21)))
        self.assertEqual(32, getBitSizeOfVarUInt((1 << 28) - 1))
        self.assertEqual(40, getBitSizeOfVarUInt((1 << 28)))
        self.assertEqual(40, getBitSizeOfVarUInt((1 << 35) - 1))
        self.assertEqual(48, getBitSizeOfVarUInt((1 << 35)))
        self.assertEqual(48, getBitSizeOfVarUInt((1 << 42) - 1))
        self.assertEqual(56, getBitSizeOfVarUInt((1 << 42)))
        self.assertEqual(56, getBitSizeOfVarUInt((1 << 49) - 1))
        self.assertEqual(64, getBitSizeOfVarUInt((1 << 49)))
        self.assertEqual(64, getBitSizeOfVarUInt((1 << 56) - 1))
        self.assertEqual(72, getBitSizeOfVarUInt((1 << 56)))
        self.assertEqual(72, getBitSizeOfVarUInt((1 << 64) - 1))

        with self.assertRaises(PythonRuntimeException):
            getBitSizeOfVarUInt(-1) # below the lower bound

        with self.assertRaises(PythonRuntimeException):
            getBitSizeOfVarUInt(1 << 64) # above the upper bound

    def testGetBitSizeOfString(self):
        self.assertEqual((1 + 1) * 8, getBitSizeOfString("T"))
        self.assertEqual((1 + 4) * 8, getBitSizeOfString("TEST"))

        testStringLength = 1 << 7 # 2 bytes per character!
        testString = (b'\xc2\xAB' * testStringLength).decode("utf-8")
        self.assertEqual((2 + 2 * testStringLength) * 8, getBitSizeOfString(testString))

    def testGetBitSizeOfBitBuffer(self):
        testBitBuffer1 = BitBuffer([0xAB, 0x03], 8)
        self.assertEqual(8 + 8, getBitSizeOfBitBuffer(testBitBuffer1))

        testBitBuffer2 = BitBuffer([0xAB, 0x03], 11)
        self.assertEqual(8 + 11, getBitSizeOfBitBuffer(testBitBuffer2))

        testBitBuffer3 = BitBuffer([0xAB, 0xCD], 16)
        self.assertEqual(8 + 16, getBitSizeOfBitBuffer(testBitBuffer3))

        testBitBuffer4 = BitBuffer([0xAB, 0xCD])
        self.assertEqual(8 + 16, getBitSizeOfBitBuffer(testBitBuffer4))

        testBitBuffer5 = BitBuffer(16 * [1], 127)
        self.assertEqual(8 + 15 * 8 + 7, getBitSizeOfBitBuffer(testBitBuffer5))

        testBitBuffer6 = BitBuffer(16 * [1], 128)
        self.assertEqual(16 + 16 * 8, getBitSizeOfBitBuffer(testBitBuffer6))
