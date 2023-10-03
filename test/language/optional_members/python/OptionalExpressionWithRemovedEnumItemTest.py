import unittest
import zserio

from testutils import getZserioApi

class OptionalExpressionWithRemovedEnumItemTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "optional_members.zs").optional_expression_with_removed_enum_item

    def testWriteRead(self):
        compound = self.api.Compound(12, [1, 2])

        bitBuffer = zserio.serialize(compound)

        readCompound = zserio.deserialize(self.api.Compound, bitBuffer)
        self.assertEqual(compound, readCompound)
