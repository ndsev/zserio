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
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        self._checkGrandChildParamInStream(reader, grandChildParam)

        reader.setBitPosition(0)
        readGrandChildParam = self.api.GrandChildParam.fromReader(reader)
        self.assertEqual(grandChildParam, readGrandChildParam)

    def _createGrandChildParam(self):
        item = self.api.Item(self.ITEM_CHOICE_HOLDER_HAS_ITEM, self.ITEM_PARAM,
                             self.ITEM_EXTRA_PARAM)
        itemChoice = self.api.ItemChoice(self.ITEM_CHOICE_HOLDER_HAS_ITEM)
        itemChoice.item = item
        itemChoiceHolder = self.api.ItemChoiceHolder(itemChoice.has_item, itemChoice)

        return self.api.GrandChildParam(itemChoiceHolder)

    def _checkGrandChildParamInStream(self, stream, grandChildParam):
        itemChoiceHolder = grandChildParam.item_choice_holder
        self.assertEqual(itemChoiceHolder.has_item, stream.readBool())

        item = itemChoiceHolder.item_choice.item
        self.assertEqual(item.param, stream.readBits(16))
        self.assertEqual(item.extra_param, stream.readBits(32))

    ITEM_CHOICE_HOLDER_HAS_ITEM = True
    ITEM_PARAM = 0xAABB
    ITEM_EXTRA_PARAM = 0x11223344
