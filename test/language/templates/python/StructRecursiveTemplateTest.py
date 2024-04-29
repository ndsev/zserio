import zserio

import Templates


class StructRecursiveTemplateTest(Templates.TestCase):
    def testReadWrite(self):
        structRecursiveTemplate = self.api.StructRecursiveTemplate(
            self.api.Compound_Compound_uint32(self.api.Compound_uint32(42)),
            self.api.Compound_Compound_Compound_string(
                self.api.Compound_Compound_string(self.api.Compound_string("string"))
            ),
        )

        writer = zserio.BitStreamWriter()
        structRecursiveTemplate.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readStructRecursiveTemplate = self.api.StructRecursiveTemplate()
        readStructRecursiveTemplate.read(reader)
        self.assertEqual(structRecursiveTemplate, readStructRecursiveTemplate)
