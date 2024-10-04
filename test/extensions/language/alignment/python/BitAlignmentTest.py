import zserio

import Alignment


class BitAlignmentTest(Alignment.TestCase):
    def testBitSizeOf(self):
        bitAlignment = self._createBitAlignment()
        self.assertEqual(self.BIT_ALIGNMENT_BIT_SIZE, bitAlignment.bitsizeof())

    def testBitSizeOfWithPosition(self):
        bitAlignment = self._createBitAlignment()

        # starting at bit position 78, also next 64bits are needed
        for startBitPosition in range(78):
            self.assertEqual(
                self.BIT_ALIGNMENT_BIT_SIZE - startBitPosition, bitAlignment.bitsizeof(startBitPosition)
            )

        # starting at bit position 78, also next 64bits are needed
        startBitPosition = 78
        self.assertEqual(
            self.BIT_ALIGNMENT_BIT_SIZE - startBitPosition + 64, bitAlignment.bitsizeof(startBitPosition)
        )

    def testInitializeOffsets(self):
        bitAlignment = self._createBitAlignment()

        # starting up to bit position 77, the structure still fits into original size thanks to alignments
        for bitPosition in range(78):
            self.assertEqual(self.BIT_ALIGNMENT_BIT_SIZE, bitAlignment.initialize_offsets(bitPosition))

        # starting at bit position 78, also next 64bits are needed
        bitPosition = 78
        self.assertEqual(self.BIT_ALIGNMENT_BIT_SIZE + 64, bitAlignment.initialize_offsets(bitPosition))

    def testRead(self):
        writer = zserio.BitStreamWriter()
        self._writeBitAlignmentToStream(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        bitAlignment = self.api.BitAlignment.from_reader(reader)
        self._checkBitAlignment(bitAlignment)

    def testWrite(self):
        bitAlignment = self._createBitAlignment()
        bitBuffer = zserio.serialize(bitAlignment)
        readBitAlignment = zserio.deserialize(self.api.BitAlignment, bitBuffer)
        self._checkBitAlignment(readBitAlignment)
        self.assertTrue(bitAlignment == readBitAlignment)

    def _writeBitAlignmentToStream(self, writer):
        writer.write_bits(self.ALIGNED1_FIELD_VALUE, 1)

        writer.write_bits(0, 1)
        writer.write_bits(self.ALIGNED2_FIELD_VALUE, 2)

        writer.write_bits(0, 2)
        writer.write_bits(self.ALIGNED3_FIELD_VALUE, 3)

        writer.write_bits(0, 3)
        writer.write_bits(self.ALIGNED4_FIELD_VALUE, 4)

        writer.write_bits(0, 4)
        writer.write_bits(self.ALIGNED5_FIELD_VALUE, 5)

        writer.write_bits(0, 5)
        writer.write_bits(self.ALIGNED6_FIELD_VALUE, 6)

        writer.write_bits(0, 6)
        writer.write_bits(self.ALIGNED7_FIELD_VALUE, 7)

        writer.write_bits(0, 7)
        writer.write_bits(self.ALIGNED8_FIELD_VALUE, 8)

        writer.write_bits(0, 1 + 15)
        writer.write_bits(self.ALIGNED16_FIELD_VALUE, 16)

        writer.write_bits(0, 1 + 31)
        writer.write_bits(self.ALIGNED32_FIELD_VALUE, 32)

        writer.write_bits(0, 33)
        writer.write_bits(0, 63)
        writer.write_bits(self.ALIGNED64_FIELD_VALUE, 64)

    def _checkBitAlignment(self, bitAlignment):
        self.assertEqual(self.ALIGNED1_FIELD_VALUE, bitAlignment.aligned1_field)
        self.assertEqual(self.ALIGNED2_FIELD_VALUE, bitAlignment.aligned2_field)
        self.assertEqual(self.ALIGNED3_FIELD_VALUE, bitAlignment.aligned3_field)
        self.assertEqual(self.ALIGNED4_FIELD_VALUE, bitAlignment.aligned4_field)
        self.assertEqual(self.ALIGNED5_FIELD_VALUE, bitAlignment.aligned5_field)
        self.assertEqual(self.ALIGNED6_FIELD_VALUE, bitAlignment.aligned6_field)
        self.assertEqual(self.ALIGNED7_FIELD_VALUE, bitAlignment.aligned7_field)
        self.assertEqual(self.ALIGNED8_FIELD_VALUE, bitAlignment.aligned8_field)
        self.assertEqual(self.ALIGNED16_FIELD_VALUE, bitAlignment.aligned16_field)
        self.assertEqual(self.ALIGNED32_FIELD_VALUE, bitAlignment.aligned32_field)
        self.assertEqual(self.ALIGNED64_FIELD_VALUE, bitAlignment.aligned64_field)

    def _createBitAlignment(self):
        return self.api.BitAlignment(
            self.ALIGNED1_FIELD_VALUE,
            self.ALIGNED2_FIELD_VALUE,
            self.ALIGNED3_FIELD_VALUE,
            self.ALIGNED4_FIELD_VALUE,
            self.ALIGNED5_FIELD_VALUE,
            self.ALIGNED6_FIELD_VALUE,
            self.ALIGNED7_FIELD_VALUE,
            self.ALIGNED8_FIELD_VALUE,
            0,
            self.ALIGNED16_FIELD_VALUE,
            0,
            self.ALIGNED32_FIELD_VALUE,
            0,
            self.ALIGNED64_FIELD_VALUE,
        )

    BIT_ALIGNMENT_BIT_SIZE = 320

    ALIGNED1_FIELD_VALUE = 1
    ALIGNED2_FIELD_VALUE = 2
    ALIGNED3_FIELD_VALUE = 5
    ALIGNED4_FIELD_VALUE = 13
    ALIGNED5_FIELD_VALUE = 26
    ALIGNED6_FIELD_VALUE = 56
    ALIGNED7_FIELD_VALUE = 88
    ALIGNED8_FIELD_VALUE = 222
    ALIGNED16_FIELD_VALUE = 0xCAFE
    ALIGNED32_FIELD_VALUE = 0xCAFEC0DE
    ALIGNED64_FIELD_VALUE = 0xCAFEC0DEDEADFACE
