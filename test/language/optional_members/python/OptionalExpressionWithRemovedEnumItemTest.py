import zserio

import OptionalMembers


class OptionalExpressionWithRemovedEnumItemTest(OptionalMembers.TestCase):
    def testWriteRead(self):
        compound = self.api.Compound(12, [1, 2])

        bitBuffer = zserio.serialize(compound)

        readCompound = zserio.deserialize(self.api.Compound, bitBuffer)
        self.assertEqual(compound, readCompound)
