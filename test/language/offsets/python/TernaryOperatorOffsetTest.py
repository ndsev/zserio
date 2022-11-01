import unittest
import zserio

from testutils import getZserioApi

class TernaryOperatorOffsetTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "offsets.zs").ternary_operator_offset

    def testFirstOffset(self):
        isFirstOffsetUsed = True
        self._testOffset(isFirstOffsetUsed)

    def testFirstOffsetWriteWrong(self):
        isFirstOffsetUsed = True
        self._testOffsetWriteWrong(isFirstOffsetUsed)

    def testFirstOffsetReadWrong(self):
        isFirstOffsetUsed = True
        self._testOffsetReadWrong(isFirstOffsetUsed)

    def testSecondOffset(self):
        isFirstOffsetUsed = False
        self._testOffset(isFirstOffsetUsed)

    def testSecondOffsetWriteWrong(self):
        isFirstOffsetUsed = False
        self._testOffsetWriteWrong(isFirstOffsetUsed)

    def testSecondOffsetReadWrong(self):
        isFirstOffsetUsed = False
        self._testOffsetReadWrong(isFirstOffsetUsed)

    def _writeTernaryOffsetToStream(self, writer, isFirstOffsetUsed, writeWrongOffset):
        writer.write_bool(isFirstOffsetUsed)
        if isFirstOffsetUsed:
            writer.write_bits(self.WRONG_FIELD_OFFSET if writeWrongOffset else self.FIELD_OFFSET, 32)
            writer.write_bits(self.WRONG_FIELD_OFFSET, 32)
        else:
            writer.write_bits(self.WRONG_FIELD_OFFSET, 32)
            writer.write_bits(self.WRONG_FIELD_OFFSET if writeWrongOffset else self.FIELD_OFFSET, 32)
        writer.write_signed_bits(self.FIELD_VALUE, 32)

    def _checkTernaryOffset(self, ternaryOffset, isFirstOffsetUsed):
        self.assertEqual(isFirstOffsetUsed, ternaryOffset.is_first_offset_used)
        if isFirstOffsetUsed:
            self.assertEqual(self.FIELD_OFFSET, ternaryOffset.offsets[0])
            self.assertEqual(self.WRONG_FIELD_OFFSET, ternaryOffset.offsets[1])
        else:
            self.assertEqual(self.WRONG_FIELD_OFFSET, ternaryOffset.offsets[0])
            self.assertEqual(self.FIELD_OFFSET, ternaryOffset.offsets[1])

        self.assertEqual(self.FIELD_VALUE, ternaryOffset.value)

    def _createTernaryOffset(self, isFirstOffsetUsed, createWrongOffset):
        ternaryOffset = self.api.TernaryOffset(isFirstOffsetUsed,
                                               [self.WRONG_FIELD_OFFSET, self.WRONG_FIELD_OFFSET],
                                               self.FIELD_VALUE)
        if not createWrongOffset:
            ternaryOffset.initialize_offsets()

        return ternaryOffset

    def _testOffset(self, isFirstOffsetUsed):
        writeWrongOffset = False
        ternaryOffset = self._createTernaryOffset(isFirstOffsetUsed, writeWrongOffset)

        writer = zserio.BitStreamWriter()
        ternaryOffset.write(writer)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readTernaryOffset = self.api.TernaryOffset.from_reader(reader)

        self._checkTernaryOffset(readTernaryOffset, isFirstOffsetUsed)

    def _testOffsetWriteWrong(self, isFirstOffsetUsed):
        writeWrongOffset = True
        ternaryOffset = self._createTernaryOffset(isFirstOffsetUsed, writeWrongOffset)

        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            ternaryOffset.write(writer)

    def _testOffsetReadWrong(self, isFirstOffsetUsed):
        writer = zserio.BitStreamWriter()
        writeWrongOffset = True
        self._writeTernaryOffsetToStream(writer, isFirstOffsetUsed, writeWrongOffset)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        with self.assertRaises(zserio.PythonRuntimeException):
            self.api.TernaryOffset.from_reader(reader)

    WRONG_FIELD_OFFSET = 0
    FIELD_OFFSET = (1 + 32 + 32 + 7) / 8
    FIELD_VALUE = 0xABCD
