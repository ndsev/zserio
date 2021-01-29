import unittest
import zserio

from testutils import getZserioApi

class GrandChildParamTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "parameterized_types.zs").grand_child_param

    def testWrite(self):
        grandChildParam = self._createGrandChildParam()
        writer = zserio.BitStreamWriter()
        grandChildParam.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        self._checkGrandChildParamInStream(reader, grandChildParam)

        reader.setBitPosition(0)
        readGrandChildParam = self.api.GrandChildParam.fromReader(reader)
        self.assertEqual(grandChildParam, readGrandChildParam)

    def _createGrandChildParam(self):
        item = self.api.Item(self.ITEM_CHOICE_HOLDER_HAS_ITEM, self.ITEM_PARAM,
                             self.ITEM_EXTRA_PARAM)
        itemChoice = self.api.ItemChoice(self.ITEM_CHOICE_HOLDER_HAS_ITEM)
        itemChoice.setItem(item)
        itemChoiceHolder = self.api.ItemChoiceHolder(itemChoice.getHasItem(), itemChoice)

        return self.api.GrandChildParam(itemChoiceHolder)

    def _checkGrandChildParamInStream(self, stream, grandChildParam):
        itemChoiceHolder = grandChildParam.getItemChoiceHolder()
        self.assertEqual(itemChoiceHolder.getHasItem(), stream.readBool())

        item = itemChoiceHolder.getItemChoice().getItem()
        self.assertEqual(item.getParam(), stream.readBits(16))
        self.assertEqual(item.getExtraParam(), stream.readBits(32))

    ITEM_CHOICE_HOLDER_HAS_ITEM = True
    ITEM_PARAM = 0xAABB
    ITEM_EXTRA_PARAM = 0x11223344
