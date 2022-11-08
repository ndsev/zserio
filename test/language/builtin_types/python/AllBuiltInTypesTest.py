import sys
import unittest
import os
import struct

import zserio

from testutils import getZserioApi, getApiDir

class AllBuiltInTypesTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "builtin_types.zs").all_builtin_types

    def testUint8Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.uint8_type = zserio.limits.UINT8_MAX
        self.assertEqual(zserio.limits.UINT8_MAX, allBuiltInTypes.uint8_type)

    def testUint16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.uint16_type = zserio.limits.UINT16_MAX
        self.assertEqual(zserio.limits.UINT16_MAX, allBuiltInTypes.uint16_type)

    def testUint32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.uint32_type = zserio.limits.UINT32_MAX
        self.assertEqual(zserio.limits.UINT32_MAX, allBuiltInTypes.uint32_type)

    def testUint64Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.uint64_type = zserio.limits.UINT64_MAX
        self.assertEqual(zserio.limits.UINT64_MAX, allBuiltInTypes.uint64_type)

    def testInt8Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.int8_type = zserio.limits.INT8_MAX
        self.assertEqual(zserio.limits.INT8_MAX, allBuiltInTypes.int8_type)

    def testInt16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.int16_type = zserio.limits.INT16_MAX
        self.assertEqual(zserio.limits.INT16_MAX, allBuiltInTypes.int16_type)

    def testInt32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.int32_type = zserio.limits.INT32_MAX
        self.assertEqual(zserio.limits.INT32_MAX, allBuiltInTypes.int32_type)

    def testInt64Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.int64_type = zserio.limits.INT64_MAX
        self.assertEqual(zserio.limits.INT64_MAX, allBuiltInTypes.int64_type)

    def testBitField7Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield7Type = 0x7F
        allBuiltInTypes.bitfield7_type = maxBitfield7Type
        self.assertEqual(maxBitfield7Type, allBuiltInTypes.bitfield7_type)

    def testBitField8Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield8Type = zserio.limits.UINT8_MAX
        allBuiltInTypes.bitfield8_type = maxBitfield8Type
        self.assertEqual(maxBitfield8Type, allBuiltInTypes.bitfield8_type)

    def testBitField15Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield15Type = 0x7FFF
        allBuiltInTypes.bitfield15_type = maxBitfield15Type
        self.assertEqual(maxBitfield15Type, allBuiltInTypes.bitfield15_type)

    def testBitField16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield16Type = zserio.limits.UINT16_MAX
        allBuiltInTypes.bitfield16_type = maxBitfield16Type
        self.assertEqual(maxBitfield16Type, allBuiltInTypes.bitfield16_type)

    def testBitField31Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield31Type = 0x7FFFFFFF
        allBuiltInTypes.bitfield31_type = maxBitfield31Type
        self.assertEqual(maxBitfield31Type, allBuiltInTypes.bitfield31_type)

    def testBitField32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield32Type = zserio.limits.UINT32_MAX
        allBuiltInTypes.bitfield32_type = maxBitfield32Type
        self.assertEqual(maxBitfield32Type, allBuiltInTypes.bitfield32_type)

    def testBitField63Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield63Type = 0x7FFFFFFFFFFFFFFF
        allBuiltInTypes.bitfield63_type = maxBitfield63Type
        self.assertEqual(maxBitfield63Type, allBuiltInTypes.bitfield63_type)

    def testVariableBitfieldType(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxVariableBitfieldType = zserio.limits.UINT64_MAX
        allBuiltInTypes.variable_bitfield_type = maxVariableBitfieldType
        self.assertEqual(maxVariableBitfieldType, allBuiltInTypes.variable_bitfield_type)

    def testVariableBitField8Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxVariableBitfield8Type = zserio.limits.UINT8_MAX
        allBuiltInTypes.variable_bitfield8_type = maxVariableBitfield8Type
        self.assertEqual(maxVariableBitfield8Type, allBuiltInTypes.variable_bitfield8_type)

    def testIntField8Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.intfield8_type = zserio.limits.INT8_MAX
        self.assertEqual(zserio.limits.INT8_MAX, allBuiltInTypes.intfield8_type)

    def testIntField16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.intfield16_type = zserio.limits.INT16_MAX
        self.assertEqual(zserio.limits.INT16_MAX, allBuiltInTypes.intfield16_type)

    def testIntField32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.intfield32_type = zserio.limits.INT32_MAX
        self.assertEqual(zserio.limits.INT32_MAX, allBuiltInTypes.intfield32_type)

    def testIntField64Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.intfield64_type = zserio.limits.INT64_MAX
        self.assertEqual(zserio.limits.INT64_MAX, allBuiltInTypes.intfield64_type)

    def testVariableIntfieldType(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        variableIntfieldTypeMax = (1 << 13) - 1
        allBuiltInTypes.variable_intfield_type = variableIntfieldTypeMax
        self.assertEqual(variableIntfieldTypeMax, allBuiltInTypes.variable_intfield_type)

    def testVariableIntField8Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.variable_intfield8_type = zserio.limits.INT8_MAX
        self.assertEqual(zserio.limits.INT8_MAX, allBuiltInTypes.variable_intfield8_type)

    def testFloat16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.float16_type = sys.float_info.max
        self.assertEqual(sys.float_info.max, allBuiltInTypes.float16_type)

    def testFloat32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.float32_type = sys.float_info.max
        self.assertEqual(sys.float_info.max, allBuiltInTypes.float32_type)

    def testFloat64Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.float64_type = sys.float_info.max
        self.assertEqual(sys.float_info.max, allBuiltInTypes.float64_type)

    def testVaruint16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.varuint16_type = zserio.limits.VARUINT16_MAX
        self.assertEqual(zserio.limits.VARUINT16_MAX, allBuiltInTypes.varuint16_type)

    def testVaruint32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.varuint32_type = zserio.limits.VARUINT32_MAX
        self.assertEqual(zserio.limits.VARUINT32_MAX, allBuiltInTypes.varuint32_type)

    def testVaruint64Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.varuint64_type = zserio.limits.VARUINT64_MAX
        self.assertEqual(zserio.limits.VARUINT64_MAX, allBuiltInTypes.varuint64_type)

    def testVaruintType(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.varuint_type = zserio.limits.VARUINT_MIN
        self.assertEqual(zserio.limits.VARUINT_MIN, allBuiltInTypes.varuint_type)

        allBuiltInTypes.varuint_type = zserio.limits.VARUINT_MAX
        self.assertEqual(zserio.limits.VARUINT_MAX, allBuiltInTypes.varuint_type)

    def testVarsizeType(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.varsize_type = zserio.limits.VARSIZE_MIN
        self.assertEqual(zserio.limits.VARSIZE_MIN, allBuiltInTypes.varsize_type)

        allBuiltInTypes.varsize_type = zserio.limits.VARSIZE_MAX
        self.assertEqual(zserio.limits.VARSIZE_MAX, allBuiltInTypes.varsize_type)

    def testVarint16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.varint16_type = zserio.limits.VARINT16_MAX
        self.assertEqual(zserio.limits.VARINT16_MAX, allBuiltInTypes.varint16_type)

    def testVarint32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.varint32_type = zserio.limits.VARINT32_MAX
        self.assertEqual(zserio.limits.VARINT32_MAX, allBuiltInTypes.varint32_type)

    def testVarint64Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.varint64_type = zserio.limits.VARINT64_MAX
        self.assertEqual(zserio.limits.VARINT64_MAX, allBuiltInTypes.varint64_type)

    def testVarintType(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.varint_type = zserio.limits.VARINT_MIN
        self.assertEqual(zserio.limits.VARINT_MIN, allBuiltInTypes.varint_type)

        allBuiltInTypes.varint_type = zserio.limits.VARINT_MAX
        self.assertEqual(zserio.limits.VARINT_MAX, allBuiltInTypes.varint_type)

    def testBoolType(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.bool_type = True
        self.assertTrue(allBuiltInTypes.bool_type)
        allBuiltInTypes.bool_type = False
        self.assertFalse(allBuiltInTypes.bool_type)

    def testStringType(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        testString = "TEST"
        allBuiltInTypes.string_type = testString
        self.assertEqual(testString, allBuiltInTypes.string_type)

    def testExternType(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        testExtern = self._getExternalBitBuffer()
        allBuiltInTypes.extern_type = testExtern
        self.assertEqual(testExtern, allBuiltInTypes.extern_type)

    def testBytesType(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        testBytes = bytearray([1, 255])
        allBuiltInTypes.bytes_type = testBytes
        self.assertEqual(testBytes, allBuiltInTypes.bytes_type)

    def testBitSizeOf(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.bool_type = True
        allBuiltInTypes.uint8_type = 1
        allBuiltInTypes.uint16_type = zserio.limits.UINT16_MAX
        allBuiltInTypes.uint32_type = zserio.limits.UINT32_MAX
        allBuiltInTypes.uint64_type = zserio.limits.UINT64_MAX
        allBuiltInTypes.int8_type = zserio.limits.INT8_MAX
        allBuiltInTypes.int16_type = zserio.limits.INT16_MAX
        allBuiltInTypes.int32_type = zserio.limits.INT32_MAX
        allBuiltInTypes.int64_type = zserio.limits.INT64_MAX
        allBuiltInTypes.bitfield7_type = 0x7F
        allBuiltInTypes.bitfield8_type = zserio.limits.UINT8_MAX
        allBuiltInTypes.bitfield15_type = 0x7FFF
        allBuiltInTypes.bitfield16_type = zserio.limits.UINT16_MAX
        allBuiltInTypes.bitfield31_type = 0x7FFFFFFF
        allBuiltInTypes.bitfield32_type = zserio.limits.UINT32_MAX
        allBuiltInTypes.bitfield63_type = 0x7FFFFFFFFFFFFFFF
        allBuiltInTypes.variable_bitfield_type = 1
        allBuiltInTypes.variable_bitfield8_type = zserio.limits.UINT8_MAX
        allBuiltInTypes.intfield8_type = zserio.limits.INT8_MAX
        allBuiltInTypes.intfield16_type = zserio.limits.INT16_MAX
        allBuiltInTypes.intfield32_type = zserio.limits.INT32_MAX
        allBuiltInTypes.intfield64_type = zserio.limits.INT64_MAX
        variableIntfieldTypeMax = (1 << 13) - 1
        allBuiltInTypes.variable_intfield_type = variableIntfieldTypeMax
        allBuiltInTypes.variable_intfield8_type = zserio.limits.INT8_MAX
        allBuiltInTypes.float16_type = self.FLOAT32_MAX
        allBuiltInTypes.float32_type = self.FLOAT32_MAX
        allBuiltInTypes.float64_type = sys.float_info.max
        allBuiltInTypes.varuint16_type = zserio.limits.VARUINT16_MAX
        allBuiltInTypes.varuint32_type = zserio.limits.VARUINT32_MAX
        allBuiltInTypes.varuint64_type = zserio.limits.VARUINT64_MAX
        allBuiltInTypes.varuint_type = zserio.limits.VARUINT_MAX
        allBuiltInTypes.varsize_type = zserio.limits.VARSIZE_MAX
        allBuiltInTypes.varint16_type = zserio.limits.VARINT16_MAX
        allBuiltInTypes.varint32_type = zserio.limits.VARINT32_MAX
        allBuiltInTypes.varint64_type = zserio.limits.VARINT64_MAX
        allBuiltInTypes.varint_type = zserio.limits.VARINT_MAX
        allBuiltInTypes.string_type = "TEST"
        allBuiltInTypes.extern_type =  self._getExternalBitBuffer()
        allBuiltInTypes.bytes_type = bytearray([1, 255])
        expectedBitSizeOf = 1166
        self.assertEqual(expectedBitSizeOf, allBuiltInTypes.bitsizeof())

    def testReadWrite(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.bool_type = True
        allBuiltInTypes.uint8_type = 8
        allBuiltInTypes.uint16_type = zserio.limits.UINT16_MAX
        allBuiltInTypes.uint32_type = zserio.limits.UINT32_MAX
        allBuiltInTypes.uint64_type = zserio.limits.UINT64_MAX
        allBuiltInTypes.int8_type = zserio.limits.INT8_MAX
        allBuiltInTypes.int16_type = zserio.limits.INT16_MAX
        allBuiltInTypes.int32_type = zserio.limits.INT32_MAX
        allBuiltInTypes.int64_type = zserio.limits.INT64_MAX
        allBuiltInTypes.bitfield7_type = 0x7F
        allBuiltInTypes.bitfield8_type = zserio.limits.UINT8_MAX
        allBuiltInTypes.bitfield15_type = 0x7FFF
        allBuiltInTypes.bitfield16_type = zserio.limits.UINT16_MAX
        allBuiltInTypes.bitfield31_type = 0x7FFFFFFF
        allBuiltInTypes.bitfield32_type = zserio.limits.UINT32_MAX
        allBuiltInTypes.bitfield63_type = 0x7FFFFFFFFFFFFFFF
        allBuiltInTypes.variable_bitfield_type = zserio.limits.UINT8_MAX
        allBuiltInTypes.variable_bitfield8_type = zserio.limits.UINT8_MAX
        allBuiltInTypes.intfield8_type = zserio.limits.INT8_MAX
        allBuiltInTypes.intfield16_type = zserio.limits.INT16_MAX
        allBuiltInTypes.intfield32_type = zserio.limits.INT32_MAX
        allBuiltInTypes.intfield64_type = zserio.limits.INT64_MAX
        variableIntfieldTypeMax = (1 << 13) - 1
        allBuiltInTypes.variable_intfield_type = variableIntfieldTypeMax
        allBuiltInTypes.variable_intfield8_type = zserio.limits.INT8_MAX
        allBuiltInTypes.float16_type = 1.0
        allBuiltInTypes.float32_type = self.FLOAT32_MAX
        allBuiltInTypes.float64_type = sys.float_info.max
        allBuiltInTypes.varuint16_type = zserio.limits.VARUINT16_MAX
        allBuiltInTypes.varuint32_type = zserio.limits.VARUINT32_MAX
        allBuiltInTypes.varuint64_type = zserio.limits.VARUINT64_MAX
        allBuiltInTypes.varuint_type = zserio.limits.VARUINT_MAX
        allBuiltInTypes.varsize_type = zserio.limits.VARSIZE_MAX
        allBuiltInTypes.varint16_type = zserio.limits.VARINT16_MAX
        allBuiltInTypes.varint32_type = zserio.limits.VARINT32_MAX
        allBuiltInTypes.varint64_type = zserio.limits.VARINT64_MAX
        allBuiltInTypes.varint_type = zserio.limits.VARINT_MAX
        allBuiltInTypes.string_type = "TEST"
        allBuiltInTypes.extern_type = self._getExternalBitBuffer()
        allBuiltInTypes.bytes_type = bytearray([1, 255])

        zserio.serialize_to_file(allBuiltInTypes, self.BLOB_NAME)

        readAllBuiltInTypes = zserio.deserialize_from_file(self.api.AllBuiltInTypes, self.BLOB_NAME)
        self.assertEqual(allBuiltInTypes, readAllBuiltInTypes)

    def _getExternalBitBuffer(self):
        externalStructure = self.api.ExternalStructure(value_=0xCD, rest_=0x03)
        writer = zserio.BitStreamWriter()
        externalStructure.write(writer)

        return zserio.BitBuffer(writer.byte_array, writer.bitposition)

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)), "all_builtin_types.blob")
    FLOAT32_MAX = struct.unpack('>f', bytes(b'\x7f\x7f\xff\xff'))[0]
