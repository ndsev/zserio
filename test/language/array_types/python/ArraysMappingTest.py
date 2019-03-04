import unittest

from testutils import getZserioApi

class ArraysMappingTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "array_types.zs").arrays_mapping

    def testUnsignedIntegerArrays(self):
        arraysMapping = self.api.ArraysMapping()
        intArray = [i for i in range(self.FIXED_ARRAY_LENGTH)]

        arraysMapping.setUint8Array(intArray)
        arraysMapping.setUint16Array(intArray)
        arraysMapping.setUint32Array(intArray)
        arraysMapping.setUint64Array(intArray)

    def testSignedIntegerArrays(self):
        arraysMapping = self.api.ArraysMapping()
        intArray = [i for i in range(self.FIXED_ARRAY_LENGTH)]

        arraysMapping.setInt8Array(intArray)
        arraysMapping.setInt16Array(intArray)
        arraysMapping.setInt32Array(intArray)
        arraysMapping.setInt64Array(intArray)

    def testUnsignedBitfieldArrays(self):
        arraysMapping = self.api.ArraysMapping()
        intArray = [i for i in range(self.FIXED_ARRAY_LENGTH)]

        arraysMapping.setBitfield8Array(intArray)
        arraysMapping.setBitfield16Array(intArray)
        arraysMapping.setBitfield32Array(intArray)
        arraysMapping.setBitfield63Array(intArray)
        arraysMapping.setUint8Value(8)
        arraysMapping.setVariableBitfieldLongArray(intArray)
        arraysMapping.setVariableBitfieldIntArray(intArray)
        arraysMapping.setVariableBitfieldShortArray(intArray)
        arraysMapping.setVariableBitfieldByteArray(intArray)

    def testSignedBitfieldArrays(self):
        arraysMapping = self.api.ArraysMapping()
        intArray = [i for i in range(self.FIXED_ARRAY_LENGTH)]

        arraysMapping.setIntfield8Array(intArray)
        arraysMapping.setIntfield16Array(intArray)
        arraysMapping.setIntfield32Array(intArray)
        arraysMapping.setIntfield64Array(intArray)
        arraysMapping.setUint8Value(8)
        arraysMapping.setVariableIntfieldLongArray(intArray)
        arraysMapping.setVariableIntfieldIntArray(intArray)
        arraysMapping.setVariableIntfieldShortArray(intArray)
        arraysMapping.setVariableIntfieldByteArray(intArray)

    def testFloatArrays(self):
        arraysMapping = self.api.ArraysMapping()
        floatArray = [i / (i + 1) for i in range(self.FIXED_ARRAY_LENGTH)]

        arraysMapping.setFloat16Array(floatArray)
        arraysMapping.setFloat32Array(floatArray)
        arraysMapping.setFloat64Array(floatArray)

    def testVariableUnsignedIntegerArrays(self):
        arraysMapping = self.api.ArraysMapping()
        intArray = [i for i in range(self.FIXED_ARRAY_LENGTH)]

        arraysMapping.setVaruint16Array(intArray)
        arraysMapping.setVaruint32Array(intArray)
        arraysMapping.setVaruint64Array(intArray)
        arraysMapping.setVaruintArray(intArray)

    def testVariableSignedIntegerArrays(self):
        arraysMapping = self.api.ArraysMapping()
        intArray = [i for i in range(self.FIXED_ARRAY_LENGTH)]

        arraysMapping.setVarint16Array(intArray)
        arraysMapping.setVarint32Array(intArray)
        arraysMapping.setVarint64Array(intArray)
        arraysMapping.setVarintArray(intArray)

    def testBoolArray(self):
        arraysMapping = self.api.ArraysMapping()
        boolArray = [i % 2 == 0 for i in range(self.FIXED_ARRAY_LENGTH)]

        arraysMapping.setBoolArray(boolArray)

    def testStringArrays(self):
        arraysMapping = self.api.ArraysMapping()
        stringArray = ["Test" + str(i) for i in range(self.FIXED_ARRAY_LENGTH)]

        arraysMapping.setStringArray(stringArray)

    def testCompoundArray(self):
        arraysMapping = self.api.ArraysMapping()
        compoundArray = [self.api.TestStructure() for i in range(self.FIXED_ARRAY_LENGTH)]

        arraysMapping.setCompoundArray(compoundArray)

    def testEnumArray(self):
        arraysMapping = self.api.ArraysMapping()
        enumArray = [self.api.TestEnum(self.api.TestEnum.VALUE1) for i in range(self.FIXED_ARRAY_LENGTH)]

        arraysMapping.setEnumArray(enumArray)

    FIXED_ARRAY_LENGTH = 5
