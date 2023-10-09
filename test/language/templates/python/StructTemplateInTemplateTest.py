import zserio

import Templates

class StructTemplateInTemplateTest(Templates.TestCase):
    def testReadWrite(self):
        structTemplateInTemplate = self.api.StructTemplateInTemplate(
            self.api.Field_uint32(self.api.Compound_uint32(42)),
            self.api.Field_string(self.api.Compound_string("string"))
        )

        writer = zserio.BitStreamWriter()
        structTemplateInTemplate.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readStructTemplateInTemplate = self.api.StructTemplateInTemplate()
        readStructTemplateInTemplate.read(reader)
        self.assertEqual(structTemplateInTemplate, readStructTemplateInTemplate)
