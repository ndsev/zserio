import unittest
import zserio

from testutils import getZserioApi

class ChoiceTemplatedEnumSelectorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").choice_templated_enum_selector

    def testReadWrite(self):
        selectorFromZero = self.api.EnumFromZero.ONE
        selectorFromOne = self.api.EnumFromOne.THREE
        fromZeroChoice = self.api.TemplatedChoice_EnumFromZero(selectorFromZero)
        fromZeroChoice.setUint16Field(42)
        fromOneChoice = self.api.TemplatedChoice_EnumFromOne(selectorFromOne, stringField_="string")
        choiceTemplatedEnumSelector = self.api.ChoiceTemplatedEnumSelector(
            selectorFromZero, selectorFromOne, fromZeroChoice, fromOneChoice
        )

        writer = zserio.BitStreamWriter()
        choiceTemplatedEnumSelector.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readChoiceTemplatedEnumSelector = self.api.ChoiceTemplatedEnumSelector()
        readChoiceTemplatedEnumSelector.read(reader)
        self.assertEqual(choiceTemplatedEnumSelector, readChoiceTemplatedEnumSelector)
