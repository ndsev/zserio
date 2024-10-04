import zserio

import Templates


class InstantiateClashOtherTemplateTest(Templates.TestCase):
    def testReadWrite(self):
        instantiateClashOtherTemplate = self.api.InstantiateClashOtherTemplate(
            self.api.Test_uint32_99604043(13)
        )

        writer = zserio.BitStreamWriter()
        instantiateClashOtherTemplate.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateClashOtherTemplate = self.api.InstantiateClashOtherTemplate()
        readInstantiateClashOtherTemplate.read(reader)
        self.assertEqual(instantiateClashOtherTemplate, readInstantiateClashOtherTemplate)
