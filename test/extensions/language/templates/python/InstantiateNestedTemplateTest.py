import zserio

import Templates


class InstantiateNestedTemplateTest(Templates.TestCase):
    def testReadWrite(self):
        instantiateNestedTemplate = self.api.InstantiateNestedTemplate(self.api.TStr(self.api.NStr("test")))

        writer = zserio.BitStreamWriter()
        instantiateNestedTemplate.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateNestedTemplate = self.api.InstantiateNestedTemplate()
        readInstantiateNestedTemplate.read(reader)
        self.assertEqual(instantiateNestedTemplate, readInstantiateNestedTemplate)
