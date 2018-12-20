import unittest
import zserio

from testutils import getZserioApi

class AutoArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types.zs").auto_array

    def testBitSizeOfLength1(self):
        self._checkBitSizeOf(self.AUTO_ARRAY_LENGTH1)

    def testBitSizeOfLength2(self):
        self._checkBitSizeOf(self.AUTO_ARRAY_LENGTH2)

    def testInitializeOffsetsLength1(self):
        self._checkInitializeOffsets(self.AUTO_ARRAY_LENGTH1)

    def testInitializeOffsetsLength2(self):
        self._checkInitializeOffsets(self.AUTO_ARRAY_LENGTH2)

    def testReadLength1(self):
        self._checkRead(self.AUTO_ARRAY_LENGTH1)

    def testReadLength2(self):
        self._checkRead(self.AUTO_ARRAY_LENGTH2)

    def testWriteLength1(self):
        self._checkWrite(self.AUTO_ARRAY_LENGTH1)

    def testWriteLength2(self):
        self._checkWrite(self.AUTO_ARRAY_LENGTH2)

    def _checkBitSizeOf(self, numElements):
        uint8Array = [i for i in range(numElements)]
        autoArray = self.api.AutoArray.fromFields(uint8Array)
        bitPosition = 2
        autoArrayBitSize = 8 + numElements * 8
        self.assertEqual(autoArrayBitSize, autoArray.bitSizeOf(bitPosition))

    def _checkInitializeOffsets(self, numElements):
        uint8Array = [i for i in range(numElements)]
        autoArray = self.api.AutoArray.fromFields(uint8Array)
        bitPosition = 2
        expectedEndBitPosition = bitPosition + 8 + numElements * 8
        self.assertEqual(expectedEndBitPosition, autoArray.initializeOffsets(bitPosition))

    def _checkRead(self, numElements):
        writer = zserio.BitStreamWriter()
        AutoArrayTest._writeAutoArrayToStream(writer, numElements)
        reader = zserio.BitStreamReader(writer.getByteArray())
        autoArray = self.api.AutoArray.fromReader(reader)

        uint8Array = autoArray.getUint8Array()
        self.assertEqual(numElements, len(uint8Array))
        for i in range(numElements):
            self.assertEqual(i, uint8Array[i])

    def _checkWrite(self, numElements):
        uint8Array = [i for i in range(numElements)]
        autoArray = self.api.AutoArray.fromFields(uint8Array)
        writer = zserio.BitStreamWriter()
        autoArray.write(writer)

        reader = zserio.BitStreamReader(writer.getByteArray())
        readAutoArray = self.api.AutoArray.fromReader(reader)
        readUint8Array = readAutoArray.getUint8Array()
        self.assertEqual(numElements, len(readUint8Array))
        for i in range(numElements):
            self.assertEqual(i, readUint8Array[i])

    @staticmethod
    def _writeAutoArrayToStream(writer, numElements):
        writer.writeVarUInt64(numElements)
        for i in range(numElements):
            writer.writeBits(i, 8)

    AUTO_ARRAY_LENGTH1 = 5
    AUTO_ARRAY_LENGTH2 = 10
