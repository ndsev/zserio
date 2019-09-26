import unittest
import zserio

from testutils import getZserioApi

class StructTemplateInTemplateTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").struct_template_in_template

    def testReadWrite(self):
        structTemplateInTemplate = self.api.StructTemplateInTemplate.fromFields(
            self.api.Field_uint32.fromFields(self.api.Compound_uint32.fromFields(42)),
            self.api.Field_string.fromFields(self.api.Compound_string.fromFields("string"))
        )

        writer = zserio.BitStreamWriter()
        structTemplateInTemplate.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readStructTemplateInTemplate = self.api.StructTemplateInTemplate()
        readStructTemplateInTemplate.read(reader)
        self.assertEqual(structTemplateInTemplate, readStructTemplateInTemplate)
