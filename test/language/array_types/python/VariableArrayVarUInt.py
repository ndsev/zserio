import unittest
import zserio

from testutils import getZserioApi

class VariableArrayVarUIntTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types.zs").variable_array_varuint

    def testBitSizeOf(self):
        numElements = 33
        compoundArray = [self.api.TestStructure(i, "Name" + str(i)) for i in range(numElements)]
        variableArray = self.api.VariableArray(numElements, compoundArray)
        bitPosition = 2
        numOneNumberIndexes = 10
        expectedBitSize = (1 + numElements * (4 + 7) - numOneNumberIndexes) * 8
        self.assertEqual(expectedBitSize, variableArray.bitSizeOf(bitPosition))

    def testInitializeOffsets(self):
        numElements = 33
        compoundArray = [self.api.TestStructure(i, "Name" + str(i)) for i in range(numElements)]
        variableArray = self.api.VariableArray(numElements, compoundArray)
        bitPosition = 2
        numOneNumberIndexes = 10
        expectedEndBitPosition = bitPosition + (1 + numElements * (4 + 7) - numOneNumberIndexes) * 8
        self.assertEqual(expectedEndBitPosition, variableArray.initializeOffsets(bitPosition))

    def testRead(self):
        numElements = 59
        writer = zserio.BitStreamWriter()
        VariableArrayVarUIntTest._writeVariableArrayToStream(writer, numElements)
        reader = zserio.BitStreamReader(writer.getByteArray())
        variableArray = self.api.VariableArray.fromReader(reader)

        self.assertEqual(numElements, variableArray.getNumElements())
        compoundArray = variableArray.getCompoundArray()
        self.assertEqual(numElements, len(compoundArray))
        for i in range(numElements):
            testStructure = compoundArray[i]
            self.assertEqual(i, testStructure.getId())
            self.assertTrue(testStructure.getName() == "Name" + str(i))

    def testWrite(self):
        numElements = 33
        compoundArray = [self.api.TestStructure(id_=i, name_="Name" + str(i)) for i in range(numElements)]
        variableArray = self.api.VariableArray(numElements_=numElements, compoundArray_=compoundArray)
        writer = zserio.BitStreamWriter()
        variableArray.write(writer)

        reader = zserio.BitStreamReader(writer.getByteArray())
        readVariableArray = self.api.VariableArray.fromReader(reader)
        self.assertEqual(numElements, readVariableArray.getNumElements())
        readCompoundArray = readVariableArray.getCompoundArray()
        self.assertEqual(numElements, len(readCompoundArray))
        for i in range(numElements):
            readTestStructure = readCompoundArray[i]
            self.assertEqual(i, readTestStructure.getId())
            self.assertTrue(readTestStructure.getName() == "Name" + str(i))

    def testWriteWrongArray(self):
        numElements = 33
        compoundArray = [self.api.TestStructure(i, "Name" + str(i)) for i in range(numElements)]
        variableArray = self.api.VariableArray(numElements + 1, compoundArray)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            variableArray.write(writer)

    @staticmethod
    def _writeVariableArrayToStream(writer, numElements):
        writer.writeBits(numElements, 8)
        for i in range(numElements):
            writer.writeBits(i, 32)
            writer.writeString("Name" + str(i))
