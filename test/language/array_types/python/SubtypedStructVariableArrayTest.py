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
        self.assertEqual(expectedBitSize, subtypedStructVariableArray.bitsizeof(bitPosition))

    def testInitializeOffsets(self):
        numElements = 33
        compoundArray = [self.api.ArrayElement(id_=i, name_="Name" + str(i)) for i in range(numElements)]
        subtypedStructVariableArray = self.api.SubtypedStructVariableArray(numElements, compoundArray)
        bitPosition = 2
        numOneNumberIndexes = 10
        expectedEndBitPosition = bitPosition + (1 + numElements * (4 + 7) - numOneNumberIndexes) * 8
        self.assertEqual(expectedEndBitPosition, subtypedStructVariableArray.initialize_offsets(bitPosition))

    def testRead(self):
        numElements = 59
        writer = zserio.BitStreamWriter()
        SubtypedStructVariableArrayTest._writeSubtypedStructVariableArrayToStream(writer, numElements)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        subtypedStructVariableArray = self.api.SubtypedStructVariableArray.from_reader(reader)

        self.assertEqual(numElements, subtypedStructVariableArray.num_elements)
        compoundArray = subtypedStructVariableArray.compound_array
        self.assertEqual(numElements, len(compoundArray))
        for i in range(numElements):
            testStructure = compoundArray[i]
            self.assertEqual(i, testStructure.id)
            self.assertTrue(testStructure.name == "Name" + str(i))

    def testWrite(self):
        numElements = 33
        compoundArray = [self.api.ArrayElement(i, "Name" + str(i)) for i in range(numElements)]
        subtypedStructVariableArray = self.api.SubtypedStructVariableArray(numElements, compoundArray)
        bitBuffer = zserio.serialize(subtypedStructVariableArray)
        readSubtypedStructVariableArray = zserio.deserialize(self.api.SubtypedStructVariableArray, bitBuffer)
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
        subtypedStructVariableArray = self.api.SubtypedStructVariableArray(numElements + 1, compoundArray)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            subtypedStructVariableArray.write(writer)

    @staticmethod
    def _writeSubtypedStructVariableArrayToStream(writer, numElements):
        writer.write_signed_bits(numElements, 8)
        for i in range(numElements):
            writer.write_bits(i, 32)
            writer.write_string("Name" + str(i))
