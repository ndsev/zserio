import sys
import unittest

import zserio

from testutils import getZserioApi

class AllBuiltInTypesTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "builtin_types.zs").all_builtin_types

    def testUint8Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.uint8type = zserio.limits.UINT8_MAX
        self.assertEqual(zserio.limits.UINT8_MAX, allBuiltInTypes.uint8type)

    def testUint16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.uint16type = zserio.limits.UINT16_MAX
        self.assertEqual(zserio.limits.UINT16_MAX, allBuiltInTypes.uint16type)

    def testUint32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.uint32Type = zserio.limits.UINT32_MAX
        self.assertEqual(zserio.limits.UINT32_MAX, allBuiltInTypes.uint32Type)

    def testUint64Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.uint64type = zserio.limits.UINT64_MAX
        self.assertEqual(zserio.limits.UINT64_MAX, allBuiltInTypes.uint64type)

    def testInt8Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.int8type = zserio.limits.INT8_MAX
        self.assertEqual(zserio.limits.INT8_MAX, allBuiltInTypes.int8type)

    def testInt16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.int16type = zserio.limits.INT16_MAX
        self.assertEqual(zserio.limits.INT16_MAX, allBuiltInTypes.int16type)

    def testInt32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.int32type = zserio.limits.INT32_MAX
        self.assertEqual(zserio.limits.INT32_MAX, allBuiltInTypes.int32type)

    def testInt64Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.int64type = zserio.limits.INT64_MAX
        self.assertEqual(zserio.limits.INT64_MAX, allBuiltInTypes.int64type)

    def testBitField7Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield7Type = 0x7F
        allBuiltInTypes.bitfield7type = maxBitfield7Type
        self.assertEqual(maxBitfield7Type, allBuiltInTypes.bitfield7type)

    def testBitField8Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield8Type = zserio.limits.UINT8_MAX
        allBuiltInTypes.bitfield8type = maxBitfield8Type
        self.assertEqual(maxBitfield8Type, allBuiltInTypes.bitfield8type)

    def testBitField15Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield15Type = 0x7FFF
        allBuiltInTypes.bitfield15_type = maxBitfield15Type
        self.assertEqual(maxBitfield15Type, allBuiltInTypes.bitfield15_type)

    def testBitField16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield16Type = zserio.limits.UINT16_MAX
        allBuiltInTypes.bitfield16type = maxBitfield16Type
        self.assertEqual(maxBitfield16Type, allBuiltInTypes.bitfield16type)

    def testBitField31Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield31Type = 0x7FFFFFFF
        allBuiltInTypes.bitfield31_type = maxBitfield31Type
        self.assertEqual(maxBitfield31Type, allBuiltInTypes.bitfield31_type)

    def testBitField32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield32Type = zserio.limits.UINT32_MAX
        allBuiltInTypes.bitfield32type = maxBitfield32Type
        self.assertEqual(maxBitfield32Type, allBuiltInTypes.bitfield32type)

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
        allBuiltInTypes.variable_bitfield8type = maxVariableBitfield8Type
        self.assertEqual(maxVariableBitfield8Type, allBuiltInTypes.variable_bitfield8type)

    def testIntField8Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.intfield8type = zserio.limits.INT8_MAX
        self.assertEqual(zserio.limits.INT8_MAX, allBuiltInTypes.intfield8type)

    def testIntField16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.intfield16type = zserio.limits.INT16_MAX
        self.assertEqual(zserio.limits.INT16_MAX, allBuiltInTypes.intfield16type)

    def testIntField32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.intfield32type = zserio.limits.INT32_MAX
        self.assertEqual(zserio.limits.INT32_MAX, allBuiltInTypes.intfield32type)

    def testIntField64Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.intfield64type = zserio.limits.INT64_MAX
        self.assertEqual(zserio.limits.INT64_MAX, allBuiltInTypes.intfield64type)

    def testVariableIntfieldType(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.variable_intfield_type = zserio.limits.INT16_MAX
        self.assertEqual(zserio.limits.INT16_MAX, allBuiltInTypes.variable_intfield_type)

    def testVariableIntField8Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.variable_intfield8type = zserio.limits.INT8_MAX
        self.assertEqual(zserio.limits.INT8_MAX, allBuiltInTypes.variable_intfield8type)

    def testFloat16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.float16type = sys.float_info.max
        self.assertEqual(sys.float_info.max, allBuiltInTypes.float16type)

    def testFloat32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.float32type = sys.float_info.max
        self.assertEqual(sys.float_info.max, allBuiltInTypes.float32type)

    def testFloat64Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.float64type = sys.float_info.max
        self.assertEqual(sys.float_info.max, allBuiltInTypes.float64type)

    def testVaruint16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.varuint16type = zserio.limits.VARUINT16_MAX
        self.assertEqual(zserio.limits.VARUINT16_MAX, allBuiltInTypes.varuint16type)

    def testVaruint32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.varuint32type = zserio.limits.VARUINT32_MAX
        self.assertEqual(zserio.limits.VARUINT32_MAX, allBuiltInTypes.varuint32type)

    def testVaruint64Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.varuint64type = zserio.limits.VARUINT64_MAX
        self.assertEqual(zserio.limits.VARUINT64_MAX, allBuiltInTypes.varuint64type)

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
        allBuiltInTypes.varint16type = zserio.limits.VARINT16_MAX
        self.assertEqual(zserio.limits.VARINT16_MAX, allBuiltInTypes.varint16type)

    def testVarint32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.varint32type = zserio.limits.VARINT32_MAX
        self.assertEqual(zserio.limits.VARINT32_MAX, allBuiltInTypes.varint32type)

    def testVarint64Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.varint64type = zserio.limits.VARINT64_MAX
        self.assertEqual(zserio.limits.VARINT64_MAX, allBuiltInTypes.varint64type)

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

    def testBitSizeOf(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.bool_type = True
        allBuiltInTypes.uint8type = 1
        allBuiltInTypes.uint16type = zserio.limits.UINT16_MAX
        allBuiltInTypes.uint32type = zserio.limits.UINT32_MAX
        allBuiltInTypes.uint64type = zserio.limits.UINT64_MAX
        allBuiltInTypes.int8type = zserio.limits.INT8_MAX
        allBuiltInTypes.int16type = zserio.limits.INT16_MAX
        allBuiltInTypes.int32type = zserio.limits.INT32_MAX
        allBuiltInTypes.int64type = zserio.limits.INT64_MAX
        allBuiltInTypes.bitfield7type = 0x7F
        allBuiltInTypes.bitfield8type = zserio.limits.UINT8_MAX
        allBuiltInTypes.bitfield15_type = 0x7FFF
        allBuiltInTypes.bitfield16type = zserio.limits.UINT16_MAX
        allBuiltInTypes.bitfield31_type = 0x7FFFFFFF
        allBuiltInTypes.bitfield32type = zserio.limits.UINT32_MAX
        allBuiltInTypes.bitfield63_type = 0x7FFFFFFFFFFFFFFF
        allBuiltInTypes.variable_bitfield_type = 1
        allBuiltInTypes.variable_bitfield8type = zserio.limits.UINT8_MAX
        allBuiltInTypes.intfield8type = zserio.limits.INT8_MAX
        allBuiltInTypes.intfield16type = zserio.limits.INT16_MAX
        allBuiltInTypes.intfield32type = zserio.limits.INT32_MAX
        allBuiltInTypes.intfield64type = zserio.limits.INT64_MAX
        allBuiltInTypes.variable_intfield_type = 1
        allBuiltInTypes.variable_intfield8type = zserio.limits.INT8_MAX
        allBuiltInTypes.float16type = sys.float_info.max
        allBuiltInTypes.float32type = sys.float_info.max
        allBuiltInTypes.float64type = sys.float_info.max
        allBuiltInTypes.varuint16type = zserio.limits.VARUINT16_MAX
        allBuiltInTypes.varuint32type = zserio.limits.VARUINT32_MAX
        allBuiltInTypes.varuint64type = zserio.limits.VARUINT64_MAX
        allBuiltInTypes.varuint_type = zserio.limits.VARUINT_MAX
        allBuiltInTypes.varsize_type = zserio.limits.VARSIZE_MAX
        allBuiltInTypes.varint16type = zserio.limits.VARINT16_MAX
        allBuiltInTypes.varint32type = zserio.limits.VARINT32_MAX
        allBuiltInTypes.varint64type = zserio.limits.VARINT64_MAX
        allBuiltInTypes.varint_type = zserio.limits.VARINT_MAX
        allBuiltInTypes.string_type = "TEST"
        allBuiltInTypes.extern_type =  self._getExternalBitBuffer()
        expectedBitSizeOf = 1142
        self.assertEqual(expectedBitSizeOf, allBuiltInTypes.bitsizeof())

    def testReadWrite(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.bool_type = True
        allBuiltInTypes.uint8type = 8
        allBuiltInTypes.uint16type = zserio.limits.UINT16_MAX
        allBuiltInTypes.uint32type = zserio.limits.UINT32_MAX
        allBuiltInTypes.uint64type = zserio.limits.UINT64_MAX
        allBuiltInTypes.int8type = zserio.limits.INT8_MAX
        allBuiltInTypes.int16type = zserio.limits.INT16_MAX
        allBuiltInTypes.int32type = zserio.limits.INT32_MAX
        allBuiltInTypes.int64type = zserio.limits.INT64_MAX
        allBuiltInTypes.bitfield7type = 0x7F
        allBuiltInTypes.bitfield8type = zserio.limits.UINT8_MAX
        allBuiltInTypes.bitfield15_type = 0x7FFF
        allBuiltInTypes.bitfield16type = zserio.limits.UINT16_MAX
        allBuiltInTypes.bitfield31_type = 0x7FFFFFFF
        allBuiltInTypes.bitfield32type = zserio.limits.UINT32_MAX
        allBuiltInTypes.bitfield63_type = 0x7FFFFFFFFFFFFFFF
        allBuiltInTypes.variable_bitfield_type = zserio.limits.UINT8_MAX
        allBuiltInTypes.variable_bitfield8type = zserio.limits.UINT8_MAX
        allBuiltInTypes.intfield8type = zserio.limits.INT8_MAX
        allBuiltInTypes.intfield16type = zserio.limits.INT16_MAX
        allBuiltInTypes.intfield32type = zserio.limits.INT32_MAX
        allBuiltInTypes.intfield64type = zserio.limits.INT64_MAX
        allBuiltInTypes.variable_intfield_type = zserio.limits.INT8_MAX
        allBuiltInTypes.variable_intfield8type = zserio.limits.INT8_MAX
        allBuiltInTypes.float16type = 1.0
        allBuiltInTypes.float32type = 1.0
        allBuiltInTypes.float64type = sys.float_info.max
        allBuiltInTypes.varuint16type = zserio.limits.VARUINT16_MAX
        allBuiltInTypes.varuint32type = zserio.limits.VARUINT32_MAX
        allBuiltInTypes.varuint64type = zserio.limits.VARUINT64_MAX
        allBuiltInTypes.varuint_type = zserio.limits.VARUINT_MAX
        allBuiltInTypes.varsize_type = zserio.limits.VARSIZE_MAX
        allBuiltInTypes.varint16type = zserio.limits.VARINT16_MAX
        allBuiltInTypes.varint32type = zserio.limits.VARINT32_MAX
        allBuiltInTypes.varint64type = zserio.limits.VARINT64_MAX
        allBuiltInTypes.varint_type = zserio.limits.VARINT_MAX
        allBuiltInTypes.string_type = "TEST"
        allBuiltInTypes.extern_type = self._getExternalBitBuffer()

        writer = zserio.BitStreamWriter()
        allBuiltInTypes.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readAllBuiltInTypes = self.api.AllBuiltInTypes()
        readAllBuiltInTypes.read(reader)
        self.assertEqual(allBuiltInTypes, readAllBuiltInTypes)

    def _getExternalBitBuffer(self):
        externalStructure = self.api.ExternalStructure(value_=0xCD, rest_=0x03)
        writer = zserio.BitStreamWriter()
        externalStructure.write(writer)

        return zserio.BitBuffer(writer.byte_array, writer.bitposition)
