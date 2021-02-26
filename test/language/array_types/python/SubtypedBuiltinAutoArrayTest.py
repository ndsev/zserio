import unittest
import zserio

from testutils import getZserioApi

class AutoArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types.zs").subtyped_builtin_auto_array

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
        array = list(range(numElements))
        subtypedBuiltinAutoArray = self.api.SubtypedBuiltinAutoArray(array)
        bitPosition = 2
        autoArrayBitSize = 8 + numElements * 8
        self.assertEqual(autoArrayBitSize, subtypedBuiltinAutoArray.bitSizeOf(bitPosition))

    def _checkInitializeOffsets(self, numElements):
        array = list(range(numElements))
        subtypedBuiltinAutoArray = self.api.SubtypedBuiltinAutoArray(array)
        bitPosition = 2
        expectedEndBitPosition = bitPosition + 8 + numElements * 8
        self.assertEqual(expectedEndBitPosition, subtypedBuiltinAutoArray.initializeOffsets(bitPosition))

    def _checkRead(self, numElements):
        writer = zserio.BitStreamWriter()
        AutoArrayTest._writeAutoArrayToStream(writer, numElements)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        subtypedBuiltinAutoArray = self.api.SubtypedBuiltinAutoArray.fromReader(reader)

        array = subtypedBuiltinAutoArray.array
        self.assertEqual(numElements, len(array))
        for i in range(numElements):
            self.assertEqual(i, array[i])

    def _checkWrite(self, numElements):
        array = list(range(numElements))
        subtypedBuiltinAutoArray = self.api.SubtypedBuiltinAutoArray(array_=array)
        bitBuffer = zserio.serialize(subtypedBuiltinAutoArray)
        readAutoArray = zserio.deserialize(self.api.SubtypedBuiltinAutoArray, bitBuffer)
        readArray = readAutoArray.array
        self.assertEqual(numElements, len(readArray))
        for i in range(numElements):
            self.assertEqual(i, readArray[i])

    @staticmethod
    def _writeAutoArrayToStream(writer, numElements):
        writer.writeVarSize(numElements)
        for i in range(numElements):
            writer.writeBits(i, 8)

    AUTO_ARRAY_LENGTH1 = 5
    AUTO_ARRAY_LENGTH2 = 10
