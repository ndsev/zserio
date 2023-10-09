import zserio

import Templates

class ChoiceTemplatedSelectorTest(Templates.TestCase):
    def testReadWrite(self):
        selector16 = 0
        selector32 = 1
        uint16Choice = self.api.TemplatedChoice_uint16_Shift16(selector16)
        uint16Choice.uint16_field = 42
        uint32Choice = self.api.TemplatedChoice_uint32_Shift32(selector32, string_field_="string")
        choiceTemplatedSelector = self.api.ChoiceTemplatedSelector(
            selector16, selector32, uint16Choice, uint32Choice
        )

        writer = zserio.BitStreamWriter()
        choiceTemplatedSelector.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readChoiceTemplatedSelector = self.api.ChoiceTemplatedSelector()
        readChoiceTemplatedSelector.read(reader)
        self.assertEqual(choiceTemplatedSelector, readChoiceTemplatedSelector)
