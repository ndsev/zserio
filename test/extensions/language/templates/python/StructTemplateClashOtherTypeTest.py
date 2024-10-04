import zserio

import Templates


class StructTemplateClashOtherTypeTest(Templates.TestCase):
    def testReadWrite(self):
        instantiationNameClashOtherType = self.api.InstantiationNameClashOtherType(
            self.api.Test_uint32_99604043(42)
        )

        writer = zserio.BitStreamWriter()
        instantiationNameClashOtherType.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiationNameClashOtherType = self.api.InstantiationNameClashOtherType()
        readInstantiationNameClashOtherType.read(reader)
        self.assertEqual(instantiationNameClashOtherType, readInstantiationNameClashOtherType)
