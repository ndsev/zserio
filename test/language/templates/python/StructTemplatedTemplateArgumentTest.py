import unittest
import zserio

from testutils import getZserioApi

class StructTemplatedTemplateArgumentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").struct_templated_template_argument

    def testReadWrite(self):
        structTemplatedTemplateArgument = self.api.StructTemplatedTemplateArgument.fromFields(
            self.api.Field_Compound_uint32.fromFields(self.api.Compound_uint32.fromFields(42))
        )

        writer = zserio.BitStreamWriter()
        structTemplatedTemplateArgument.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readStructTemplatedTemplateArgument = self.api.StructTemplatedTemplateArgument()
        readStructTemplatedTemplateArgument.read(reader)
        self.assertEqual(structTemplatedTemplateArgument, readStructTemplatedTemplateArgument)
