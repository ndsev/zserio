import zserio

import Templates

class InstantiateWithInstantiateTemplateArgumentTest(Templates.TestCase):
    def testReadWrite(self):
        instantiateWithInstantiateTemplateArgument = self.api.InstantiateWithInstantiateTemplateArgument(
            self.api.Other8(self.api.Data8(13)),
            self.api.Other32(self.api.Data32(0xCAFE))
        )

        writer = zserio.BitStreamWriter()
        instantiateWithInstantiateTemplateArgument.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateWithInstantiateTemplateArgument = self.api.InstantiateWithInstantiateTemplateArgument()
        readInstantiateWithInstantiateTemplateArgument.read(reader)
        self.assertEqual(instantiateWithInstantiateTemplateArgument,
                         readInstantiateWithInstantiateTemplateArgument)
