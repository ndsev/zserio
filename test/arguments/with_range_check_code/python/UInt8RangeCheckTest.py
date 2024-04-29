import zserio

import WithRangeCheckCode


class UInt8RangeCheckTest(WithRangeCheckCode.TestCase):
    def testUInt8LowerBound(self):
        self._checkUInt8Value(UINT8_LOWER_BOUND)

    def testUInt8UpperBound(self):
        self._checkUInt8Value(UINT8_UPPER_BOUND)

    def testUInt8BelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkUInt8Value(UINT8_LOWER_BOUND - 1)

    def testUInt8AboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkUInt8Value(UINT8_UPPER_BOUND + 1)

    def _checkUInt8Value(self, value):
        uint8RangeCheckCompound = self.api.UInt8RangeCheckCompound(value)
        bitBuffer = zserio.serialize(uint8RangeCheckCompound)
        readUInt8RangeCheckCompound = zserio.deserialize(self.api.UInt8RangeCheckCompound, bitBuffer)
        self.assertEqual(uint8RangeCheckCompound, readUInt8RangeCheckCompound)


UINT8_LOWER_BOUND = 0
UINT8_UPPER_BOUND = 255
