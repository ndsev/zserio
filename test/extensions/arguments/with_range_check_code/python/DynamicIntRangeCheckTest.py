import zserio

import WithRangeCheckCode


class DynamicIntRangeCheckTest(WithRangeCheckCode.TestCase):
    def testDynamicIntLowerBound(self):
        self._checkDynamicIntValue(NUM_BITS, DYNAMIC_INT_LOWER_BOUND)

    def testDynamicIntUpperBound(self):
        self._checkDynamicIntValue(NUM_BITS, DYNAMIC_INT_UPPER_BOUND)

    def testDynamicIntBelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkDynamicIntValue(NUM_BITS, DYNAMIC_INT_LOWER_BOUND - 1)

    def testDynamicIntAboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkDynamicIntValue(NUM_BITS, DYNAMIC_INT_UPPER_BOUND + 1)

    def testNumBitsMax(self):
        self._checkDynamicIntValue(64, zserio.limits.INT64_MAX)

    def testNumBitsAboveMax(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkDynamicIntValue(65, zserio.limits.INT64_MAX)

    def _checkDynamicIntValue(self, numBits, value):
        dynamicIntRangeCheckCompound = self.api.DynamicIntRangeCheckCompound(num_bits_=numBits, value_=value)
        bitBuffer = zserio.serialize(dynamicIntRangeCheckCompound)
        readDynamicIntRangeCheckCompound = zserio.deserialize(self.api.DynamicIntRangeCheckCompound, bitBuffer)
        self.assertEqual(dynamicIntRangeCheckCompound, readDynamicIntRangeCheckCompound)


NUM_BITS = 10
DYNAMIC_INT_LOWER_BOUND = zserio.bitfield.signed_bitfield_lowerbound(NUM_BITS)
DYNAMIC_INT_UPPER_BOUND = zserio.bitfield.signed_bitfield_upperbound(NUM_BITS)
