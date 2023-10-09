import zserio

import Templates

class StructTemplateClashTest(Templates.TestCase):
    def testReadWrite(self):
        testStruct_uint32 = self.api.TestStruct_uint32(
            42,
            self.api.Template_A_B_C_7FE93D34(self.api.A_B(1), self.api.C(True)),
            self.api.Template_A_B_C_5EB4E3FC(self.api.A(1), self.api.B_C("string"))
        )
        instantiationNameClash = self.api.InstantiationNameClash(testStruct_uint32)

        writer = zserio.BitStreamWriter()
        instantiationNameClash.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiationNameClash = self.api.InstantiationNameClash()
        readInstantiationNameClash.read(reader)
        self.assertEqual(instantiationNameClash, readInstantiationNameClash)
