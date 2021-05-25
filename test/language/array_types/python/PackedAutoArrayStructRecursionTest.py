import unittest
import zserio

from testutils import getZserioApi

class PackedAutoArrayStructRecursionTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types.zs").packed_auto_array_struct_recursion

    def testBitSizeOfLength1(self):
        self._checkBitSizeOf(self.AUTO_ARRAY_LENGTH1)

    def testBitSizeOfLength2(self):
        self._checkBitSizeOf(self.AUTO_ARRAY_LENGTH2)

    def testBitSizeOfLength3(self):
        self._checkBitSizeOf(self.AUTO_ARRAY_LENGTH3)

    def testInitializeOffsetsLength1(self):
        self._checkInitializeOffsets(self.AUTO_ARRAY_LENGTH1)

    def testInitializeOffsetsLength2(self):
        self._checkInitializeOffsets(self.AUTO_ARRAY_LENGTH2)

    def testInitializeOffsetsLength3(self):
        self._checkInitializeOffsets(self.AUTO_ARRAY_LENGTH3)

    def testReadLength1(self):
        self._checkRead(self.AUTO_ARRAY_LENGTH1)

    def testReadLength2(self):
        self._checkRead(self.AUTO_ARRAY_LENGTH2)

    def testReadLength3(self):
        self._checkRead(self.AUTO_ARRAY_LENGTH3)

    def testWriteLength1(self):
        self._checkWrite(self.AUTO_ARRAY_LENGTH1)

    def testWriteLength2(self):
        self._checkWrite(self.AUTO_ARRAY_LENGTH2)

    def testWriteLength3(self):
        self._checkWrite(self.AUTO_ARRAY_LENGTH3)

    def _checkBitSizeOf(self, numElements):
        packedAutoArrayRecursion = self._createPackedAutoArrayRecursion(numElements)
        bitPosition = 2
        autoArrayRecursionBitSize = 8 + 8 + 1 + 6 + 8 + 8 + (numElements - 1) * (8 + 2)
        self.assertEqual(autoArrayRecursionBitSize, packedAutoArrayRecursion.bitsizeof(bitPosition))

    def _checkInitializeOffsets(self, numElements):
        packedAutoArrayRecursion = self._createPackedAutoArrayRecursion(numElements)
        bitPosition = 2
        expectedEndBitPosition = bitPosition + 8 + 8 + 1 + 6 + 8 + 8 + (numElements - 1) * (8 + 2)
        self.assertEqual(expectedEndBitPosition, packedAutoArrayRecursion.initialize_offsets(bitPosition))

    def _checkRead(self, numElements):
        writer = zserio.BitStreamWriter()
        self._writePackedAutoArrayRecursionToStream(writer, numElements)

        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        packedAutoArrayRecursion = self.api.PackedAutoArrayRecursion.from_reader(reader)
        self._checkPackedAutoArrayRecursion(packedAutoArrayRecursion, numElements)

    def _checkWrite(self, numElements):
        packedAutoArrayRecursion = self._createPackedAutoArrayRecursion(numElements)
        bitBuffer = zserio.serialize(packedAutoArrayRecursion)
        readPackedAutoArrayRecursion = zserio.deserialize(self.api.PackedAutoArrayRecursion, bitBuffer)
        self._checkPackedAutoArrayRecursion(readPackedAutoArrayRecursion, numElements)

    def _createPackedAutoArrayRecursion(self, numElements):
        autoArray = []
        for i in range(1, numElements + 1):
            element = self.api.PackedAutoArrayRecursion(i, [])
            autoArray.append(element)

        return self.api.PackedAutoArrayRecursion(0, autoArray)

    def _checkPackedAutoArrayRecursion(self, packedAutoArrayRecursion, numElements):
        self.assertEqual(0, packedAutoArrayRecursion.id)
        autoArray = packedAutoArrayRecursion.packed_auto_array_recursion
        self.assertEqual(numElements, len(autoArray))
        for i in range(1, numElements + 1):
            element = autoArray[i - 1]
            self.assertEqual(i, element.id)
            self.assertEqual(0, len(element.packed_auto_array_recursion))

    @staticmethod
    def _writePackedAutoArrayRecursionToStream(writer, numElements):
        writer.write_bits(0, 8)
        writer.write_varsize(numElements)
        writer.write_bool(True)
        maxBitNumber = 1
        writer.write_bits(maxBitNumber, 6)
        writer.write_bits(1, 8)
        writer.write_varsize(0)
        for _ in range(numElements - 1):
            writer.write_signed_bits(1, maxBitNumber + 1)
            writer.write_varsize(0)

    AUTO_ARRAY_LENGTH1 = 1
    AUTO_ARRAY_LENGTH2 = 5
    AUTO_ARRAY_LENGTH3 = 10
