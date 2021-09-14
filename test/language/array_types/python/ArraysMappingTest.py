import unittest

import zserio

from testutils import getZserioApi

class ArraysMappingTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types.zs").arrays_mapping

    def testUnsignedIntegerArrays(self):
        arraysMapping = self.api.ArraysMapping()
        intArray = list(range(self.FIXED_ARRAY_LENGTH))

        arraysMapping.uint8_array = intArray
        arraysMapping.uint16_array = intArray
        arraysMapping.uint32_array = intArray
        arraysMapping.uint64_array = intArray

    def testSignedIntegerArrays(self):
        arraysMapping = self.api.ArraysMapping()
        intArray = list(range(self.FIXED_ARRAY_LENGTH))

        arraysMapping.int8_array = intArray
        arraysMapping.int16_array = intArray
        arraysMapping.int32_array = intArray
        arraysMapping.int64_array = intArray

    def testUnsignedBitfieldArrays(self):
        arraysMapping = self.api.ArraysMapping()
        intArray = list(range(self.FIXED_ARRAY_LENGTH))

        arraysMapping.bitfield8_array = intArray
        arraysMapping.bitfield16_array = intArray
        arraysMapping.bitfield32_array = intArray
        arraysMapping.bitfield63_array = intArray
        arraysMapping.uint8_value = 8
        arraysMapping.variable_bitfield_long_array = intArray
        arraysMapping.variable_bitfield_int_array = intArray
        arraysMapping.variable_bitfield_short_array = intArray
        arraysMapping.variable_bitfield_byte_array = intArray
        arraysMapping.length64 = 64
        arraysMapping.variable_bitfield64_array = intArray

    def testSignedBitfieldArrays(self):
        arraysMapping = self.api.ArraysMapping()
        intArray = list(range(self.FIXED_ARRAY_LENGTH))

        arraysMapping.intfield8_array = intArray
        arraysMapping.intfield16_array = intArray
        arraysMapping.intfield32_array = intArray
        arraysMapping.intfield64_array = intArray
        arraysMapping.uint8_value = 8
        arraysMapping.variable_intfield_long_array = intArray
        arraysMapping.variable_intfield_int_array = intArray
        arraysMapping.variable_intfield_short_array = intArray
        arraysMapping.variable_intfield_byte_array = intArray
        arraysMapping.length32 = 64
        arraysMapping.variable_intfield32_array = intArray

    def testFloatArrays(self):
        arraysMapping = self.api.ArraysMapping()
        floatArray = [i / (i + 1) for i in range(self.FIXED_ARRAY_LENGTH)]

        arraysMapping.float16_array = floatArray
        arraysMapping.float32_array = floatArray
        arraysMapping.float64_array = floatArray

    def testVariableUnsignedIntegerArrays(self):
        arraysMapping = self.api.ArraysMapping()
        intArray = list(range(self.FIXED_ARRAY_LENGTH))

        arraysMapping.varuint16_array = intArray
        arraysMapping.varuint32_array = intArray
        arraysMapping.varuint64_array = intArray
        arraysMapping.varuint_array = intArray
        arraysMapping.varsize_array = intArray

    def testVariableSignedIntegerArrays(self):
        arraysMapping = self.api.ArraysMapping()
        intArray = list(range(self.FIXED_ARRAY_LENGTH))

        arraysMapping.varint16_array = intArray
        arraysMapping.varint32_array = intArray
        arraysMapping.varint64_array = intArray
        arraysMapping.varint_array = intArray

    def testBoolArray(self):
        arraysMapping = self.api.ArraysMapping()
        boolArray = [i % 2 == 0 for i in range(self.FIXED_ARRAY_LENGTH)]

        arraysMapping.bool_array = boolArray

    def testStringArrays(self):
        arraysMapping = self.api.ArraysMapping()
        stringArray = ["Test" + str(i) for i in range(self.FIXED_ARRAY_LENGTH)]

        arraysMapping.string_array = stringArray

    def testExternArrays(self):
        arraysMapping = self.api.ArraysMapping()
        externArray = [zserio.BitBuffer(bytes([0xCD, 0xC0]), 10)
                       for i in range(self.FIXED_ARRAY_LENGTH)]
        arraysMapping.extern_array = externArray

    def testCompoundArray(self):
        arraysMapping = self.api.ArraysMapping()
        compoundArray = [self.api.TestStructure() for i in range(self.FIXED_ARRAY_LENGTH)]

        arraysMapping.compound_array = compoundArray

    def testEnumArray(self):
        arraysMapping = self.api.ArraysMapping()
        enumArray = [self.api.TestEnum(self.api.TestEnum.VALUE1) for i in range(self.FIXED_ARRAY_LENGTH)]

        arraysMapping.enum_array = enumArray

    def testBitmaskArray(self):
        arraysMapping = self.api.ArraysMapping()
        bitmaskArray = [self.api.TestBitmask.Values.MASK1 for i in range(self.FIXED_ARRAY_LENGTH)]

        arraysMapping.bitmask_array = bitmaskArray

    FIXED_ARRAY_LENGTH = 5
