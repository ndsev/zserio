import os
import zserio

import ParameterizedTypes
from testutils import getApiDir

class GrandChildParamTest(ParameterizedTypes.TestCase):
    def testWrite(self):
        grandChildParam = self._createGrandChildParam()
        writer = zserio.BitStreamWriter()
        grandChildParam.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        self._checkGrandChildParamInStream(reader, grandChildParam)

        reader.bitposition = 0
        readGrandChildParam = self.api.GrandChildParam.from_reader(reader)
        self.assertEqual(grandChildParam, readGrandChildParam)

    def testWriteFile(self):
        grandChildParam = self._createGrandChildParam()
        zserio.serialize_to_file(grandChildParam, self.BLOB_NAME)

        readGrandChildParam = zserio.deserialize_from_file(self.api.GrandChildParam, self.BLOB_NAME)
        self.assertEqual(grandChildParam, readGrandChildParam)

    def _createItemChoiceHolder(self):
        item = self.api.Item(self.ITEM_CHOICE_HOLDER_HAS_ITEM, self.ITEM_PARAM,
                             self.ITEM_EXTRA_PARAM)
        itemChoice = self.api.ItemChoice(self.ITEM_CHOICE_HOLDER_HAS_ITEM)
        itemChoice.item = item

        return self.api.ItemChoiceHolder(itemChoice.has_item, itemChoice)

    def _createGrandChildParam(self):
        itemChoiceHolder = self._createItemChoiceHolder()
        itemChoiceHolderArray = [self._createItemChoiceHolder()]
        dummyArray = [0]

        return self.api.GrandChildParam(itemChoiceHolder, itemChoiceHolderArray, dummyArray)

    def _checkItemChoiceHolderInStream(self, reader, itemChoiceHolder):
        self.assertEqual(itemChoiceHolder.has_item, reader.read_bool())

        item = itemChoiceHolder.item_choice.item
        self.assertEqual(item.param, reader.read_bits(16))
        self.assertEqual(item.extra_param, reader.read_bits(32))

    def _checkGrandChildParamInStream(self, reader, grandChildParam):
        itemChoiceHolder = grandChildParam.item_choice_holder
        self._checkItemChoiceHolderInStream(reader, itemChoiceHolder)

        itemChoiceHolderArray = grandChildParam.item_choice_holder_array
        self.assertEqual(len(itemChoiceHolderArray), reader.read_varsize())
        self._checkItemChoiceHolderInStream(reader, itemChoiceHolderArray[0])

        isDummyArrayUsed = grandChildParam.is_dummy_array_used()
        self.assertEqual(isDummyArrayUsed, reader.read_bool())
        if isDummyArrayUsed:
            dummyArray = grandChildParam.dummy_array
            self.assertEqual(len(dummyArray), reader.read_varsize())
            self.assertEqual(dummyArray[0], reader.read_bits(32))

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)), "grand_child_param.blob")

    ITEM_CHOICE_HOLDER_HAS_ITEM = True
    ITEM_PARAM = 0xAABB
    ITEM_EXTRA_PARAM = 0x11223344
