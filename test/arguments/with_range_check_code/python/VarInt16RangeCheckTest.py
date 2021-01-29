import unittest
import zserio

from testutils import getZserioApi

class VarInt16RangeCheckTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_range_check_code.zs",
                               extraArgs=["-withRangeCheckCode"]).varint16_range_check

    def testVarInt16LowerBound(self):
        self._checkVarInt16Value(VARINT16_LOWER_BOUND)

    def testVarInt16UpperBound(self):
        self._checkVarInt16Value(VARINT16_UPPER_BOUND)

    def testVarInt16BelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarInt16Value(VARINT16_LOWER_BOUND - 1)

    def testVarInt16AboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarInt16Value(VARINT16_UPPER_BOUND + 1)

    def _checkVarInt16Value(self, value):
        varInt16RangeCheckCompound = self.api.VarInt16RangeCheckCompound(value)
        writer = zserio.BitStreamWriter()
        varInt16RangeCheckCompound.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readVarInt16RangeCheckCompound = self.api.VarInt16RangeCheckCompound.fromReader(reader)
        self.assertEqual(varInt16RangeCheckCompound, readVarInt16RangeCheckCompound)

VARINT16_LOWER_BOUND = zserio.limits.VARINT16_MIN
VARINT16_UPPER_BOUND = zserio.limits.VARINT16_MAX
