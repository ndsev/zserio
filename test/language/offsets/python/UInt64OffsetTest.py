import unittest
import zserio

from testutils import getZserioApi

class UInt64OffsetTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "offsets.zs").uint64_offset

    def testBitSizeOf(self):
        uint64Offset = self.api.UInt64Offset.fromReader(self._createReader(False))
        self.assertEqual(self.BIT_SIZE, uint64Offset.bitSizeOf())

    def testBitSizeOfWithPosition(self):
        uint64Offset = self.api.UInt64Offset.fromReader(self._createReader(False))
        self.assertEqual(self.BIT_SIZE + 5, uint64Offset.bitSizeOf(3))

    def testInitializeOffsets(self):
        uint64Offset = self.api.UInt64Offset()
        uint64Offset.setArray(list(range(self.ARRAY_SIZE)))
        uint64Offset.initializeOffsets(0)
        self.assertEqual(self.OFFSET, uint64Offset.getOffset())

    def testInitializeOffsetsWithPosition(self):
        uint64Offset = self.api.UInt64Offset()
        uint64Offset.setArray(list(range(self.ARRAY_SIZE)))
        uint64Offset.initializeOffsets(3)
        # 3 bits start position + 5 bits alignment -> + 1 byte
        self.assertEqual(self.OFFSET + 1, uint64Offset.getOffset())

    def testRead(self):
        reader = self._createReader(False)
        uint64Offset = self.api.UInt64Offset()
        uint64Offset.read(reader)
        self.assertEqual(self.OFFSET, uint64Offset.getOffset())

    def testReadWrongOffsets(self):
        reader = self._createReader(True)
        uint64Offset = self.api.UInt64Offset()
        with self.assertRaises(zserio.PythonRuntimeException):
            uint64Offset.read(reader)

    def testWrite(self):
        uint64Offset = self.api.UInt64Offset.fromFields(0, list(range(self.ARRAY_SIZE)), 0)
        writer = zserio.BitStreamWriter()
        uint64Offset.write(writer)
        self.assertEqual(self.OFFSET, uint64Offset.getOffset())
        self.assertEqual(self.BIT_SIZE / 8, len(writer.getByteArray()))

    def testWriteWithPosition(self):
        uint64Offset = self.api.UInt64Offset.fromFields(0, list(range(self.ARRAY_SIZE)), 0)
        writer = zserio.BitStreamWriter()
        writer.writeBits(0, 3)
        uint64Offset.write(writer)
        self.assertEqual(self.OFFSET + 1, uint64Offset.getOffset())
        self.assertEqual(self.BIT_SIZE / 8 + 1, len(writer.getByteArray()))

    def testWriteWrongOffsets(self):
        uint64Offset = self.api.UInt64Offset.fromFields(self.WRONG_OFFSET, list(range(self.ARRAY_SIZE)), 0)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            uint64Offset.write(writer, callInitializeOffsets=False)

    def _createReader(self, wrongOffset):
        writer = zserio.BitStreamWriter()

        # offset
        writer.writeBits(self.WRONG_OFFSET if wrongOffset else self.OFFSET, 64)
        writer.writeVarSize(self.ARRAY_SIZE)
        for i in range(self.ARRAY_SIZE):
            writer.writeSignedBits(i, 8)
        writer.writeSignedBits(0, 32)

        return zserio.BitStreamReader(writer.getByteArray())

    ARRAY_SIZE = 13
    OFFSET = 8 + 1 + 13
    WRONG_OFFSET = (8 + 1 + 13) + 1
    BIT_SIZE = 8 * (8 + 1 + 13 + 4)
