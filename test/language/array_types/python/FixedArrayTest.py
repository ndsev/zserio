import unittest
import zserio

from testutils import getZserioApi

class FixedArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types.zs").fixed_array

    def testBitSizeOf(self):
        uint8Array = list(range(self.FIXED_ARRAY_LENGTH))
        fixedArray = self.api.FixedArray(uint8Array)
        bitPosition = 2
        fixedArrayBitSize = self.FIXED_ARRAY_LENGTH * 8
        self.assertEqual(fixedArrayBitSize, fixedArray.bitsizeof(bitPosition))

    def testInitializeOffsets(self):
        uint8Array = list(range(self.FIXED_ARRAY_LENGTH))
        fixedArray = self.api.FixedArray(uint8Array)
        bitPosition = 2
        expectedEndBitPosition = bitPosition + self.FIXED_ARRAY_LENGTH * 8
        self.assertEqual(expectedEndBitPosition, fixedArray.initialize_offsets(bitPosition))

    def testRead(self):
        writer = zserio.BitStreamWriter()
        self._writeFixedArrayToStream(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        fixedArray = self.api.FixedArray.from_reader(reader)

        uint8Array = fixedArray.uint8_array
        self.assertEqual(self.FIXED_ARRAY_LENGTH, len(uint8Array))
        for i in range(self.FIXED_ARRAY_LENGTH):
            self.assertEqual(i, uint8Array[i])

    def testWrite(self):
        uint8Array = list(range(self.FIXED_ARRAY_LENGTH))
        fixedArray = self.api.FixedArray(uint8Array)
        bitBuffer = zserio.serialize(fixedArray)
        readFixedArray = zserio.deserialize(self.api.FixedArray, bitBuffer)
        readUint8Array = readFixedArray.uint8_array
        self.assertEqual(self.FIXED_ARRAY_LENGTH, len(readUint8Array))
        for i in range(self.FIXED_ARRAY_LENGTH):
            self.assertEqual(i, readUint8Array[i])

    def testWriteWrongArray(self):
        uint8Array = list(range(self.FIXED_ARRAY_LENGTH + 1))
        fixedArray = self.api.FixedArray(uint8_array_=uint8Array)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            fixedArray.write(writer)

    def _writeFixedArrayToStream(self, writer):
        for i in range(self.FIXED_ARRAY_LENGTH):
            writer.write_bits(i, 8)

    FIXED_ARRAY_LENGTH = 5
