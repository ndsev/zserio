import unittest
import zserio

from testutils import getZserioApi

class ChoiceTemplatedFieldTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").choice_templated_field

    def testReadWrite(self):
        selector = 0
        choice1 = self.api.TemplatedChoice_uint32_uint16(selector)
        choice1.setTemplatedField1(42)
        choice2 = self.api.TemplatedChoice_Compound_uint32_uint16(
            selector, templatedField1_=self.api.Compound_uint32(42)
        )
        choiceTemplatedField = self.api.ChoiceTemplatedField(selector, choice1, choice2)

        writer = zserio.BitStreamWriter()
        choiceTemplatedField.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readChoiceTemplatedField = self.api.ChoiceTemplatedField()
        readChoiceTemplatedField.read(reader)
        self.assertEqual(choiceTemplatedField, readChoiceTemplatedField)
