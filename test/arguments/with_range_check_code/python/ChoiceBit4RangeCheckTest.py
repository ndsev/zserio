import zserio

import WithRangeCheckCode

class ChoiceBit4RangeCheckTest(WithRangeCheckCode.TestCase):
    def testChoiceBit4LowerBound(self):
        self._checkChoiceBit4Value(BIT4_LOWER_BOUND)

    def testChoiceBit4UpperBound(self):
        self._checkChoiceBit4Value(BIT4_UPPER_BOUND)

    def testChoiceBit4BelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkChoiceBit4Value(BIT4_LOWER_BOUND - 1)

    def testChoiceBit4AboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkChoiceBit4Value(BIT4_UPPER_BOUND + 1)

    def _checkChoiceBit4Value(self, value):
        selector = True
        choiceBit4RangeCheckCompound = self.api.ChoiceBit4RangeCheckCompound(selector)
        choiceBit4RangeCheckCompound.value = value
        bitBuffer = zserio.serialize(choiceBit4RangeCheckCompound)
        readChoiceBit4RangeCheckCompound = zserio.deserialize(self.api.ChoiceBit4RangeCheckCompound, bitBuffer,
                                                              selector)
        self.assertEqual(choiceBit4RangeCheckCompound, readChoiceBit4RangeCheckCompound)

BIT4_LOWER_BOUND = 0
BIT4_UPPER_BOUND = 15
