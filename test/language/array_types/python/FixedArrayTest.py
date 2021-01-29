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
        self.assertEqual(fixedArrayBitSize, fixedArray.bitSizeOf(bitPosition))

    def testInitializeOffsets(self):
        uint8Array = list(range(self.FIXED_ARRAY_LENGTH))
        fixedArray = self.api.FixedArray(uint8Array)
        bitPosition = 2
        expectedEndBitPosition = bitPosition + self.FIXED_ARRAY_LENGTH * 8
        self.assertEqual(expectedEndBitPosition, fixedArray.initializeOffsets(bitPosition))

    def testRead(self):
        writer = zserio.BitStreamWriter()
        self._writeFixedArrayToStream(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        fixedArray = self.api.FixedArray.fromReader(reader)

        uint8Array = fixedArray.getUint8Array()
        self.assertEqual(self.FIXED_ARRAY_LENGTH, len(uint8Array))
        for i in range(self.FIXED_ARRAY_LENGTH):
            self.assertEqual(i, uint8Array[i])

    def testWrite(self):
        uint8Array = list(range(self.FIXED_ARRAY_LENGTH))
        fixedArray = self.api.FixedArray(uint8Array)
        writer = zserio.BitStreamWriter()
        fixedArray.write(writer)

        reader = zserio.BitStreamReader(writer.getByteArray())
        readFixedArray = self.api.FixedArray.fromReader(reader)
        readUint8Array = readFixedArray.getUint8Array()
        self.assertEqual(self.FIXED_ARRAY_LENGTH, len(readUint8Array))
        for i in range(self.FIXED_ARRAY_LENGTH):
            self.assertEqual(i, readUint8Array[i])

    def testWriteWrongArray(self):
        uint8Array = list(range(self.FIXED_ARRAY_LENGTH + 1))
        fixedArray = self.api.FixedArray(uint8Array_=uint8Array)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            fixedArray.write(writer)

    def _writeFixedArrayToStream(self, writer):
        for i in range(self.FIXED_ARRAY_LENGTH):
            writer.writeBits(i, 8)

    FIXED_ARRAY_LENGTH = 5
