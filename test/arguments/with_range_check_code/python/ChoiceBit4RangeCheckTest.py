import unittest
import zserio

from testutils import getZserioApi

class ChoiceBit4RangeCheckTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_range_check_code.zs",
                               extraArgs=["-withRangeCheckCode"]).choice_bit4_range_check

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
