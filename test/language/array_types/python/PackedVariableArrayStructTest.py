import unittest
import zserio

from testutils import getZserioApi

class PackedVariableArrayStructTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types.zs").packed_variable_array_struct

    def testBitSizeOfLength1(self):
        self._checkBitSizeOf(self.VARIABLE_ARRAY_LENGTH1)

    def testBitSizeOfLength2(self):
        self._checkBitSizeOf(self.VARIABLE_ARRAY_LENGTH2)

    def testBitSizeOfLength3(self):
        self._checkBitSizeOf(self.VARIABLE_ARRAY_LENGTH3)

    def testBitSizeOfLength4(self):
        self._checkBitSizeOf(self.VARIABLE_ARRAY_LENGTH4)

    def testWriteReadfLength1(self):
        self._checkWriteRead(self.VARIABLE_ARRAY_LENGTH1)

    def testWriteReadfLength2(self):
        self._checkWriteRead(self.VARIABLE_ARRAY_LENGTH2)

    def testWriteReadfLength3(self):
        self._checkWriteRead(self.VARIABLE_ARRAY_LENGTH3)

    def testWriteReadfLength4(self):
        self._checkWriteRead(self.VARIABLE_ARRAY_LENGTH4)

    def _checkBitSizeOf(self, numElements):
        packedVariableArray = self._createPackedVariableArray(numElements)
        unpackedBitsizeOf = packedVariableArray.test_unpacked_array.bitsizeof()
        packedBitsizeOf = packedVariableArray.test_packed_array.bitsizeof()
        minCompressionRatio = 0.7
        self.assertTrue(unpackedBitsizeOf * minCompressionRatio > packedBitsizeOf, "Unpacked array has " +
                        str(unpackedBitsizeOf) + " bits, packed array has " + str(packedBitsizeOf) + " bits, " +
                        "compression ratio is " + str(packedBitsizeOf / unpackedBitsizeOf * 100) + "%!")

    def _checkWriteRead(self, numElements):
        packedVariableArray = self._createPackedVariableArray(numElements)
        bitBuffer = zserio.serialize(packedVariableArray)
        self.assertEqual(packedVariableArray.bitsizeof(), bitBuffer.bitsize)
        self.assertEqual(packedVariableArray.initialize_offsets(0), bitBuffer.bitsize)
        readPackedVariableArray = zserio.deserialize(self.api.PackedVariableArray, bitBuffer)
        self.assertEqual(packedVariableArray, readPackedVariableArray)

    def _createPackedVariableArray(self, numElements):
        testStructureArray = self._createTestStructureArray(numElements)
        testUnpackedArray = self.api.TestUnpackedArray(numElements, testStructureArray)
        testPackedArray = self.api.TestPackedArray(numElements, testStructureArray)

        return self.api.PackedVariableArray(numElements, testUnpackedArray, testPackedArray)

    def _createTestStructureArray(self, numElements):
        testStructureArray = []
        for i in range(numElements):
            testStructureArray.append(self._createTestStructure(i))

        return testStructureArray

    def _createTestStructure(self, index):
        name = "name" + str(index)
        testChoice = (self.api.TestChoice(index, value16_=index) if index in (0, 2, 4) else
                      self.api.TestChoice(index, value32_=index * 2))
        testUnion = (self.api.TestUnion(value16_=index) if (index % 2) == 0 else
                     self.api.TestUnion(value32_=index * 2))
        testEnum = self.api.TestEnum.DARK_RED if (index % 2) == 0 else self.api.TestEnum.DARK_GREEN
        testBitmask = (self.api.TestBitmask.Values.READ if (index % 2) == 0 else
                       self.api.TestBitmask.Values.CREATE)
        testOptional = index if (index % 2) == 0 else None
        values = list(range(1, 18, 3))
        numValues = len(values)

        return self.api.TestStructure(id_=index, name_=name, test_choice_=testChoice, test_union_=testUnion,
            test_enum_=testEnum, test_bitmask_=testBitmask, test_optional_=testOptional, num_values_=numValues,
            unpacked_values_=values, packed_values_=values)

    VARIABLE_ARRAY_LENGTH1 = 10
    VARIABLE_ARRAY_LENGTH2 = 50
    VARIABLE_ARRAY_LENGTH3 = 100
    VARIABLE_ARRAY_LENGTH4 = 1000
