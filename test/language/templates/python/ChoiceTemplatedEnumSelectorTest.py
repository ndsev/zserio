import zserio

import Templates

class ChoiceTemplatedEnumSelectorTest(Templates.TestCase):
    def testReadWrite(self):
        selectorFromZero = self.api.EnumFromZero.ONE
        selectorFromOne = self.api.EnumFromOne.THREE
        fromZeroChoice = self.api.TemplatedChoice_EnumFromZero(selectorFromZero)
        fromZeroChoice.uint16_field = 42
        fromOneChoice = self.api.TemplatedChoice_EnumFromOne(selectorFromOne, string_field_="string")
        choiceTemplatedEnumSelector = self.api.ChoiceTemplatedEnumSelector(
            selectorFromZero, selectorFromOne, fromZeroChoice, fromOneChoice
        )

        writer = zserio.BitStreamWriter()
        choiceTemplatedEnumSelector.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readChoiceTemplatedEnumSelector = self.api.ChoiceTemplatedEnumSelector()
        readChoiceTemplatedEnumSelector.read(reader)
        self.assertEqual(choiceTemplatedEnumSelector, readChoiceTemplatedEnumSelector)
