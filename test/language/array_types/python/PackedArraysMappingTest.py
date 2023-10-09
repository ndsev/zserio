import ArrayTypes

class PackedArraysMappingTest(ArrayTypes.TestCase):
    def testUnsignedIntegerArrays(self):
        packedArraysMapping = self.api.PackedArraysMapping()
        intArray = list(range(self.FIXED_ARRAY_LENGTH))

        packedArraysMapping.uint8_array = intArray
        packedArraysMapping.uint16_array = intArray
        packedArraysMapping.uint32_array = intArray
        packedArraysMapping.uint64_array = intArray

    def testSignedIntegerArrays(self):
        packedArraysMapping = self.api.PackedArraysMapping()
        intArray = list(range(self.FIXED_ARRAY_LENGTH))

        packedArraysMapping.int8_array = intArray
        packedArraysMapping.int16_array = intArray
        packedArraysMapping.int32_array = intArray
        packedArraysMapping.int64_array = intArray

    def testUnsignedBitfieldArrays(self):
        packedArraysMapping = self.api.PackedArraysMapping()
        intArray = list(range(self.FIXED_ARRAY_LENGTH))

        packedArraysMapping.bitfield8_array = intArray
        packedArraysMapping.bitfield16_array = intArray
        packedArraysMapping.bitfield32_array = intArray
        packedArraysMapping.bitfield63_array = intArray
        packedArraysMapping.uint8_value = 8
        packedArraysMapping.variable_bitfield_long_array = intArray

    def testSignedBitfieldArrays(self):
        packedArraysMapping = self.api.PackedArraysMapping()
        intArray = list(range(self.FIXED_ARRAY_LENGTH))

        packedArraysMapping.intfield8_array = intArray
        packedArraysMapping.intfield16_array = intArray
        packedArraysMapping.intfield32_array = intArray
        packedArraysMapping.intfield64_array = intArray
        packedArraysMapping.uint8_value = 8
        packedArraysMapping.variable_intfield_long_array = intArray

    def testVariableUnsignedIntegerArrays(self):
        packedArraysMapping = self.api.PackedArraysMapping()
        intArray = list(range(self.FIXED_ARRAY_LENGTH))

        packedArraysMapping.varuint16_array = intArray
        packedArraysMapping.varuint32_array = intArray
        packedArraysMapping.varuint64_array = intArray
        packedArraysMapping.varuint_array = intArray
        packedArraysMapping.varsize_array = intArray

    def testVariableSignedIntegerArrays(self):
        packedArraysMapping = self.api.PackedArraysMapping()
        intArray = list(range(self.FIXED_ARRAY_LENGTH))

        packedArraysMapping.varint16_array = intArray
        packedArraysMapping.varint32_array = intArray
        packedArraysMapping.varint64_array = intArray
        packedArraysMapping.varint_array = intArray

    def testCompoundArray(self):
        packedArraysMapping = self.api.PackedArraysMapping()
        compoundArray = [self.api.TestStructure() for i in range(self.FIXED_ARRAY_LENGTH)]

        packedArraysMapping.compound_array = compoundArray

    def testEnumArray(self):
        packedArraysMapping = self.api.PackedArraysMapping()
        enumArray = [self.api.TestEnum(self.api.TestEnum.VALUE1) for i in range(self.FIXED_ARRAY_LENGTH)]

        packedArraysMapping.enum_array = enumArray

    def testBitmaskArray(self):
        packedArraysMapping = self.api.PackedArraysMapping()
        bitmaskArray = [self.api.TestBitmask.Values.MASK1 for i in range(self.FIXED_ARRAY_LENGTH)]

        packedArraysMapping.bitmask_array = bitmaskArray

    FIXED_ARRAY_LENGTH = 5
