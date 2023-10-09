import zserio

import WithRangeCheckCode

class Bit4RangeCheckTest(WithRangeCheckCode.TestCase):
    def testBit4LowerBound(self):
        self._checkBit4Value(BIT4_LOWER_BOUND)

    def testBit4UpperBound(self):
        self._checkBit4Value(BIT4_UPPER_BOUND)

    def testBit4BelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkBit4Value(BIT4_LOWER_BOUND - 1)

    def testBit4AboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkBit4Value(BIT4_UPPER_BOUND + 1)

    def _checkBit4Value(self, value):
        bit4RangeCheckCompound = self.api.Bit4RangeCheckCompound(value_=value)
        bitBuffer = zserio.serialize(bit4RangeCheckCompound)
        readBit4RangeCheckCompound = zserio.deserialize(self.api.Bit4RangeCheckCompound, bitBuffer)
        self.assertEqual(bit4RangeCheckCompound, readBit4RangeCheckCompound)

BIT4_LOWER_BOUND = 0
BIT4_UPPER_BOUND = 15
