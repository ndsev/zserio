import os
import zserio

import ArrayTypes

from testutils import getApiDir


class VariableArrayStructCastInt8Test(ArrayTypes.TestCase):
    def testBitSizeOf(self):
        numElements = 33
        compoundArray = [self.api.TestStructure(i, "Name" + str(i)) for i in range(numElements)]
        variableArray = self.api.VariableArray(numElements, compoundArray)
        bitPosition = 2
        numOneNumberIndexes = 10
        expectedBitSize = (1 + numElements * (4 + 7) - numOneNumberIndexes) * 8
        self.assertEqual(expectedBitSize, variableArray.bitsizeof(bitPosition))

    def testInitializeOffsets(self):
        numElements = 33
        compoundArray = [self.api.TestStructure(i, "Name" + str(i)) for i in range(numElements)]
        variableArray = self.api.VariableArray(numElements, compoundArray)
        bitPosition = 2
        numOneNumberIndexes = 10
        expectedEndBitPosition = bitPosition + (1 + numElements * (4 + 7) - numOneNumberIndexes) * 8
        self.assertEqual(expectedEndBitPosition, variableArray.initialize_offsets(bitPosition))

    def testRead(self):
        numElements = 59
        writer = zserio.BitStreamWriter()
        VariableArrayStructCastInt8Test._writeVariableArrayToStream(writer, numElements)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        variableArray = self.api.VariableArray.from_reader(reader)

        self.assertEqual(numElements, variableArray.num_elements)
        compoundArray = variableArray.compound_array
        self.assertEqual(numElements, len(compoundArray))
        for i in range(numElements):
            testStructure = compoundArray[i]
            self.assertEqual(i, testStructure.id)
            self.assertTrue(testStructure.name == "Name" + str(i))

    def testWriteRead(self):
        numElements = 33
        compoundArray = [self.api.TestStructure(i, "Name" + str(i)) for i in range(numElements)]
        variableArray = self.api.VariableArray(numElements, compoundArray)
        bitBuffer = zserio.serialize(variableArray)

        self.assertEqual(variableArray.bitsizeof(), bitBuffer.bitsize)
        self.assertEqual(variableArray.initialize_offsets(), bitBuffer.bitsize)

        readVariableArray = zserio.deserialize(self.api.VariableArray, bitBuffer)
        self.assertEqual(numElements, readVariableArray.num_elements)
        readCompoundArray = readVariableArray.compound_array
        self.assertEqual(numElements, len(readCompoundArray))
        for i in range(numElements):
            readTestStructure = readCompoundArray[i]
            self.assertEqual(i, readTestStructure.id)
            self.assertTrue(readTestStructure.name == "Name" + str(i))

    def testWriteReadFile(self):
        numElements = 33
        compoundArray = [self.api.TestStructure(i, "Name" + str(i)) for i in range(numElements)]
        variableArray = self.api.VariableArray(numElements, compoundArray)
        zserio.serialize_to_file(variableArray, self.BLOB_NAME)

        readVariableArray = zserio.deserialize_from_file(self.api.VariableArray, self.BLOB_NAME)
        self.assertEqual(numElements, readVariableArray.num_elements)
        readCompoundArray = readVariableArray.compound_array
        self.assertEqual(numElements, len(readCompoundArray))
        for i in range(numElements):
            readTestStructure = readCompoundArray[i]
            self.assertEqual(i, readTestStructure.id)
            self.assertTrue(readTestStructure.name == "Name" + str(i))

    def testWriteWrongArray(self):
        numElements = 33
        compoundArray = [self.api.TestStructure(i, "Name" + str(i)) for i in range(numElements)]
        variableArray = self.api.VariableArray(numElements + 1, compoundArray)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            variableArray.write(writer)

    @staticmethod
    def _writeVariableArrayToStream(writer, numElements):
        writer.write_bits(numElements, 8)
        for i in range(numElements):
            writer.write_bits(i, 32)
            writer.write_string("Name" + str(i))

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)), "variable_array_struct_cast_int8.blob")
