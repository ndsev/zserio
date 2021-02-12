import unittest
import zserio

from testutils import getZserioApi

class AutoArrayRecursionTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types.zs").auto_array_recursion

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
        autoArrayRecursion = self._createAutoArrayRecursion(numElements)
        bitPosition = 2
        autoArrayRecursionBitSize = 8 + 8 + numElements * (8 + 8)
        self.assertEqual(autoArrayRecursionBitSize, autoArrayRecursion.bitSizeOf(bitPosition))

    def _checkInitializeOffsets(self, numElements):
        autoArrayRecursion = self._createAutoArrayRecursion(numElements)
        bitPosition = 2
        expectedEndBitPosition = bitPosition + 8 + 8 + numElements * (8 + 8)
        self.assertEqual(expectedEndBitPosition, autoArrayRecursion.initializeOffsets(bitPosition))

    def _checkRead(self, numElements):
        writer = zserio.BitStreamWriter()
        AutoArrayRecursionTest._writeAutoArrayRecursionToStream(writer, numElements)

        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        autoArrayRecursion = self.api.AutoArrayRecursion.fromReader(reader)
        self._checkAutoArrayRecursion(autoArrayRecursion, numElements)

    def _checkWrite(self, numElements):
        autoArrayRecursion = self._createAutoArrayRecursion(numElements)
        bitBuffer = zserio.serialize(autoArrayRecursion)
        readAutoArrayRecursion = zserio.deserialize(self.api.AutoArrayRecursion, bitBuffer)
        self._checkAutoArrayRecursion(readAutoArrayRecursion, numElements)

    def _createAutoArrayRecursion(self, numElements):
        autoArray = []
        for i in range(1, numElements + 1):
            element = self.api.AutoArrayRecursion(i, [])
            autoArray.append(element)

        return self.api.AutoArrayRecursion(0, autoArray)

    def _checkAutoArrayRecursion(self, autoArrayRecursion, numElements):
        self.assertEqual(0, autoArrayRecursion.getId())
        autoArray = autoArrayRecursion.getAutoArrayRecursion()
        self.assertEqual(numElements, len(autoArray))
        for i in range(1, numElements + 1):
            element = autoArray[i - 1]
            self.assertEqual(i, element.getId())
            self.assertEqual(0, len(element.getAutoArrayRecursion()))

    @staticmethod
    def _writeAutoArrayRecursionToStream(writer, numElements):
        writer.writeBits(0, 8)
        writer.writeVarSize(numElements)
        for i in range(1, numElements + 1):
            writer.writeBits(i, 8)
            writer.writeVarSize(0)

    AUTO_ARRAY_LENGTH1 = 5
    AUTO_ARRAY_LENGTH2 = 10
