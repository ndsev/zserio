import unittest

from zserio.bitreader import BitStreamReader
from zserio.bitsizeof import INT64_MIN
from zserio.bitwriter import BitStreamWriter

class BitStreamTest(unittest.TestCase):

    def testBits(self):
        for numBits in range(1, 65):
            maxValue = (1 << numBits) - 1
            values = [
                maxValue,
                maxValue >> 1,
                maxValue >> 2,
                1,
                0,
                1,
                maxValue >> 2,
                maxValue >> 1,
                maxValue
            ]
            self._testBitsImpl(BitStreamWriter.writeBits, BitStreamReader.readBits, values, numBits)

    def testSignedBits(self):
        for numBits in range(1, 65):
            minValue = -1 << (numBits - 1)
            maxValue = (1 << (numBits - 1)) - 1
            values = [
                minValue,
                maxValue,
                minValue >> 1,
                maxValue >> 1,
                minValue >> 2,
                maxValue >> 2,
                - 1,
                (1 if numBits != 1 else -1),
                0,
                (1 if numBits != 1 else -1),
                - 1,
                maxValue >> 2,
                minValue >> 2,
                maxValue >> 1,
                minValue >> 1,
                maxValue,
                minValue
            ]
            self._testBitsImpl(BitStreamWriter.writeSignedBits, BitStreamReader.readSignedBits, values, numBits)

    def testVarInt16(self):
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

        self._testImpl(BitStreamWriter.writeVarInt16, BitStreamReader.readVarInt16, values, 15)

    def testVarInt32(self):
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

        self._testImpl(BitStreamWriter.writeVarInt32, BitStreamReader.readVarInt32, values, 31)

    def testVarInt64(self):
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

        self._testImpl(BitStreamWriter.writeVarInt64, BitStreamReader.readVarInt64, values, 63)

    def testVarInt(self):
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

        self._testImpl(BitStreamWriter.writeVarInt, BitStreamReader.readVarInt, values, 71)

    def testVarUInt16(self):
        values = [
            # 1 byte
            0,
            1,
            ((1 << (7)) - 1),
            # 2 bytes
            ((1 << (7))),
            ((1 << (7 + 8)) - 1),
        ]

        self._testImpl(BitStreamWriter.writeVarUInt16, BitStreamReader.readVarUInt16, values, 15)

    def testVarUInt32(self):
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

        self._testImpl(BitStreamWriter.writeVarUInt32, BitStreamReader.readVarUInt32, values, 31)

    def testVarUInt64(self):
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

        self._testImpl(BitStreamWriter.writeVarUInt64, BitStreamReader.readVarUInt64, values, 63)

    def testVarUInt(self):
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

        self._testImpl(BitStreamWriter.writeVarUInt, BitStreamReader.readVarUInt, values, 71)

    def testFloat16(self):
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

        self._testImpl(BitStreamWriter.writeFloat16, BitStreamReader.readFloat16, values, 15)

    def testFloat32(self):
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

        self._testImpl(BitStreamWriter.writeFloat32, BitStreamReader.readFloat32, values, 31)

    def testFloat64(self):
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

        self._testImpl(BitStreamWriter.writeFloat64, BitStreamReader.readFloat64, values, 63)

    def testString(self):
        values = [
            "Hello World",
            "\n\t%^@(*aAzZ01234569$%^!?<>[]](){}-=+~:;/|\\\"\'Hello World2\0nonWrittenPart",
            b"Price: \xE2\x82\xAC 3 what's this? -> \xC2\xA2".decode("utf-8")
        ]

        self._testImpl(BitStreamWriter.writeString, BitStreamReader.readString, values, 7)

    def testBool(self):
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

        self._testImpl(BitStreamWriter.writeBool, BitStreamReader.readBool, values, 1)

    def testBitPosition(self):
        writer = BitStreamWriter()
        writer.writeBits(0xaaaa, 16)
        self.assertEqual(16, writer.getBitPosition())
        writer.writeBits(0xff, 8)
        self.assertEqual(24, writer.getBitPosition())

        reader = BitStreamReader(buffer=writer.getByteArray())
        self.assertEqual(0xaaaa, reader.readBits(16))
        self.assertEqual(16, reader.getBitPosition())
        reader.setBitPosition(8)
        self.assertEqual(8, reader.getBitPosition())
        self.assertEqual(0xaaff, reader.readBits(16))
        reader.setBitPosition(13)
        self.assertEqual(13, reader.getBitPosition())
        self.assertEqual(0x02, reader.readBits(3))
        self.assertEqual(16, reader.getBitPosition())
        self.assertEqual(0xff, reader.readBits(8))
        self.assertEqual(24, reader.getBitPosition())
        reader.setBitPosition(0)
        self.assertEqual(0, reader.getBitPosition())
        self.assertEqual(0xaaaaff, reader.readBits(24))

    def testAlignTo(self):
        writer = BitStreamWriter()
        writer.writeBits(5, 3)
        writer.alignTo(8)
        self.assertEqual(8, writer.getBitPosition())
        writer.writeBits(0, 1)
        writer.alignTo(16)
        self.assertEqual(16, writer.getBitPosition())
        writer.writeBits(0xaa, 9)
        writer.alignTo(32)
        self.assertEqual(32, writer.getBitPosition())
        writer.writeBits(0xaca, 13)
        writer.alignTo(64)
        self.assertEqual(64, writer.getBitPosition())
        writer.writeBits(0xcafe, 16)

        reader = BitStreamReader(buffer=writer.getByteArray())
        self.assertEqual(5, reader.readBits(3))
        reader.alignTo(8)
        self.assertEqual(8, reader.getBitPosition())
        self.assertEqual(0, reader.readBits(1))
        reader.alignTo(16)
        self.assertEqual(16, reader.getBitPosition())
        self.assertEqual(0xaa, reader.readBits(9))
        reader.alignTo(32)
        self.assertEqual(32, reader.getBitPosition())
        self.assertEqual(0xaca, reader.readBits(13))
        reader.alignTo(64)
        self.assertEqual(64, reader.getBitPosition())
        self.assertEqual(0xcafe, reader.readBits(16))

    def testFile(self):
        TEST_FILE_NAME = "BitStreamTest.bin"
        writer = BitStreamWriter()
        writer.writeBits(13, 7)
        writer.writeString(TEST_FILE_NAME)
        writer.writeVarInt(-123456)
        writer.toFile(TEST_FILE_NAME)

        reader = BitStreamReader.fromFile(TEST_FILE_NAME)
        self.assertEqual(13, reader.readBits(7))
        self.assertEqual(TEST_FILE_NAME, reader.readString())
        self.assertEqual(-123456, reader.readVarInt())

    def _testBitsImpl(self, writeMethod, readMethod, values, numBits):
        for bitPos in range(numBits):
            writer = BitStreamWriter()
            if bitPos > 0:
                writer.writeBits(0, bitPos)
            for v in values:
                writeMethod(writer, v, numBits)

            reader = BitStreamReader(buffer=writer.getByteArray())
            if bitPos > 0:
                reader.readBits(bitPos)
            for v in values:
                self.assertEqual(v, readMethod(reader, numBits),
                                 "[numBits=%d, bitPos=%d]" % (numBits, bitPos))

    def _testImpl(self, writeMethod, readMethod, values, maxStartBitPos):
        for bitPos in range(maxStartBitPos):
            writer = BitStreamWriter()
            if bitPos > 0:
                writer.writeBits(0, bitPos)
            for v in values:
                writeMethod(writer, v)

            reader = BitStreamReader(buffer=writer.getByteArray())
            if bitPos > 0:
                reader.readBits(bitPos)
            for v in values:
                self.assertEqual(v, readMethod(reader), "[bitPos=%d]" % bitPos)
