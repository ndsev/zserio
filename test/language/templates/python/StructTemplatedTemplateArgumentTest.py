import unittest
import zserio

from testutils import getZserioApi

class StructTemplatedTemplateArgumentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").struct_templated_template_argument

    def testReadWrite(self):
        structTemplatedTemplateArgument = self.api.StructTemplatedTemplateArgument(
            self.api.Field_Compound_uint32(self.api.Compound_uint32(42))
        )

        writer = zserio.BitStreamWriter()
        structTemplatedTemplateArgument.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readStructTemplatedTemplateArgument = self.api.StructTemplatedTemplateArgument()
        readStructTemplatedTemplateArgument.read(reader)
        self.assertEqual(structTemplatedTemplateArgument, readStructTemplatedTemplateArgument)
