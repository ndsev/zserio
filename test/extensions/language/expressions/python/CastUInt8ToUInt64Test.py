import Expressions


class CastUInt8ToUInt64Test(Expressions.TestCase):
    def testUInt64ValueUsingUInt8Value(self):
        uint8Value = 0xBA
        castUInt8ToUInt64Expression = self.api.CastUInt8ToUInt64Expression(uint8Value, False)
        expectedUInt64Value = uint8Value
        self.assertEqual(expectedUInt64Value, castUInt8ToUInt64Expression.uint64_value())

    def testUint64ValueUsingConstant(self):
        uint8Value = 0xBA
        castUInt8ToUInt64Expression = self.api.CastUInt8ToUInt64Expression(uint8Value, use_constant_=True)
        expectedUInt64Value = 1
        self.assertEqual(expectedUInt64Value, castUInt8ToUInt64Expression.uint64_value())
