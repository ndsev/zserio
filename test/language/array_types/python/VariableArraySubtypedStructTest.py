import unittest
import zserio
import os

from testutils import getZserioApi, getApiDir

class VariableArraySubtypedStructTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types.zs").variable_array_subtyped_struct

    def testBitSizeOf(self):
        numElements = 33
        compoundArray = [self.api.ArrayElement(i, "Name" + str(i)) for i in range(numElements)]
        variableArray = self.api.VariableArray(numElements, compoundArray)
        bitPosition = 2
        numOneNumberIndexes = 10
        expectedBitSize = (1 + numElements * (4 + 7) - numOneNumberIndexes) * 8
        self.assertEqual(expectedBitSize, variableArray.bitsizeof(bitPosition))

    def testInitializeOffsets(self):
        numElements = 33
        compoundArray = [self.api.ArrayElement(id_=i, name_="Name" + str(i)) for i in range(numElements)]
        variableArray = self.api.VariableArray(numElements, compoundArray)
        bitPosition = 2
        numOneNumberIndexes = 10
        expectedEndBitPosition = bitPosition + (1 + numElements * (4 + 7) - numOneNumberIndexes) * 8
        self.assertEqual(expectedEndBitPosition, variableArray.initialize_offsets(bitPosition))

    def testRead(self):
        numElements = 59
        writer = zserio.BitStreamWriter()
        VariableArraySubtypedStructTest._writeVariableArrayToStream(writer, numElements)
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
        compoundArray = [self.api.ArrayElement(i, "Name" + str(i)) for i in range(numElements)]
        variableArray = self.api.VariableArray(numElements, compoundArray)
        bitBuffer = zserio.serialize(variableArray)

        self.assertEqual(variableArray.bitsizeof(), bitBuffer.bitsize)
        self.assertEqual(variableArray.initialize_offsets(0), bitBuffer.bitsize)

        readSubtypedStructVariableArray = zserio.deserialize(self.api.VariableArray, bitBuffer)
        self.assertEqual(numElements, readSubtypedStructVariableArray.num_elements)
        readCompoundArray = readSubtypedStructVariableArray.compound_array
        self.assertEqual(numElements, len(readCompoundArray))
        for i in range(numElements):
            readArrayElement = readCompoundArray[i]
            self.assertEqual(i, readArrayElement.id)
            self.assertTrue(readArrayElement.name == "Name" + str(i))

    def testWriteReadFile(self):
        numElements = 33
        compoundArray = [self.api.ArrayElement(i, "Name" + str(i)) for i in range(numElements)]
        variableArray = self.api.VariableArray(numElements, compoundArray)
        zserio.serialize_to_file(variableArray, self.BLOB_NAME)

        readSubtypedStructVariableArray = zserio.deserialize_from_file(self.api.VariableArray, self.BLOB_NAME)
        self.assertEqual(numElements, readSubtypedStructVariableArray.num_elements)
        readCompoundArray = readSubtypedStructVariableArray.compound_array
        self.assertEqual(numElements, len(readCompoundArray))
        for i in range(numElements):
            readArrayElement = readCompoundArray[i]
            self.assertEqual(i, readArrayElement.id)
            self.assertTrue(readArrayElement.name == "Name" + str(i))

    def testWriteWrongArray(self):
        numElements = 33
        compoundArray = [self.api.ArrayElement(i, name_="Name" + str(i)) for i in range(numElements)]
        variableArray = self.api.VariableArray(numElements + 1, compoundArray)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            variableArray.write(writer)

    @staticmethod
    def _writeVariableArrayToStream(writer, numElements):
        writer.write_signed_bits(numElements, 8)
        for i in range(numElements):
            writer.write_bits(i, 32)
            writer.write_string("Name" + str(i))

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)), "variable_array_subtyped_struct.blob")
