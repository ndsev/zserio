import Expressions


class CastUInt64ToUInt8Test(Expressions.TestCase):
    def testUInt8ValueUsingUInt64Value(self):
        uint64Value = 0xFFFFFFFFFFFFFFFE
        castUInt64ToUInt8Expression = self.api.CastUInt64ToUInt8Expression(uint64Value, use_constant_=False)
        expectedUInt8Value = uint64Value
        self.assertEqual(expectedUInt8Value, castUInt64ToUInt8Expression.uint8_value())

    def testUint8ValueUsingConstant(self):
        uint64Value = 0xFFFFFFFFFFFFFFFE
        castUInt64ToUInt8Expression = self.api.CastUInt64ToUInt8Expression(uint64Value, True)
        expectedUInt8Value = 1
        self.assertEqual(expectedUInt8Value, castUInt64ToUInt8Expression.uint8_value())
