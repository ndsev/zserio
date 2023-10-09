import zserio

import Templates

class InstantiateTypeOnParameterizedTemplateTest(Templates.TestCase):
    def testReadWrite(self):
        instantiateTypeOnParameterizedTemplate = self.api.InstantiateTypeOnParameterizedTemplate(
            2, self.api.TestP(2, self.api.Parameterized(2, [13, 42]))
        )

        writer = zserio.BitStreamWriter()
        instantiateTypeOnParameterizedTemplate.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateTypeOnParameterizedTemplate = self.api.InstantiateTypeOnParameterizedTemplate()
        readInstantiateTypeOnParameterizedTemplate.read(reader)
        self.assertEqual(instantiateTypeOnParameterizedTemplate, readInstantiateTypeOnParameterizedTemplate)
