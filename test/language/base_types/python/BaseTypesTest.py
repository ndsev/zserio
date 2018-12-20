import sys
import unittest

import zserio

from testutils import getZserioApi

class BaseTypesTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "base_types.zs")

    def testUint8Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setUint8Type(zserio.limits.UINT8_MAX)
        self.assertEqual(zserio.limits.UINT8_MAX, baseTypes.getUint8Type())

    def testUint16Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setUint16Type(zserio.limits.UINT16_MAX)
        self.assertEqual(zserio.limits.UINT16_MAX, baseTypes.getUint16Type())

    def testUint32Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setUint32Type(zserio.limits.UINT32_MAX)
        self.assertEqual(zserio.limits.UINT32_MAX, baseTypes.getUint32Type())

    def testUint64Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setUint64Type(zserio.limits.UINT64_MAX)
        self.assertEqual(zserio.limits.UINT64_MAX, baseTypes.getUint64Type())

    def testInt8Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setInt8Type(zserio.limits.INT8_MAX)
        self.assertEqual(zserio.limits.INT8_MAX, baseTypes.getInt8Type())

    def testInt16Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setInt16Type(zserio.limits.INT16_MAX)
        self.assertEqual(zserio.limits.INT16_MAX, baseTypes.getInt16Type())

    def testInt32Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setInt32Type(zserio.limits.INT32_MAX)
        self.assertEqual(zserio.limits.INT32_MAX, baseTypes.getInt32Type())

    def testInt64Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setInt64Type(zserio.limits.INT64_MAX)
        self.assertEqual(zserio.limits.INT64_MAX, baseTypes.getInt64Type())

    def testBitField7Type(self):
        baseTypes = self.api.BaseTypes()
        maxBitfield7Type = 0x7F
        baseTypes.setBitfield7Type(maxBitfield7Type)
        self.assertEqual(maxBitfield7Type, baseTypes.getBitfield7Type())

    def testBitField8Type(self):
        baseTypes = self.api.BaseTypes()
        maxBitfield8Type = zserio.limits.UINT8_MAX
        baseTypes.setBitfield8Type(maxBitfield8Type)
        self.assertEqual(maxBitfield8Type, baseTypes.getBitfield8Type())

    def testBitField15Type(self):
        baseTypes = self.api.BaseTypes()
        maxBitfield15Type = 0x7FFF
        baseTypes.setBitfield15Type(maxBitfield15Type)
        self.assertEqual(maxBitfield15Type, baseTypes.getBitfield15Type())

    def testBitField16Type(self):
        baseTypes = self.api.BaseTypes()
        maxBitfield16Type = zserio.limits.UINT16_MAX
        baseTypes.setBitfield16Type(maxBitfield16Type)
        self.assertEqual(maxBitfield16Type, baseTypes.getBitfield16Type())

    def testBitField31Type(self):
        baseTypes = self.api.BaseTypes()
        maxBitfield31Type = 0x7FFFFFFF
        baseTypes.setBitfield31Type(maxBitfield31Type)
        self.assertEqual(maxBitfield31Type, baseTypes.getBitfield31Type())

    def testBitField32Type(self):
        baseTypes = self.api.BaseTypes()
        maxBitfield32Type = zserio.limits.UINT32_MAX
        baseTypes.setBitfield32Type(maxBitfield32Type)
        self.assertEqual(maxBitfield32Type, baseTypes.getBitfield32Type())

    def testBitField63Type(self):
        baseTypes = self.api.BaseTypes()
        maxBitfield63Type = 0x7FFFFFFFFFFFFFFF
        baseTypes.setBitfield63Type(maxBitfield63Type)
        self.assertEqual(maxBitfield63Type, baseTypes.getBitfield63Type())

    def testVariableBitfieldType(self):
        baseTypes = self.api.BaseTypes()
        maxVariableBitfieldType = zserio.limits.UINT64_MAX
        baseTypes.setVariableBitfieldType(maxVariableBitfieldType)
        self.assertEqual(maxVariableBitfieldType, baseTypes.getVariableBitfieldType())

    def testVariableBitField8Type(self):
        baseTypes = self.api.BaseTypes()
        maxBitfield8Type = zserio.limits.UINT8_MAX
        baseTypes.setBitfield8Type(maxBitfield8Type)
        self.assertEqual(maxBitfield8Type, baseTypes.getBitfield8Type())

    def testIntField8Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setIntfield8Type(zserio.limits.INT8_MAX)
        self.assertEqual(zserio.limits.INT8_MAX, baseTypes.getIntfield8Type())

    def testIntField16Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setIntfield16Type(zserio.limits.INT16_MAX)
        self.assertEqual(zserio.limits.INT16_MAX, baseTypes.getIntfield16Type())

    def testIntField32Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setIntfield32Type(zserio.limits.INT32_MAX)
        self.assertEqual(zserio.limits.INT32_MAX, baseTypes.getIntfield32Type())

    def testIntField64Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setIntfield64Type(zserio.limits.INT64_MAX)
        self.assertEqual(zserio.limits.INT64_MAX, baseTypes.getIntfield64Type())

    def testVariableIntfieldType(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setVariableIntfieldType(zserio.limits.INT16_MAX)
        self.assertEqual(zserio.limits.INT16_MAX, baseTypes.getVariableIntfieldType())

    def testVariableIntField8Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setIntfield8Type(zserio.limits.INT8_MAX)
        self.assertEqual(zserio.limits.INT8_MAX, baseTypes.getIntfield8Type())

    def testFloat16Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setFloat16Type(sys.float_info.max)
        self.assertEqual(sys.float_info.max, baseTypes.getFloat16Type())

    def testFloat32Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setFloat32Type(sys.float_info.max)
        self.assertEqual(sys.float_info.max, baseTypes.getFloat32Type())

    def testFloat64Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setFloat64Type(sys.float_info.max)
        self.assertEqual(sys.float_info.max, baseTypes.getFloat64Type())

    def testVaruint16Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setVaruint16Type(zserio.limits.VARUINT16_MAX)
        self.assertEqual(zserio.limits.VARUINT16_MAX, baseTypes.getVaruint16Type())

    def testVaruint32Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setVaruint32Type(zserio.limits.VARUINT32_MAX)
        self.assertEqual(zserio.limits.VARUINT32_MAX, baseTypes.getVaruint32Type())

    def testVaruint64Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setVaruint64Type(zserio.limits.VARUINT64_MAX)
        self.assertEqual(zserio.limits.VARUINT64_MAX, baseTypes.getVaruint64Type())

    def testVaruintType(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setVaruintType(zserio.limits.VARUINT_MIN)
        self.assertEqual(zserio.limits.VARUINT_MIN, baseTypes.getVaruintType())

        baseTypes.setVaruintType(zserio.limits.VARUINT_MAX)
        self.assertEqual(zserio.limits.VARUINT_MAX, baseTypes.getVaruintType())

    def testVarint16Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setVarint16Type(zserio.limits.VARINT16_MAX)
        self.assertEqual(zserio.limits.VARINT16_MAX, baseTypes.getVarint16Type())

    def testVarint32Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setVarint32Type(zserio.limits.VARINT32_MAX)
        self.assertEqual(zserio.limits.VARINT32_MAX, baseTypes.getVarint32Type())

    def testVarint64Type(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setVarint64Type(zserio.limits.VARINT64_MAX)
        self.assertEqual(zserio.limits.VARINT64_MAX, baseTypes.getVarint64Type())

    def testVarintType(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setVarintType(zserio.limits.VARINT_MIN)
        self.assertEqual(zserio.limits.VARINT_MIN, baseTypes.getVarintType())

        baseTypes.setVarintType(zserio.limits.VARINT_MAX)
        self.assertEqual(zserio.limits.VARINT_MAX, baseTypes.getVarintType())

    def testBoolType(self):
        baseTypes = self.api.BaseTypes()
        baseTypes.setBoolType(True)
        self.assertTrue(baseTypes.getBoolType())
        baseTypes.setBoolType(False)
        self.assertFalse(baseTypes.getBoolType())

    def testStringType(self):
        baseTypes = self.api.BaseTypes()
        testString = "TEST"
        baseTypes.setStringType(testString)
        self.assertEqual(testString, baseTypes.getStringType())
