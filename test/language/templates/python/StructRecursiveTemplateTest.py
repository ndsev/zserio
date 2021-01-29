import unittest
import zserio

from testutils import getZserioApi

class StructRecursiveTemplateTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").struct_recursive_template

    def testReadWrite(self):
        structRecursiveTemplate = self.api.StructRecursiveTemplate(
            self.api.Compound_Compound_uint32(self.api.Compound_uint32(42)),
            self.api.Compound_Compound_Compound_string(
                self.api.Compound_Compound_string(self.api.Compound_string("string"))
            )
        )

        writer = zserio.BitStreamWriter()
        structRecursiveTemplate.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readStructRecursiveTemplate = self.api.StructRecursiveTemplate()
        readStructRecursiveTemplate.read(reader)
        self.assertEqual(structRecursiveTemplate, readStructRecursiveTemplate)
