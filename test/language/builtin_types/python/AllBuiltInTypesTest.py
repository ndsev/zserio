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
        allBuiltInTypes.setUint8Type(zserio.limits.UINT8_MAX)
        self.assertEqual(zserio.limits.UINT8_MAX, allBuiltInTypes.getUint8Type())

    def testUint16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setUint16Type(zserio.limits.UINT16_MAX)
        self.assertEqual(zserio.limits.UINT16_MAX, allBuiltInTypes.getUint16Type())

    def testUint32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setUint32Type(zserio.limits.UINT32_MAX)
        self.assertEqual(zserio.limits.UINT32_MAX, allBuiltInTypes.getUint32Type())

    def testUint64Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setUint64Type(zserio.limits.UINT64_MAX)
        self.assertEqual(zserio.limits.UINT64_MAX, allBuiltInTypes.getUint64Type())

    def testInt8Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setInt8Type(zserio.limits.INT8_MAX)
        self.assertEqual(zserio.limits.INT8_MAX, allBuiltInTypes.getInt8Type())

    def testInt16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setInt16Type(zserio.limits.INT16_MAX)
        self.assertEqual(zserio.limits.INT16_MAX, allBuiltInTypes.getInt16Type())

    def testInt32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setInt32Type(zserio.limits.INT32_MAX)
        self.assertEqual(zserio.limits.INT32_MAX, allBuiltInTypes.getInt32Type())

    def testInt64Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setInt64Type(zserio.limits.INT64_MAX)
        self.assertEqual(zserio.limits.INT64_MAX, allBuiltInTypes.getInt64Type())

    def testBitField7Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield7Type = 0x7F
        allBuiltInTypes.setBitfield7Type(maxBitfield7Type)
        self.assertEqual(maxBitfield7Type, allBuiltInTypes.getBitfield7Type())

    def testBitField8Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield8Type = zserio.limits.UINT8_MAX
        allBuiltInTypes.setBitfield8Type(maxBitfield8Type)
        self.assertEqual(maxBitfield8Type, allBuiltInTypes.getBitfield8Type())

    def testBitField15Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield15Type = 0x7FFF
        allBuiltInTypes.setBitfield15Type(maxBitfield15Type)
        self.assertEqual(maxBitfield15Type, allBuiltInTypes.getBitfield15Type())

    def testBitField16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield16Type = zserio.limits.UINT16_MAX
        allBuiltInTypes.setBitfield16Type(maxBitfield16Type)
        self.assertEqual(maxBitfield16Type, allBuiltInTypes.getBitfield16Type())

    def testBitField31Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield31Type = 0x7FFFFFFF
        allBuiltInTypes.setBitfield31Type(maxBitfield31Type)
        self.assertEqual(maxBitfield31Type, allBuiltInTypes.getBitfield31Type())

    def testBitField32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield32Type = zserio.limits.UINT32_MAX
        allBuiltInTypes.setBitfield32Type(maxBitfield32Type)
        self.assertEqual(maxBitfield32Type, allBuiltInTypes.getBitfield32Type())

    def testBitField63Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxBitfield63Type = 0x7FFFFFFFFFFFFFFF
        allBuiltInTypes.setBitfield63Type(maxBitfield63Type)
        self.assertEqual(maxBitfield63Type, allBuiltInTypes.getBitfield63Type())

    def testVariableBitfieldType(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxVariableBitfieldType = zserio.limits.UINT64_MAX
        allBuiltInTypes.setVariableBitfieldType(maxVariableBitfieldType)
        self.assertEqual(maxVariableBitfieldType, allBuiltInTypes.getVariableBitfieldType())

    def testVariableBitField8Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        maxVariableBitfield8Type = zserio.limits.UINT8_MAX
        allBuiltInTypes.setVariableBitfield8Type(maxVariableBitfield8Type)
        self.assertEqual(maxVariableBitfield8Type, allBuiltInTypes.getVariableBitfield8Type())

    def testIntField8Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setIntfield8Type(zserio.limits.INT8_MAX)
        self.assertEqual(zserio.limits.INT8_MAX, allBuiltInTypes.getIntfield8Type())

    def testIntField16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setIntfield16Type(zserio.limits.INT16_MAX)
        self.assertEqual(zserio.limits.INT16_MAX, allBuiltInTypes.getIntfield16Type())

    def testIntField32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setIntfield32Type(zserio.limits.INT32_MAX)
        self.assertEqual(zserio.limits.INT32_MAX, allBuiltInTypes.getIntfield32Type())

    def testIntField64Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setIntfield64Type(zserio.limits.INT64_MAX)
        self.assertEqual(zserio.limits.INT64_MAX, allBuiltInTypes.getIntfield64Type())

    def testVariableIntfieldType(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setVariableIntfieldType(zserio.limits.INT16_MAX)
        self.assertEqual(zserio.limits.INT16_MAX, allBuiltInTypes.getVariableIntfieldType())

    def testVariableIntField8Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setVariableIntfield8Type(zserio.limits.INT8_MAX)
        self.assertEqual(zserio.limits.INT8_MAX, allBuiltInTypes.getVariableIntfield8Type())

    def testFloat16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setFloat16Type(sys.float_info.max)
        self.assertEqual(sys.float_info.max, allBuiltInTypes.getFloat16Type())

    def testFloat32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setFloat32Type(sys.float_info.max)
        self.assertEqual(sys.float_info.max, allBuiltInTypes.getFloat32Type())

    def testFloat64Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setFloat64Type(sys.float_info.max)
        self.assertEqual(sys.float_info.max, allBuiltInTypes.getFloat64Type())

    def testVaruint16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setVaruint16Type(zserio.limits.VARUINT16_MAX)
        self.assertEqual(zserio.limits.VARUINT16_MAX, allBuiltInTypes.getVaruint16Type())

    def testVaruint32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setVaruint32Type(zserio.limits.VARUINT32_MAX)
        self.assertEqual(zserio.limits.VARUINT32_MAX, allBuiltInTypes.getVaruint32Type())

    def testVaruint64Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setVaruint64Type(zserio.limits.VARUINT64_MAX)
        self.assertEqual(zserio.limits.VARUINT64_MAX, allBuiltInTypes.getVaruint64Type())

    def testVaruintType(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setVaruintType(zserio.limits.VARUINT_MIN)
        self.assertEqual(zserio.limits.VARUINT_MIN, allBuiltInTypes.getVaruintType())

        allBuiltInTypes.setVaruintType(zserio.limits.VARUINT_MAX)
        self.assertEqual(zserio.limits.VARUINT_MAX, allBuiltInTypes.getVaruintType())

    def testVarint16Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setVarint16Type(zserio.limits.VARINT16_MAX)
        self.assertEqual(zserio.limits.VARINT16_MAX, allBuiltInTypes.getVarint16Type())

    def testVarint32Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setVarint32Type(zserio.limits.VARINT32_MAX)
        self.assertEqual(zserio.limits.VARINT32_MAX, allBuiltInTypes.getVarint32Type())

    def testVarint64Type(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setVarint64Type(zserio.limits.VARINT64_MAX)
        self.assertEqual(zserio.limits.VARINT64_MAX, allBuiltInTypes.getVarint64Type())

    def testVarintType(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setVarintType(zserio.limits.VARINT_MIN)
        self.assertEqual(zserio.limits.VARINT_MIN, allBuiltInTypes.getVarintType())

        allBuiltInTypes.setVarintType(zserio.limits.VARINT_MAX)
        self.assertEqual(zserio.limits.VARINT_MAX, allBuiltInTypes.getVarintType())

    def testBoolType(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setBoolType(True)
        self.assertTrue(allBuiltInTypes.getBoolType())
        allBuiltInTypes.setBoolType(False)
        self.assertFalse(allBuiltInTypes.getBoolType())

    def testStringType(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        testString = "TEST"
        allBuiltInTypes.setStringType(testString)
        self.assertEqual(testString, allBuiltInTypes.getStringType())

    def testExternType(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        testExtern = self._getExternalBitBuffer()
        allBuiltInTypes.setExternType(testExtern)
        self.assertEqual(testExtern, allBuiltInTypes.getExternType())

    def testBitSizeOf(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setBoolType(True)
        allBuiltInTypes.setUint8Type(1)
        allBuiltInTypes.setUint16Type(zserio.limits.UINT16_MAX)
        allBuiltInTypes.setUint32Type(zserio.limits.UINT32_MAX)
        allBuiltInTypes.setUint64Type(zserio.limits.UINT64_MAX)
        allBuiltInTypes.setInt8Type(zserio.limits.INT8_MAX)
        allBuiltInTypes.setInt16Type(zserio.limits.INT16_MAX)
        allBuiltInTypes.setInt32Type(zserio.limits.INT32_MAX)
        allBuiltInTypes.setInt64Type(zserio.limits.INT64_MAX)
        allBuiltInTypes.setBitfield7Type(0x7F)
        allBuiltInTypes.setBitfield8Type(zserio.limits.UINT8_MAX)
        allBuiltInTypes.setBitfield15Type(0x7FFF)
        allBuiltInTypes.setBitfield16Type(zserio.limits.UINT16_MAX)
        allBuiltInTypes.setBitfield31Type(0x7FFFFFFF)
        allBuiltInTypes.setBitfield32Type(zserio.limits.UINT32_MAX)
        allBuiltInTypes.setBitfield63Type(0x7FFFFFFFFFFFFFFF)
        allBuiltInTypes.setVariableBitfieldType(1)
        allBuiltInTypes.setVariableBitfield8Type(zserio.limits.UINT8_MAX)
        allBuiltInTypes.setIntfield8Type(zserio.limits.INT8_MAX)
        allBuiltInTypes.setIntfield16Type(zserio.limits.INT16_MAX)
        allBuiltInTypes.setIntfield32Type(zserio.limits.INT32_MAX)
        allBuiltInTypes.setIntfield64Type(zserio.limits.INT64_MAX)
        allBuiltInTypes.setVariableIntfieldType(1)
        allBuiltInTypes.setVariableIntfield8Type(zserio.limits.INT8_MAX)
        allBuiltInTypes.setFloat16Type(sys.float_info.max)
        allBuiltInTypes.setFloat32Type(sys.float_info.max)
        allBuiltInTypes.setFloat64Type(sys.float_info.max)
        allBuiltInTypes.setVaruint16Type(zserio.limits.VARUINT16_MAX)
        allBuiltInTypes.setVaruint32Type(zserio.limits.VARUINT32_MAX)
        allBuiltInTypes.setVaruint64Type(zserio.limits.VARUINT64_MAX)
        allBuiltInTypes.setVaruintType(zserio.limits.VARUINT_MAX)
        allBuiltInTypes.setVarint16Type(zserio.limits.VARINT16_MAX)
        allBuiltInTypes.setVarint32Type(zserio.limits.VARINT32_MAX)
        allBuiltInTypes.setVarint64Type(zserio.limits.VARINT64_MAX)
        allBuiltInTypes.setVarintType(zserio.limits.VARINT_MAX)
        allBuiltInTypes.setStringType("TEST")
        allBuiltInTypes.setExternType(self._getExternalBitBuffer())
        expectedBitSizeOf = 1102
        self.assertEqual(expectedBitSizeOf, allBuiltInTypes.bitSizeOf())

    def testReadWrite(self):
        allBuiltInTypes = self.api.AllBuiltInTypes()
        allBuiltInTypes.setBoolType(True)
        allBuiltInTypes.setUint8Type(8)
        allBuiltInTypes.setUint16Type(zserio.limits.UINT16_MAX)
        allBuiltInTypes.setUint32Type(zserio.limits.UINT32_MAX)
        allBuiltInTypes.setUint64Type(zserio.limits.UINT64_MAX)
        allBuiltInTypes.setInt8Type(zserio.limits.INT8_MAX)
        allBuiltInTypes.setInt16Type(zserio.limits.INT16_MAX)
        allBuiltInTypes.setInt32Type(zserio.limits.INT32_MAX)
        allBuiltInTypes.setInt64Type(zserio.limits.INT64_MAX)
        allBuiltInTypes.setBitfield7Type(0x7F)
        allBuiltInTypes.setBitfield8Type(zserio.limits.UINT8_MAX)
        allBuiltInTypes.setBitfield15Type(0x7FFF)
        allBuiltInTypes.setBitfield16Type(zserio.limits.UINT16_MAX)
        allBuiltInTypes.setBitfield31Type(0x7FFFFFFF)
        allBuiltInTypes.setBitfield32Type(zserio.limits.UINT32_MAX)
        allBuiltInTypes.setBitfield63Type(0x7FFFFFFFFFFFFFFF)
        allBuiltInTypes.setVariableBitfieldType(zserio.limits.UINT8_MAX)
        allBuiltInTypes.setVariableBitfield8Type(zserio.limits.UINT8_MAX)
        allBuiltInTypes.setIntfield8Type(zserio.limits.INT8_MAX)
        allBuiltInTypes.setIntfield16Type(zserio.limits.INT16_MAX)
        allBuiltInTypes.setIntfield32Type(zserio.limits.INT32_MAX)
        allBuiltInTypes.setIntfield64Type(zserio.limits.INT64_MAX)
        allBuiltInTypes.setVariableIntfieldType(zserio.limits.INT8_MAX)
        allBuiltInTypes.setVariableIntfield8Type(zserio.limits.INT8_MAX)
        allBuiltInTypes.setFloat16Type(1.0)
        allBuiltInTypes.setFloat32Type(1.0)
        allBuiltInTypes.setFloat64Type(sys.float_info.max)
        allBuiltInTypes.setVaruint16Type(zserio.limits.VARUINT16_MAX)
        allBuiltInTypes.setVaruint32Type(zserio.limits.VARUINT32_MAX)
        allBuiltInTypes.setVaruint64Type(zserio.limits.VARUINT64_MAX)
        allBuiltInTypes.setVaruintType(zserio.limits.VARUINT_MAX)
        allBuiltInTypes.setVarint16Type(zserio.limits.VARINT16_MAX)
        allBuiltInTypes.setVarint32Type(zserio.limits.VARINT32_MAX)
        allBuiltInTypes.setVarint64Type(zserio.limits.VARINT64_MAX)
        allBuiltInTypes.setVarintType(zserio.limits.VARINT_MAX)
        allBuiltInTypes.setStringType("TEST")
        allBuiltInTypes.setExternType(self._getExternalBitBuffer())

        writer = zserio.BitStreamWriter()
        allBuiltInTypes.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readAllBuiltInTypes = self.api.AllBuiltInTypes()
        readAllBuiltInTypes.read(reader)
        self.assertEqual(allBuiltInTypes, readAllBuiltInTypes)

    def _getExternalBitBuffer(self):
        externalStructure = self.api.ExternalStructure.fromFields(0xCD, 0x03)
        writer = zserio.BitStreamWriter()
        externalStructure.write(writer)

        return zserio.BitBuffer(writer.getByteArray(), writer.getBitPosition())
