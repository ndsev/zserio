import zserio

import WithRangeCheckCode


class Int8RangeCheckTest(WithRangeCheckCode.TestCase):
    def testInt8LowerBound(self):
        self._checkInt8Value(INT8_LOWER_BOUND)

    def testInt8UpperBound(self):
        self._checkInt8Value(INT8_UPPER_BOUND)

    def testBit4BelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkInt8Value(INT8_LOWER_BOUND - 1)

    def testBit4AboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkInt8Value(INT8_UPPER_BOUND + 1)

    def _checkInt8Value(self, value):
        int8RangeCheckCompound = self.api.Int8RangeCheckCompound(value_=value)
        bitBuffer = zserio.serialize(int8RangeCheckCompound)
        readInt8RangeCheckCompound = zserio.deserialize(self.api.Int8RangeCheckCompound, bitBuffer)
        self.assertEqual(int8RangeCheckCompound, readInt8RangeCheckCompound)


INT8_LOWER_BOUND = -128
INT8_UPPER_BOUND = 127
