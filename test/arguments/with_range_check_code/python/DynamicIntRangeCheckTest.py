import unittest
import zserio

from testutils import getZserioApi

class DynamicIntRangeCheckTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_range_check_code.zs",
                               extraArgs=["-withRangeCheckCode"]).dynamic_int_range_check

    def testDynamicIntLowerBound(self):
        self._checkDynamicIntValue(DYNAMIC_INT_LOWER_BOUND)

    def testDynamicIntUpperBound(self):
        self._checkDynamicIntValue(DYNAMIC_INT_UPPER_BOUND)

    def testDynamicIntBelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkDynamicIntValue(DYNAMIC_INT_LOWER_BOUND - 1)

    def testDynamicIntAboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkDynamicIntValue(DYNAMIC_INT_UPPER_BOUND + 1)

    def _checkDynamicIntValue(self, value):
        dynamicIntRangeCheckCompound = self.api.DynamicIntRangeCheckCompound.fromFields(NUM_BITS, value)
        writer = zserio.BitStreamWriter()
        dynamicIntRangeCheckCompound.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readDynamicIntRangeCheckCompound = self.api.DynamicIntRangeCheckCompound.fromReader(reader)
        self.assertEqual(dynamicIntRangeCheckCompound, readDynamicIntRangeCheckCompound)

NUM_BITS = 10
DYNAMIC_INT_LOWER_BOUND = zserio.bitfield.getSignedBitFieldLowerBound(NUM_BITS)
DYNAMIC_INT_UPPER_BOUND = zserio.bitfield.getSignedBitFieldUpperBound(NUM_BITS)
