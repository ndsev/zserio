import unittest
import zserio

from testutils import getZserioApi

class ChoiceTemplatedSelectorTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").choice_templated_selector

    def testReadWrite(self):
        selector16 = 0
        selector32 = 2
        uint16Choice = self.api.TemplatedChoice_uint16(selector16)
        uint16Choice.setUint16Field(42)
        uint32Choice = self.api.TemplatedChoice_uint32(selector32)
        uint32Choice.setStringField("string")
        choiceTemplatedSelector = self.api.ChoiceTemplatedSelector.fromFields(
            selector16, selector32, uint16Choice, uint32Choice
        )

        writer = zserio.BitStreamWriter()
        choiceTemplatedSelector.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readChoiceTemplatedSelector = self.api.ChoiceTemplatedSelector()
        readChoiceTemplatedSelector.read(reader)
        self.assertEqual(choiceTemplatedSelector, readChoiceTemplatedSelector)
