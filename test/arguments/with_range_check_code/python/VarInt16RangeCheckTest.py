import zserio

import WithRangeCheckCode

class VarInt16RangeCheckTest(WithRangeCheckCode.TestCase):
    def testVarInt16LowerBound(self):
        self._checkVarInt16Value(VARINT16_LOWER_BOUND)

    def testVarInt16UpperBound(self):
        self._checkVarInt16Value(VARINT16_UPPER_BOUND)

    def testVarInt16BelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarInt16Value(VARINT16_LOWER_BOUND - 1)

    def testVarInt16AboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarInt16Value(VARINT16_UPPER_BOUND + 1)

    def _checkVarInt16Value(self, value):
        varInt16RangeCheckCompound = self.api.VarInt16RangeCheckCompound(value)
        bitBuffer = zserio.serialize(varInt16RangeCheckCompound)
        readVarInt16RangeCheckCompound = zserio.deserialize(self.api.VarInt16RangeCheckCompound, bitBuffer)
        self.assertEqual(varInt16RangeCheckCompound, readVarInt16RangeCheckCompound)

VARINT16_LOWER_BOUND = zserio.limits.VARINT16_MIN
VARINT16_UPPER_BOUND = zserio.limits.VARINT16_MAX
