import zserio

import Templates

class ChoiceTemplatedFieldTest(Templates.TestCase):
    def testReadWrite(self):
        selector = 0
        choice1 = self.api.TemplatedChoice_uint32_uint16(selector)
        choice1.templated_field1 = 42
        choice2 = self.api.TemplatedChoice_Compound_uint32_uint16(
            selector, templated_field1_=self.api.Compound_uint32(42)
        )
        choiceTemplatedField = self.api.ChoiceTemplatedField(selector, choice1, choice2)

        writer = zserio.BitStreamWriter()
        choiceTemplatedField.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readChoiceTemplatedField = self.api.ChoiceTemplatedField()
        readChoiceTemplatedField.read(reader)
        self.assertEqual(choiceTemplatedField, readChoiceTemplatedField)
