import unittest
import zserio

from testutils import getZserioApi

class ImplicitArrayUInt64Test(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types.zs").implicit_array_uint64

    def testBitSizeOf(self):
        numElements = 44
        array = list(range(numElements))
        implicitArray = self.api.ImplicitArray(array)
        bitPosition = 2
        implicitArrayBitSize = numElements * 64
        self.assertEqual(implicitArrayBitSize, implicitArray.bitSizeOf(bitPosition))

    def testInitializeOffsets(self):
        numElements = 66
        array = list(range(numElements))
        implicitArray = self.api.ImplicitArray(array)
        bitPosition = 2
        expectedEndBitPosition = bitPosition + numElements * 64
        self.assertEqual(expectedEndBitPosition, implicitArray.initializeOffsets(bitPosition))

    def testRead(self):
        numElements = 99
        writer = zserio.BitStreamWriter()
        ImplicitArrayUInt64Test._writeImplicitArrayToStream(writer, numElements)
        reader = zserio.BitStreamReader(writer.getByteArray())
        implicitArray = self.api.ImplicitArray.fromReader(reader)

        array = implicitArray.getArray()
        self.assertEqual(numElements, len(array))
        for i in range(numElements):
            self.assertEqual(i, array[i])

    def testWrite(self):
        numElements = 55
        array = list(range(numElements))
        implicitArray = self.api.ImplicitArray(array)
        writer = zserio.BitStreamWriter()
        implicitArray.write(writer)

        reader = zserio.BitStreamReader(writer.getByteArray())
        readImplicitArray = self.api.ImplicitArray.fromReader(reader)
        readArray = readImplicitArray.getArray()
        self.assertEqual(numElements, len(readArray))
        for i in range(numElements):
            self.assertEqual(i, readArray[i])

    @staticmethod
    def _writeImplicitArrayToStream(writer, numElements):
        for i in range(numElements):
            writer.writeBits(i, 64)
