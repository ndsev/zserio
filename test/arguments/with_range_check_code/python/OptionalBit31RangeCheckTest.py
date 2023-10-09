import zserio

import WithRangeCheckCode

class OptionalBit31RangeCheckTest(WithRangeCheckCode.TestCase):
    def testOptionalBit31LowerBound(self):
        self._checkOptionalBit31Value(OPTIONAL_BIT31_LOWER_BOUND)

    def testOptionalBit31UpperBound(self):
        self._checkOptionalBit31Value(OPTIONAL_BIT31_UPPER_BOUND)

    def testOptionalBit31BelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkOptionalBit31Value(OPTIONAL_BIT31_LOWER_BOUND - 1)

    def testOptionalBit31AboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkOptionalBit31Value(OPTIONAL_BIT31_UPPER_BOUND + 1)

    def testOptionalBit31None(self):
        optionalBit31RangeCheckCompound = self.api.OptionalBit31RangeCheckCompound()
        optionalBit31RangeCheckCompound.value = None

    def _checkOptionalBit31Value(self, value):
        optionalBit31RangeCheckCompound = self.api.OptionalBit31RangeCheckCompound(has_optional_=True,
                                                                                   value_=value)
        bitBuffer = zserio.serialize(optionalBit31RangeCheckCompound)
        readOptionalBit31RangeCheckCompound = zserio.deserialize(self.api.OptionalBit31RangeCheckCompound,
                                                                 bitBuffer)
        self.assertEqual(optionalBit31RangeCheckCompound, readOptionalBit31RangeCheckCompound)

OPTIONAL_BIT31_LOWER_BOUND = 0
OPTIONAL_BIT31_UPPER_BOUND = 2147483647
