import zserio

import Templates

class InstantiateTypeAsChoiceFieldTest(Templates.TestCase):
    def testReadWrite(self):
        instantiateTypeAsChoiceField = self.api.InstantiateTypeAsChoiceField(True)
        instantiateTypeAsChoiceField.test = self.api.Test32(13)

        writer = zserio.BitStreamWriter()
        instantiateTypeAsChoiceField.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateTypeAsChoiceField = self.api.InstantiateTypeAsChoiceField(True)
        readInstantiateTypeAsChoiceField.read(reader)
        self.assertEqual(instantiateTypeAsChoiceField, readInstantiateTypeAsChoiceField)
