import unittest
import zserio

from testutils import getZserioApi

class Int4RangeCheckTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_range_check_code.zs",
                               extraArgs=["-withRangeCheckCode"]).int4_range_check

    def testInt4LowerBound(self):
        self._checkInt4Value(INT4_LOWER_BOUND)

    def testInt4UpperBound(self):
        self._checkInt4Value(INT4_UPPER_BOUND)

    def testInt4BelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkInt4Value(INT4_LOWER_BOUND - 1)

    def testInt4AboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkInt4Value(INT4_UPPER_BOUND + 1)

    def _checkInt4Value(self, value):
        int4RangeCheckCompound = self.api.Int4RangeCheckCompound(value)
        writer = zserio.BitStreamWriter()
        int4RangeCheckCompound.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readInt4RangeCheckCompound = self.api.Int4RangeCheckCompound.fromReader(reader)
        self.assertEqual(int4RangeCheckCompound, readInt4RangeCheckCompound)

INT4_LOWER_BOUND = -8
INT4_UPPER_BOUND = 7
