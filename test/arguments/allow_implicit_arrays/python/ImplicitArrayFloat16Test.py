import os
import zserio

import AllowImplicitArrays

from testutils import getApiDir

class ImplicitArrayFloat16Test(AllowImplicitArrays.TestCase):
    def testBitSizeOf(self):
        numElements = 44
        array = list(range(numElements))
        implicitArray = self.api.ImplicitArray(array)
        bitPosition = 2
        implicitArrayBitSize = numElements * 16
        self.assertEqual(implicitArrayBitSize, implicitArray.bitsizeof(bitPosition))

    def testInitializeOffsets(self):
        numElements = 66
        array = list(range(numElements))
        implicitArray = self.api.ImplicitArray(array)
        bitPosition = 2
        expectedEndBitPosition = bitPosition + numElements * 16
        self.assertEqual(expectedEndBitPosition, implicitArray.initialize_offsets(bitPosition))

    def testRead(self):
        numElements = 99
        writer = zserio.BitStreamWriter()
        ImplicitArrayFloat16Test._writeImplicitArrayToStream(writer, numElements)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        implicitArray = self.api.ImplicitArray.from_reader(reader)

        array = implicitArray.array
        self.assertEqual(numElements, len(array))
        for i in range(numElements):
            self.assertEqual(i, array[i])

    def testWriteRead(self):
        numElements = 55
        array = list(range(numElements))
        implicitArray = self.api.ImplicitArray(array)
        bitBuffer = zserio.serialize(implicitArray)

        self.assertEqual(implicitArray.bitsizeof(), bitBuffer.bitsize)
        self.assertEqual(implicitArray.initialize_offsets(), bitBuffer.bitsize)

        readImplicitArray = zserio.deserialize(self.api.ImplicitArray, bitBuffer)
        readArray = readImplicitArray.array
        self.assertEqual(numElements, len(readArray))
        for i in range(numElements):
            self.assertEqual(i, readArray[i])

    def testWriteReadFile(self):
        numElements = 55
        array = list(range(numElements))
        implicitArray = self.api.ImplicitArray(array)
        zserio.serialize_to_file(implicitArray, self.BLOB_NAME)

        readImplicitArray = zserio.deserialize_from_file(self.api.ImplicitArray, self.BLOB_NAME)
        readArray = readImplicitArray.array
        self.assertEqual(numElements, len(readArray))
        for i in range(numElements):
            self.assertEqual(i, readArray[i])

    @staticmethod
    def _writeImplicitArrayToStream(writer, numElements):
        for i in range(numElements):
            writer.write_float16(i)

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)), "implicit_array_float16.blob")
