import zserio

import Offsets


class UInt64OffsetTest(Offsets.TestCase):
    def testBitSizeOf(self):
        uint64Offset = self.api.UInt64Offset.from_reader(self._createReader(False))
        self.assertEqual(self.BIT_SIZE, uint64Offset.bitsizeof())

    def testBitSizeOfWithPosition(self):
        uint64Offset = self.api.UInt64Offset.from_reader(self._createReader(False))
        self.assertEqual(self.BIT_SIZE + 5, uint64Offset.bitsizeof(3))

    def testInitializeOffsets(self):
        uint64Offset = self.api.UInt64Offset()
        uint64Offset.array = list(range(self.ARRAY_SIZE))
        uint64Offset.initialize_offsets()
        self.assertEqual(self.OFFSET, uint64Offset.offset)

    def testInitializeOffsetsWithPosition(self):
        uint64Offset = self.api.UInt64Offset()
        uint64Offset.array = list(range(self.ARRAY_SIZE))
        uint64Offset.initialize_offsets(3)
        # 3 bits start position + 5 bits alignment -> + 1 byte
        self.assertEqual(self.OFFSET + 1, uint64Offset.offset)

    def testRead(self):
        reader = self._createReader(False)
        uint64Offset = self.api.UInt64Offset()
        uint64Offset.read(reader)
        self.assertEqual(self.OFFSET, uint64Offset.offset)

    def testReadWrongOffsets(self):
        reader = self._createReader(True)
        uint64Offset = self.api.UInt64Offset()
        with self.assertRaises(zserio.PythonRuntimeException):
            uint64Offset.read(reader)

    def testWrite(self):
        uint64Offset = self.api.UInt64Offset(0, list(range(self.ARRAY_SIZE)), 0)
        writer = zserio.BitStreamWriter()
        uint64Offset.initialize_offsets(writer.bitposition)
        uint64Offset.write(writer)
        self.assertEqual(self.OFFSET, uint64Offset.offset)
        self.assertEqual(self.BIT_SIZE / 8, len(writer.byte_array))

    def testWriteWithPosition(self):
        uint64Offset = self.api.UInt64Offset(0, list(range(self.ARRAY_SIZE)), 0)
        writer = zserio.BitStreamWriter()
        writer.write_bits(0, 3)
        uint64Offset.initialize_offsets(writer.bitposition)
        uint64Offset.write(writer)
        self.assertEqual(self.OFFSET + 1, uint64Offset.offset)
        self.assertEqual(self.BIT_SIZE / 8 + 1, len(writer.byte_array))

    def testWriteWrongOffsets(self):
        uint64Offset = self.api.UInt64Offset(self.WRONG_OFFSET, list(range(self.ARRAY_SIZE)), 0)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            uint64Offset.write(writer)

    def _createReader(self, wrongOffset):
        writer = zserio.BitStreamWriter()

        # offset
        writer.write_bits(self.WRONG_OFFSET if wrongOffset else self.OFFSET, 64)
        writer.write_varsize(self.ARRAY_SIZE)
        for i in range(self.ARRAY_SIZE):
            writer.write_signed_bits(i, 8)
        writer.write_signed_bits(0, 32)

        return zserio.BitStreamReader(writer.byte_array, writer.bitposition)

    ARRAY_SIZE = 13
    OFFSET = 8 + 1 + 13
    WRONG_OFFSET = (8 + 1 + 13) + 1
    BIT_SIZE = 8 * (8 + 1 + 13 + 4)
