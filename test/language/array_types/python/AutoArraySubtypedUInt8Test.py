import unittest
import os
import zserio

from testutils import getZserioApi, getApiDir

class AutoArraySubtypedUInt8Test(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types.zs").auto_array_subtyped_uint8

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

    def testWriteReadLength1(self):
        self._checkWriteRead(self.AUTO_ARRAY_LENGTH1)

    def testWriteReadLength2(self):
        self._checkWriteRead(self.AUTO_ARRAY_LENGTH2)

    def testWriteReadFileLength1(self):
        self._checkWriteReadFile(self.AUTO_ARRAY_LENGTH1)

    def testWriteReadFileLength2(self):
        self._checkWriteReadFile(self.AUTO_ARRAY_LENGTH2)

    def _checkBitSizeOf(self, numElements):
        array = list(range(numElements))
        autoArray = self.api.AutoArray(array)
        bitPosition = 2
        autoArrayBitSize = 8 + numElements * 8
        self.assertEqual(autoArrayBitSize, autoArray.bitsizeof(bitPosition))

    def _checkInitializeOffsets(self, numElements):
        array = list(range(numElements))
        autoArray = self.api.AutoArray(array)
        bitPosition = 2
        expectedEndBitPosition = bitPosition + 8 + numElements * 8
        self.assertEqual(expectedEndBitPosition, autoArray.initialize_offsets(bitPosition))

    def _checkRead(self, numElements):
        writer = zserio.BitStreamWriter()
        AutoArraySubtypedUInt8Test._writeAutoArrayToStream(writer, numElements)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        autoArray = self.api.AutoArray.from_reader(reader)

        array = autoArray.array
        self.assertEqual(numElements, len(array))
        for i in range(numElements):
            self.assertEqual(i, array[i])

    def _checkWriteRead(self, numElements):
        array = list(range(numElements))
        autoArray = self.api.AutoArray(array_=array)
        bitBuffer = zserio.serialize(autoArray)

        self.assertEqual(autoArray.bitsizeof(), bitBuffer.bitsize)
        self.assertEqual(autoArray.initialize_offsets(0), bitBuffer.bitsize)

        readAutoArray = zserio.deserialize(self.api.AutoArray, bitBuffer)
        readArray = readAutoArray.array
        self.assertEqual(numElements, len(readArray))
        for i in range(numElements):
            self.assertEqual(i, readArray[i])

    def _checkWriteReadFile(self, numElements):
        array = list(range(numElements))
        autoArray = self.api.AutoArray(array_=array)

        filename = self.BLOB_NAME_BASE + str(numElements) + ".blob"
        zserio.serialize_to_file(autoArray, filename)

        readAutoArray = zserio.deserialize_from_file(self.api.AutoArray, filename)
        readArray = readAutoArray.array
        self.assertEqual(numElements, len(readArray))
        for i in range(numElements):
            self.assertEqual(i, readArray[i])

    @staticmethod
    def _writeAutoArrayToStream(writer, numElements):
        writer.write_varsize(numElements)
        for i in range(numElements):
            writer.write_bits(i, 8)

    BLOB_NAME_BASE = os.path.join(getApiDir(os.path.dirname(__file__)), "auto_array_subtyped_uint8_")
    AUTO_ARRAY_LENGTH1 = 5
    AUTO_ARRAY_LENGTH2 = 10
