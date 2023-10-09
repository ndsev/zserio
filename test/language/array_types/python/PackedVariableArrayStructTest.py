import os
import zserio

import ArrayTypes

from testutils import getApiDir

class PackedVariableArrayStructTest(ArrayTypes.TestCase):
    def testBitSizeOfLength1(self):
        self._checkBitSizeOf(self.VARIABLE_ARRAY_LENGTH1)

    def testBitSizeOfLength2(self):
        self._checkBitSizeOf(self.VARIABLE_ARRAY_LENGTH2)

    def testBitSizeOfLength3(self):
        self._checkBitSizeOf(self.VARIABLE_ARRAY_LENGTH3)

    def testBitSizeOfLength4(self):
        self._checkBitSizeOf(self.VARIABLE_ARRAY_LENGTH4)

    def testWriteReadLength1(self):
        self._checkWriteRead(self.VARIABLE_ARRAY_LENGTH1)

    def testWriteReadLength2(self):
        self._checkWriteRead(self.VARIABLE_ARRAY_LENGTH2)

    def testWriteReadLength3(self):
        self._checkWriteRead(self.VARIABLE_ARRAY_LENGTH3)

    def testWriteReadLength4(self):
        self._checkWriteRead(self.VARIABLE_ARRAY_LENGTH4)

    def testWriteReadFileLength1(self):
        self._checkWriteReadFile(self.VARIABLE_ARRAY_LENGTH1)

    def testWriteReadFileLength2(self):
        self._checkWriteReadFile(self.VARIABLE_ARRAY_LENGTH2)

    def testWriteReadFileLength3(self):
        self._checkWriteReadFile(self.VARIABLE_ARRAY_LENGTH3)

    def testWriteReadFileLength4(self):
        self._checkWriteReadFile(self.VARIABLE_ARRAY_LENGTH4)

    def _checkBitSizeOf(self, numElements):
        packedVariableArray = self._createPackedVariableArray(numElements)
        unpackedBitsizeOf = packedVariableArray.test_unpacked_array.bitsizeof()
        packedBitsizeOf = packedVariableArray.test_packed_array.bitsizeof()
        minCompressionRatio = 0.622
        self.assertTrue(unpackedBitsizeOf * minCompressionRatio > packedBitsizeOf, "Unpacked array has " +
                        str(unpackedBitsizeOf) + " bits, packed array has " + str(packedBitsizeOf) + " bits, " +
                        "compression ratio is " + str(packedBitsizeOf / unpackedBitsizeOf * 100) + "%!")

    def _checkWriteRead(self, numElements):
        packedVariableArray = self._createPackedVariableArray(numElements)
        bitBuffer = zserio.serialize(packedVariableArray)

        self.assertEqual(packedVariableArray.bitsizeof(), bitBuffer.bitsize)
        self.assertEqual(packedVariableArray.initialize_offsets(), bitBuffer.bitsize)

        readPackedVariableArray = zserio.deserialize(self.api.PackedVariableArray, bitBuffer)
        self.assertEqual(packedVariableArray, readPackedVariableArray)

    def _checkWriteReadFile(self, numElements):
        packedVariableArray = self._createPackedVariableArray(numElements)
        filename = self.BLOB_NAME_BASE + str(numElements) + ".blob"
        zserio.serialize_to_file(packedVariableArray, filename)

        readPackedVariableArray = zserio.deserialize_from_file(self.api.PackedVariableArray, filename)
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
        data = zserio.BitBuffer(bytes([0xCD, 0xC0]), 10)
        bytesData = bytes([0xCD, 0xC0])
        testChoice = (self.api.TestChoice(index, value16_=index) if index in (0, 2, 4) else
                      self.api.TestChoice(index, array32_=[index * 2, index * 2 + 1]) if index == 5 else
                      self.api.TestChoice(index, value32_=self.api.Value32(index * 2)))
        testUnion = (self.api.TestUnion(value16_=index) if (index % 2) == 0 else
                     self.api.TestUnion(array32_=[index * 2, index * 2 + 1]) if index == 5 else
                     self.api.TestUnion(value32_=self.api.Value32(index * 2)))
        testEnum = self.api.TestEnum.DARK_RED if (index % 2) == 0 else self.api.TestEnum.DARK_GREEN
        testBitmask = (self.api.TestBitmask.Values.READ if (index % 2) == 0 else
                       self.api.TestBitmask.Values.CREATE)
        testOptional = index if (index % 2) == 0 else None
        testDynamicBitfield = index % 3
        values = list(range(1, 18, 3))
        numValues = len(values)
        empties = [self.api.Empty()] * numValues

        return self.api.TestStructure(id_=index, name_=name, data_=data, bytes_data_=bytesData,
                                      test_choice_=testChoice, test_union_=testUnion,
                                      test_enum_=testEnum, test_bitmask_=testBitmask,
                                      test_optional_=testOptional, test_dynamic_bitfield_=testDynamicBitfield,
                                      num_values_=numValues, unpacked_values_=values, packed_values_=values,
                                      packed_empties_=empties)

    BLOB_NAME_BASE = os.path.join(getApiDir(os.path.dirname(__file__)), "packed_variable_array_struct_")
    VARIABLE_ARRAY_LENGTH1 = 25
    VARIABLE_ARRAY_LENGTH2 = 50
    VARIABLE_ARRAY_LENGTH3 = 100
    VARIABLE_ARRAY_LENGTH4 = 1000
