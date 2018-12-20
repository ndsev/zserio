import unittest
import zserio

from testutils import getZserioApi

class BitAlignmentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "alignment.zs").bit_alignment

    def testBitSizeOf(self):
        bitAlignment = self._createBitAlignment()
        self.assertEqual(self.BIT_ALIGNMENT_BIT_SIZE, bitAlignment.bitSizeOf())

    def testBitSizeOfWithPosition(self):
        bitAlignment = self._createBitAlignment()

        # starting at bit position 78, also next 64bits are needed
        for startBitPosition in range(78):
            self.assertEqual(self.BIT_ALIGNMENT_BIT_SIZE - startBitPosition,
                             bitAlignment.bitSizeOf(startBitPosition))

        # starting at bit position 78, also next 64bits are needed
        startBitPosition = 78
        self.assertEqual(self.BIT_ALIGNMENT_BIT_SIZE - startBitPosition + 64,
                         bitAlignment.bitSizeOf(startBitPosition))

    def testInitializeOffsets(self):
        bitAlignment = self._createBitAlignment()

        # starting up to bit position 77, the structure still fits into original size thanks to alignments
        for bitPosition in range(78):
            self.assertEqual(self.BIT_ALIGNMENT_BIT_SIZE, bitAlignment.initializeOffsets(bitPosition))

        # starting at bit position 78, also next 64bits are needed
        bitPosition = 78
        self.assertEqual(self.BIT_ALIGNMENT_BIT_SIZE + 64, bitAlignment.initializeOffsets(bitPosition))

    def testRead(self):
        writer = zserio.BitStreamWriter()
        self._writeBitAlignmentToStream(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        bitAlignment = self.api.BitAlignment.fromReader(reader)
        self._checkBitAlignment(bitAlignment)

    def testWrite(self):
        bitAlignment = self._createBitAlignment()
        writer = zserio.BitStreamWriter()
        bitAlignment.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readBitAlignment = self.api.BitAlignment.fromReader(reader)
        self._checkBitAlignment(readBitAlignment)
        self.assertTrue(bitAlignment == readBitAlignment)

    def _writeBitAlignmentToStream(self, writer):
        writer.writeBits(self.ALIGNED1_FIELD_VALUE, 1)

        writer.writeBits(0, 1)
        writer.writeBits(self.ALIGNED2_FIELD_VALUE, 2)

        writer.writeBits(0, 2)
        writer.writeBits(self.ALIGNED3_FIELD_VALUE, 3)

        writer.writeBits(0, 3)
        writer.writeBits(self.ALIGNED4_FIELD_VALUE, 4)

        writer.writeBits(0, 4)
        writer.writeBits(self.ALIGNED5_FIELD_VALUE, 5)

        writer.writeBits(0, 5)
        writer.writeBits(self.ALIGNED6_FIELD_VALUE, 6)

        writer.writeBits(0, 6)
        writer.writeBits(self.ALIGNED7_FIELD_VALUE, 7)

        writer.writeBits(0, 7)
        writer.writeBits(self.ALIGNED8_FIELD_VALUE, 8)

        writer.writeBits(0, 1 + 15)
        writer.writeBits(self.ALIGNED16_FIELD_VALUE, 16)

        writer.writeBits(0, 1 + 31)
        writer.writeBits(self.ALIGNED32_FIELD_VALUE, 32)

        writer.writeBits(0, 33)
        writer.writeBits(0, 63)
        writer.writeBits(self.ALIGNED64_FIELD_VALUE, 64)

    def _checkBitAlignment(self, bitAlignment):
        self.assertEqual(self.ALIGNED1_FIELD_VALUE, bitAlignment.getAligned1Field())
        self.assertEqual(self.ALIGNED2_FIELD_VALUE, bitAlignment.getAligned2Field())
        self.assertEqual(self.ALIGNED3_FIELD_VALUE, bitAlignment.getAligned3Field())
        self.assertEqual(self.ALIGNED4_FIELD_VALUE, bitAlignment.getAligned4Field())
        self.assertEqual(self.ALIGNED5_FIELD_VALUE, bitAlignment.getAligned5Field())
        self.assertEqual(self.ALIGNED6_FIELD_VALUE, bitAlignment.getAligned6Field())
        self.assertEqual(self.ALIGNED7_FIELD_VALUE, bitAlignment.getAligned7Field())
        self.assertEqual(self.ALIGNED8_FIELD_VALUE, bitAlignment.getAligned8Field())
        self.assertEqual(self.ALIGNED16_FIELD_VALUE, bitAlignment.getAligned16Field())
        self.assertEqual(self.ALIGNED32_FIELD_VALUE, bitAlignment.getAligned32Field())
        self.assertEqual(self.ALIGNED64_FIELD_VALUE, bitAlignment.getAligned64Field())

    def _createBitAlignment(self):
        return self.api.BitAlignment.fromFields(self.ALIGNED1_FIELD_VALUE, self.ALIGNED2_FIELD_VALUE,
                                                self.ALIGNED3_FIELD_VALUE, self.ALIGNED4_FIELD_VALUE,
                                                self.ALIGNED5_FIELD_VALUE, self.ALIGNED6_FIELD_VALUE,
                                                self.ALIGNED7_FIELD_VALUE, self.ALIGNED8_FIELD_VALUE, 0,
                                                self.ALIGNED16_FIELD_VALUE, 0, self.ALIGNED32_FIELD_VALUE, 0,
                                                self.ALIGNED64_FIELD_VALUE)

    BIT_ALIGNMENT_BIT_SIZE = 320

    ALIGNED1_FIELD_VALUE = 1
    ALIGNED2_FIELD_VALUE = 2
    ALIGNED3_FIELD_VALUE = 5
    ALIGNED4_FIELD_VALUE = 13
    ALIGNED5_FIELD_VALUE = 26
    ALIGNED6_FIELD_VALUE = 56
    ALIGNED7_FIELD_VALUE = 88
    ALIGNED8_FIELD_VALUE = 222
    ALIGNED16_FIELD_VALUE = 0xcafe
    ALIGNED32_FIELD_VALUE = 0xcafec0de
    ALIGNED64_FIELD_VALUE = 0xcafec0dedeadface
