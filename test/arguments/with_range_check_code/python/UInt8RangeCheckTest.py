import unittest
import zserio

from testutils import getZserioApi

class UInt8RangeCheckTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "with_range_check_code.zs",
                               extraArgs=["-withRangeCheckCode"]).uint8_range_check

    def testUInt8LowerBound(self):
        self._checkUInt8Value(UINT8_LOWER_BOUND)

    def testUInt8UpperBound(self):
        self._checkUInt8Value(UINT8_UPPER_BOUND)

    def testUInt8BelowLowerBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkUInt8Value(UINT8_LOWER_BOUND - 1)

    def testUInt8AboveUpperBound(self):
        with self.assertRaises(zserio.PythonRuntimeException):
            self._checkUInt8Value(UINT8_UPPER_BOUND + 1)

    def _checkUInt8Value(self, value):
        uint8RangeCheckCompound = self.api.UInt8RangeCheckCompound(value)
        writer = zserio.BitStreamWriter()
        uint8RangeCheckCompound.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readUInt8RangeCheckCompound = self.api.UInt8RangeCheckCompound.fromReader(reader)
        self.assertEqual(uint8RangeCheckCompound, readUInt8RangeCheckCompound)

UINT8_LOWER_BOUND = 0
UINT8_UPPER_BOUND = 255
