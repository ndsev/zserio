import unittest
import zserio

from testutils import getZserioApi

class ImplicitArrayBit8Test(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types.zs").implicit_array_bit8

    def testBitSizeOf(self):
        numElements = 44
        array = list(range(numElements))
        implicitArray = self.api.ImplicitArray(array)
        bitPosition = 2
        implicitArrayBitSize = numElements * 8
        self.assertEqual(implicitArrayBitSize, implicitArray.bitSizeOf(bitPosition))

    def testInitializeOffsets(self):
        numElements = 66
        array = list(range(numElements))
        implicitArray = self.api.ImplicitArray(array)
        bitPosition = 2
        expectedEndBitPosition = bitPosition + numElements * 8
        self.assertEqual(expectedEndBitPosition, implicitArray.initializeOffsets(bitPosition))

    def testRead(self):
        numElements = 99
        writer = zserio.BitStreamWriter()
        ImplicitArrayBit8Test._writeImplicitArrayToStream(writer, numElements)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        implicitArray = self.api.ImplicitArray.fromReader(reader)

        array = implicitArray.array
        self.assertEqual(numElements, len(array))
        for i in range(numElements):
            self.assertEqual(i, array[i])

    def testWrite(self):
        numElements = 55
        array = list(range(numElements))
        implicitArray = self.api.ImplicitArray(array)
        bitBuffer = zserio.serialize(implicitArray)
        readImplicitArray = zserio.deserialize(self.api.ImplicitArray, bitBuffer)
        readArray = readImplicitArray.array
        self.assertEqual(numElements, len(readArray))
        for i in range(numElements):
            self.assertEqual(i, readArray[i])

    @staticmethod
    def _writeImplicitArrayToStream(writer, numElements):
        for i in range(numElements):
            writer.write_bits(i, 8)
