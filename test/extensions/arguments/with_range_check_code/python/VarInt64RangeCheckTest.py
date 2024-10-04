import zserio

import WithRangeCheckCode


class VarInt64RangeCheckTest(WithRangeCheckCode.TestCase):
    def testVarInt64LowerBound(self):
        self._checkVarInt64Value(VARINT64_LOWER_BOUND)

    def testVarInt64UpperBound(self):
        self._checkVarInt64Value(VARINT64_UPPER_BOUND)

    def testVarInt64BelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarInt64Value(VARINT64_LOWER_BOUND - 1)

    def testVarInt64AboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarInt64Value(VARINT64_UPPER_BOUND + 1)

    def _checkVarInt64Value(self, value):
        varInt64RangeCheckCompound = self.api.VarInt64RangeCheckCompound(value)
        bitBuffer = zserio.serialize(varInt64RangeCheckCompound)
        readVarInt64RangeCheckCompound = zserio.deserialize(self.api.VarInt64RangeCheckCompound, bitBuffer)
        self.assertEqual(varInt64RangeCheckCompound, readVarInt64RangeCheckCompound)


VARINT64_LOWER_BOUND = zserio.limits.VARINT64_MIN
VARINT64_UPPER_BOUND = zserio.limits.VARINT64_MAX
