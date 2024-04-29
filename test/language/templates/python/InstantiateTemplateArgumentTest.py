import zserio

import Templates


class InstantiateTemplateArgumentTest(Templates.TestCase):
    def testReadWrite(self):
        instantiateTemplateArgument = self.api.InstantiateTemplateArgument(
            self.api.Other_Str(self.api.Str("test"))
        )

        writer = zserio.BitStreamWriter()
        instantiateTemplateArgument.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateTemplateArgument = self.api.InstantiateTemplateArgument()
        readInstantiateTemplateArgument.read(reader)
        self.assertEqual(instantiateTemplateArgument, readInstantiateTemplateArgument)
