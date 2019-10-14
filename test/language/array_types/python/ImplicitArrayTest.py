import unittest
import zserio

from testutils import getZserioApi

class ImplicitArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types.zs").implicit_array

    def testBitSizeOf(self):
        numElements = 44
        uint8Array = list(range(numElements))
        implicitArray = self.api.ImplicitArray.fromFields(uint8Array)
        bitPosition = 2
        implicitArrayBitSize = numElements * 8
        self.assertEqual(implicitArrayBitSize, implicitArray.bitSizeOf(bitPosition))

    def testInitializeOffsets(self):
        numElements = 66
        uint8Array = list(range(numElements))
        implicitArray = self.api.ImplicitArray.fromFields(uint8Array)
        bitPosition = 2
        expectedEndBitPosition = bitPosition + numElements * 8
        self.assertEqual(expectedEndBitPosition, implicitArray.initializeOffsets(bitPosition))

    def testRead(self):
        numElements = 99
        writer = zserio.BitStreamWriter()
        ImplicitArrayTest._writeImplicitArrayToStream(writer, numElements)
        reader = zserio.BitStreamReader(writer.getByteArray())
        implicitArray = self.api.ImplicitArray.fromReader(reader)

        uint8Array = implicitArray.getUint8Array()
        self.assertEqual(numElements, len(uint8Array))
        for i in range(numElements):
            self.assertEqual(i, uint8Array[i])

    def testWrite(self):
        numElements = 55
        uint8Array = list(range(numElements))
        implicitArray = self.api.ImplicitArray.fromFields(uint8Array)
        writer = zserio.BitStreamWriter()
        implicitArray.write(writer)

        reader = zserio.BitStreamReader(writer.getByteArray())
        readImplicitArray = self.api.ImplicitArray.fromReader(reader)
        readUint8Array = readImplicitArray.getUint8Array()
        self.assertEqual(numElements, len(readUint8Array))
        for i in range(numElements):
            self.assertEqual(i, readUint8Array[i])

    @staticmethod
    def _writeImplicitArrayToStream(writer, numElements):
        for i in range(numElements):
            writer.writeBits(i, 8)
