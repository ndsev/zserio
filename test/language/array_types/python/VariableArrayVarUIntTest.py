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
        VariableArrayVarUIntTest._writeVariableArrayToStream(writer, numElements)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        variableArray = self.api.VariableArray.from_reader(reader)

        self.assertEqual(numElements, variableArray.num_elements)
        compoundArray = variableArray.compound_array
        self.assertEqual(numElements, len(compoundArray))
        for i in range(numElements):
            testStructure = compoundArray[i]
            self.assertEqual(i, testStructure.id)
            self.assertTrue(testStructure.name == "Name" + str(i))

    def testWrite(self):
        numElements = 33
        compoundArray = [self.api.TestStructure(id_=i, name_="Name" + str(i)) for i in range(numElements)]
        variableArray = self.api.VariableArray(num_elements_=numElements, compound_array_=compoundArray)
        bitBuffer = zserio.serialize(variableArray)
        readVariableArray = zserio.deserialize(self.api.VariableArray, bitBuffer)
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
