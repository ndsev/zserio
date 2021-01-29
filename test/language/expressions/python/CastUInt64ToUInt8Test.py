import unittest

from testutils import getZserioApi

class CastUInt64ToUInt8Test(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "expressions.zs").cast_uint64_to_uint8

    def testUInt8ValueUsingUInt64Value(self):
        uint64Value = 0xFFFFFFFFFFFFFFFE
        castUInt64ToUInt8Expression = self.api.CastUInt64ToUInt8Expression(uint64Value, useConstant_=False)
        expectedUInt8Value = uint64Value
        self.assertEqual(expectedUInt8Value, castUInt64ToUInt8Expression.funcUint8Value())

    def testUint8ValueUsingConstant(self):
        uint64Value = 0xFFFFFFFFFFFFFFFE
        castUInt64ToUInt8Expression = self.api.CastUInt64ToUInt8Expression(uint64Value, True)
        expectedUInt8Value = 1
        self.assertEqual(expectedUInt8Value, castUInt64ToUInt8Expression.funcUint8Value())
