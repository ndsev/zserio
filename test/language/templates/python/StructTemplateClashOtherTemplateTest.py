import zserio

import Templates


class StructTemplateClashOtherTemplateTest(Templates.TestCase):
    def testReadWrite(self):
        instantiationNameClashOtherTemplate = self.api.InstantiationNameClashOtherTemplate(
            self.api.Test_A_uint32_FA82A3B7(42), self.api.Test_A_uint32_5D68B0C2(self.api.A_uint32(10))
        )

        writer = zserio.BitStreamWriter()
        instantiationNameClashOtherTemplate.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiationNameClashOtherTemplate = self.api.InstantiationNameClashOtherTemplate()
        readInstantiationNameClashOtherTemplate.read(reader)
        self.assertEqual(instantiationNameClashOtherTemplate, readInstantiationNameClashOtherTemplate)
