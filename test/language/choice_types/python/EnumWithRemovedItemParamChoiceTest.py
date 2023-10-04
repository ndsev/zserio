import unittest
import zserio

from testutils import getZserioApi

class EnumWithRemovedItemParamChoiceTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "choice_types.zs").enum_with_removed_item_param_choice

    def testWriteRead(self):
        enumWithRemovedItemParamChoice = self.api.EnumWithRemovedItemParamChoice(
            self.api.Selector.ZSERIO_REMOVED_GREY
        )
        enumWithRemovedItemParamChoice.grey_data = 0xCAFE

        bitBuffer = zserio.serialize(enumWithRemovedItemParamChoice)

        readEnumWithRemovedItemParamChoice = zserio.deserialize(
            self.api.EnumWithRemovedItemParamChoice, bitBuffer, self.api.Selector.ZSERIO_REMOVED_GREY)
        self.assertEqual(enumWithRemovedItemParamChoice, readEnumWithRemovedItemParamChoice)
