import unittest
import zserio

from testutils import getZserioApi

class SubtypedStructVariableArrayTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types.zs").subtyped_struct_variable_array

    def testBitSizeOf(self):
        numElements = 33
        compoundArray = [self.api.ArrayElement(i, "Name" + str(i)) for i in range(numElements)]
        subtypedStructVariableArray = self.api.SubtypedStructVariableArray(numElements, compoundArray)
        bitPosition = 2
        numOneNumberIndexes = 10
        expectedBitSize = (1 + numElements * (4 + 7) - numOneNumberIndexes) * 8
        self.assertEqual(expectedBitSize, subtypedStructVariableArray.bitSizeOf(bitPosition))

    def testInitializeOffsets(self):
        numElements = 33
        compoundArray = [self.api.ArrayElement(id_=i, name_="Name" + str(i)) for i in range(numElements)]
        subtypedStructVariableArray = self.api.SubtypedStructVariableArray(numElements, compoundArray)
        bitPosition = 2
        numOneNumberIndexes = 10
        expectedEndBitPosition = bitPosition + (1 + numElements * (4 + 7) - numOneNumberIndexes) * 8
        self.assertEqual(expectedEndBitPosition, subtypedStructVariableArray.initializeOffsets(bitPosition))

    def testRead(self):
        numElements = 59
        writer = zserio.BitStreamWriter()
        SubtypedStructVariableArrayTest._writeSubtypedStructVariableArrayToStream(writer, numElements)
        reader = zserio.BitStreamReader(writer.getByteArray())
        subtypedStructVariableArray = self.api.SubtypedStructVariableArray.fromReader(reader)

        self.assertEqual(numElements, subtypedStructVariableArray.getNumElements())
        compoundArray = subtypedStructVariableArray.getCompoundArray()
        self.assertEqual(numElements, len(compoundArray))
        for i in range(numElements):
            testStructure = compoundArray[i]
            self.assertEqual(i, testStructure.getId())
            self.assertTrue(testStructure.getName() == "Name" + str(i))

    def testWrite(self):
        numElements = 33
        compoundArray = [self.api.ArrayElement(i, "Name" + str(i)) for i in range(numElements)]
        subtypedStructVariableArray = self.api.SubtypedStructVariableArray(numElements, compoundArray)
        writer = zserio.BitStreamWriter()
        subtypedStructVariableArray.write(writer)

        reader = zserio.BitStreamReader(writer.getByteArray())
        readSubtypedStructVariableArray = self.api.SubtypedStructVariableArray.fromReader(reader)
        self.assertEqual(numElements, readSubtypedStructVariableArray.getNumElements())
        readCompoundArray = readSubtypedStructVariableArray.getCompoundArray()
        self.assertEqual(numElements, len(readCompoundArray))
        for i in range(numElements):
            readArrayElement = readCompoundArray[i]
            self.assertEqual(i, readArrayElement.getId())
            self.assertTrue(readArrayElement.getName() == "Name" + str(i))

    def testWriteWrongArray(self):
        numElements = 33
        compoundArray = [self.api.ArrayElement(i, name_="Name" + str(i)) for i in range(numElements)]
        subtypedStructVariableArray = self.api.SubtypedStructVariableArray(numElements + 1, compoundArray)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            subtypedStructVariableArray.write(writer)

    @staticmethod
    def _writeSubtypedStructVariableArrayToStream(writer, numElements):
        writer.writeSignedBits(numElements, 8)
        for i in range(numElements):
            writer.writeBits(i, 32)
            writer.writeString("Name" + str(i))
