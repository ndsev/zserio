import zserio

import ChoiceTypes


class EnumWithRemovedItemParamChoiceTest(ChoiceTypes.TestCase):
    def testWriteRead(self):
        enumWithRemovedItemParamChoice = self.api.EnumWithRemovedItemParamChoice(
            self.api.Selector.ZSERIO_REMOVED_GREY
        )
        enumWithRemovedItemParamChoice.grey_data = 0xCAFE

        bitBuffer = zserio.serialize(enumWithRemovedItemParamChoice)

        readEnumWithRemovedItemParamChoice = zserio.deserialize(
            self.api.EnumWithRemovedItemParamChoice, bitBuffer, self.api.Selector.ZSERIO_REMOVED_GREY
        )
        self.assertEqual(enumWithRemovedItemParamChoice, readEnumWithRemovedItemParamChoice)
