import unittest
import zserio

from testutils import getZserioApi

class OptionalBit31RangeCheckTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_range_check_code.zs",
                               extraArgs=["-withRangeCheckCode"]).optional_bit31_range_check

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
        optionalBit31RangeCheckCompound.setValue(None)

    def _checkOptionalBit31Value(self, value):
        optionalBit31RangeCheckCompound = self.api.OptionalBit31RangeCheckCompound(hasOptional_=True,
                                                                                   value_=value)
        writer = zserio.BitStreamWriter()
        optionalBit31RangeCheckCompound.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readOptionalBit31RangeCheckCompound = self.api.OptionalBit31RangeCheckCompound.fromReader(reader)
        self.assertEqual(optionalBit31RangeCheckCompound, readOptionalBit31RangeCheckCompound)

OPTIONAL_BIT31_LOWER_BOUND = 0
OPTIONAL_BIT31_UPPER_BOUND = 2147483647
