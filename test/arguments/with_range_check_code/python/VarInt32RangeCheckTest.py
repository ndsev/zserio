import unittest
import zserio

from testutils import getZserioApi

class VarInt32RangeCheckTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_range_check_code.zs",
                               extraArgs=["-withRangeCheckCode"]).varint32_range_check

    def testVarInt32LowerBound(self):
        self._checkVarInt32Value(VARINT32_LOWER_BOUND)

    def testVarInt32UpperBound(self):
        self._checkVarInt32Value(VARINT32_UPPER_BOUND)

    def testVarInt32BelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarInt32Value(VARINT32_LOWER_BOUND - 1)

    def testVarInt32AboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkVarInt32Value(VARINT32_UPPER_BOUND + 1)

    def _checkVarInt32Value(self, value):
        varInt32RangeCheckCompound = self.api.VarInt32RangeCheckCompound(value_=value)
        writer = zserio.BitStreamWriter()
        varInt32RangeCheckCompound.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readVarInt32RangeCheckCompound = self.api.VarInt32RangeCheckCompound.fromReader(reader)
        self.assertEqual(varInt32RangeCheckCompound, readVarInt32RangeCheckCompound)

VARINT32_LOWER_BOUND = zserio.limits.VARINT32_MIN
VARINT32_UPPER_BOUND = zserio.limits.VARINT32_MAX
