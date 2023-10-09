import zserio

import Templates

class InstantiateTypeAsTemplateArgumentTest(Templates.TestCase):
    def testReadWrite(self):
        instantiateTypeAsTemplateArgument = self.api.InstantiateTypeAsTemplateArgument(
            self.api.Other_Str(self.api.Str("test"))
        )

        writer = zserio.BitStreamWriter()
        instantiateTypeAsTemplateArgument.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateTypeAsTemplateArgument = self.api.InstantiateTypeAsTemplateArgument()
        readInstantiateTypeAsTemplateArgument.read(reader)
        self.assertEqual(instantiateTypeAsTemplateArgument, readInstantiateTypeAsTemplateArgument)
