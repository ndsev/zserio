import zserio

import WithRangeCheckCode

class VarInt32RangeCheckTest(WithRangeCheckCode.TestCase):
    def testVarInt32LowerBound(self):
        self._checkVarInt32Value(VARINT32_LOWER_BOUND)

    def testVarInt32UpperBound(self):
        self._checkVarInt32Value(VARINT32_UPPER_BOUND)

    def testVarInt32BelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarInt32Value(VARINT32_LOWER_BOUND - 1)

    def testVarInt32AboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarInt32Value(VARINT32_UPPER_BOUND + 1)

    def _checkVarInt32Value(self, value):
        varInt32RangeCheckCompound = self.api.VarInt32RangeCheckCompound(value_=value)
        bitBuffer = zserio.serialize(varInt32RangeCheckCompound)
        readVarInt32RangeCheckCompound = zserio.deserialize(self.api.VarInt32RangeCheckCompound, bitBuffer)
        self.assertEqual(varInt32RangeCheckCompound, readVarInt32RangeCheckCompound)

VARINT32_LOWER_BOUND = zserio.limits.VARINT32_MIN
VARINT32_UPPER_BOUND = zserio.limits.VARINT32_MAX
