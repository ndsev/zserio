import unittest
import zserio

from testutils import getZserioApi

class Int7ArrayRangeCheckTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_range_check_code.zs",
                               extraArgs=["-withRangeCheckCode"]).int7_array_range_check

    def testInt7ArrayLowerBound(self):
        self._checkInt7ArrayValue(INT7_LOWER_BOUND)

    def testInt7ArrayUpperBound(self):
        self._checkInt7ArrayValue(INT7_UPPER_BOUND)

    def testInt7ArrayBelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkInt7ArrayValue(INT7_LOWER_BOUND - 1)

    def testInt7ArrayAboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkInt7ArrayValue(INT7_UPPER_BOUND + 1)

    def _checkInt7ArrayValue(self, value):
        int7ArrayRangeCheckCompound = self.api.Int7ArrayRangeCheckCompound(1, array_=[value])
        bitBuffer = zserio.serialize(int7ArrayRangeCheckCompound)
        readInt7ArrayRangeCheckCompound = zserio.deserialize(self.api.Int7ArrayRangeCheckCompound, bitBuffer)
        self.assertEqual(int7ArrayRangeCheckCompound, readInt7ArrayRangeCheckCompound)

INT7_LOWER_BOUND = -64
INT7_UPPER_BOUND = 63
