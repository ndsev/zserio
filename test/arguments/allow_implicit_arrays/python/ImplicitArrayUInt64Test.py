import unittest
import zserio

from testutils import getZserioApi

class ImplicitArrayUInt64Test(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "allow_implicit_arrays.zs",
                               extraArgs=["-allowImplicitArrays"]).implicit_array_uint64

    def testBitSizeOf(self):
        numElements = 44
        array = list(range(numElements))
        implicitArray = self.api.ImplicitArray(array)
        bitPosition = 2
        implicitArrayBitSize = numElements * 64
        self.assertEqual(implicitArrayBitSize, implicitArray.bitsizeof(bitPosition))

    def testInitializeOffsets(self):
        numElements = 66
        array = list(range(numElements))
        implicitArray = self.api.ImplicitArray(array)
        bitPosition = 2
        expectedEndBitPosition = bitPosition + numElements * 64
        self.assertEqual(expectedEndBitPosition, implicitArray.initialize_offsets(bitPosition))

    def testRead(self):
        numElements = 99
        writer = zserio.BitStreamWriter()
        ImplicitArrayUInt64Test._writeImplicitArrayToStream(writer, numElements)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        implicitArray = self.api.ImplicitArray.from_reader(reader)

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
            writer.write_bits(i, 64)
