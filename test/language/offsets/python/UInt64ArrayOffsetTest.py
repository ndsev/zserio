import unittest
import zserio

from testutils import getZserioApi

class UIn64ArrayOffsetTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "offsets.zs").uint64_array_offset

    def testBitSizeOf(self):
        uint64ArrayOffset = self.api.UInt64ArrayOffset.fromReader(self._createReader(False))
        self.assertEqual(self.BIT_SIZE, uint64ArrayOffset.bitSizeOf())

    def testBitSizeOfWithPosition(self):
        uint64ArrayOffset = self.api.UInt64ArrayOffset.fromReader(self._createReader(False))
        self.assertEqual(self.BIT_SIZE + 5, uint64ArrayOffset.bitSizeOf(3))

    def testInitializeOffsets(self):
        uint64ArrayOffset = self.api.UInt64ArrayOffset()
        uint64ArrayOffset.setOffsets(list(range(self.VALUES_SIZE)))
        uint64ArrayOffset.setArray(list(range(self.ARRAY_SIZE)))
        uint64ArrayOffset.setValues(list(range(self.VALUES_SIZE)))
        uint64ArrayOffset.initializeOffsets(0)
        self.assertEqual(self.FIRST_OFFSET, uint64ArrayOffset.getOffsets()[0])

    def testInitializeOffsetsWithPosition(self):
        uint64ArrayOffset = self.api.UInt64ArrayOffset()
        uint64ArrayOffset.setOffsets(list(range(self.VALUES_SIZE)))
        uint64ArrayOffset.setArray(list(range(self.ARRAY_SIZE)))
        uint64ArrayOffset.setValues(list(range(self.VALUES_SIZE)))
        uint64ArrayOffset.initializeOffsets(3)
        # 3 bits start position + 5 bits alignment -> + 1 byte
        self.assertEqual(self.FIRST_OFFSET + 1, uint64ArrayOffset.getOffsets()[0])

    def testRead(self):
        reader = self._createReader(False)
        uint64ArrayOffset = self.api.UInt64ArrayOffset()
        uint64ArrayOffset.read(reader)
        self.assertEqual(self.FIRST_OFFSET, uint64ArrayOffset.getOffsets()[0])

    def testReadWrongOffsets(self):
        reader = self._createReader(True)
        uint64ArrayOffset = self.api.UInt64ArrayOffset()
        with self.assertRaises(zserio.PythonRuntimeException):
            uint64ArrayOffset.read(reader)

    def testWrite(self):
        uint64ArrayOffset = self.api.UInt64ArrayOffset()
        uint64ArrayOffset.setOffsets(list(range(self.VALUES_SIZE)))
        uint64ArrayOffset.setArray(list(range(self.ARRAY_SIZE)))
        uint64ArrayOffset.setValues(list(range(self.VALUES_SIZE)))
        writer = zserio.BitStreamWriter()
        uint64ArrayOffset.write(writer)
        self.assertEqual(self.FIRST_OFFSET, uint64ArrayOffset.getOffsets()[0])
        self.assertEqual(zserio.bitposition.bitsToBytes(self.BIT_SIZE), len(writer.getByteArray()))

    def testWriteWithPosition(self):
        uint64ArrayOffset = self.api.UInt64ArrayOffset()
        uint64ArrayOffset.setOffsets(list(range(self.VALUES_SIZE)))
        uint64ArrayOffset.setArray(list(range(self.ARRAY_SIZE)))
        uint64ArrayOffset.setValues(list(range(self.VALUES_SIZE)))
        writer = zserio.BitStreamWriter()
        writer.writeBits(0, 3)
        uint64ArrayOffset.write(writer)
        self.assertEqual(self.FIRST_OFFSET + 1, uint64ArrayOffset.getOffsets()[0])
        self.assertEqual(zserio.bitposition.bitsToBytes(self.BIT_SIZE) + 1, len(writer.getByteArray()))

    def testWriteWrongOffsets(self):
        uint64ArrayOffset = self.api.UInt64ArrayOffset()
        uint64ArrayOffset.setOffsets([self.FIRST_OFFSET + i * 4 + 1 if (i == self.VALUES_SIZE - 1) else 0
                                      for i in range(self.VALUES_SIZE)])
        uint64ArrayOffset.setArray(list(range(self.ARRAY_SIZE)))
        uint64ArrayOffset.setValues(list(range(self.VALUES_SIZE)))

        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            uint64ArrayOffset.write(writer, callInitializeOffsets=False)

    def _createReader(self, wrongOffset):
        writer = zserio.BitStreamWriter()

        # offset
        writer.writeVarUInt64(self.VALUES_SIZE)
        for i in range(self.VALUES_SIZE):
            offset = self.FIRST_OFFSET + i * 4 + (wrongOffset and 1 if (i == self.VALUES_SIZE - 1) else 0)
            writer.writeBits(offset, 64)

        # array
        writer.writeVarUInt64(self.ARRAY_SIZE)
        for i in range(self.ARRAY_SIZE):
            writer.writeSignedBits(0, 8)

        # values
        writer.writeVarUInt64(self.VALUES_SIZE)
        for i in range(self.VALUES_SIZE):
            writer.writeSignedBits(0, 32)

        return zserio.BitStreamReader(writer.getByteArray())

    ARRAY_SIZE = 13
    VALUES_SIZE = 42

    FIRST_OFFSET = 1 + 8 * 42 + 1 + 13 + 1
    BIT_SIZE = 8 * ((1 + 8 * 42 + 1 + 13 + 1) + 4 * 42)
