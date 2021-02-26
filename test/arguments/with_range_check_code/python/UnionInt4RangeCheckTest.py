import unittest
import zserio

from testutils import getZserioApi

class UnionInt4RangeCheckTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_range_check_code.zs",
                               extraArgs=["-withRangeCheckCode"]).union_int4_range_check

    def testUnionInt4LowerBound(self):
        self._checkUnionInt4Value(INT4_LOWER_BOUND)

    def testUnionInt4UpperBound(self):
        self._checkUnionInt4Value(INT4_UPPER_BOUND)

    def testUnionInt4BelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkUnionInt4Value(INT4_LOWER_BOUND - 1)

    def testUnionInt4AboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkUnionInt4Value(INT4_UPPER_BOUND + 1)

    def _checkUnionInt4Value(self, value):
        unionInt4RangeCheckCompound = self.api.UnionInt4RangeCheckCompound()
        unionInt4RangeCheckCompound.value = value
        bitBuffer = zserio.serialize(unionInt4RangeCheckCompound)
        readUnionInt4RangeCheckCompound = zserio.deserialize(self.api.UnionInt4RangeCheckCompound, bitBuffer)
        self.assertEqual(unionInt4RangeCheckCompound, readUnionInt4RangeCheckCompound)

INT4_LOWER_BOUND = -8
INT4_UPPER_BOUND = 7
