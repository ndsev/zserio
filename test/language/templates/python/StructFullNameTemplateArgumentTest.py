import unittest
import zserio

from testutils import getZserioApi

class StructFullNameTemplateArgumentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").struct_full_name_template_argument

    def testReadWrite(self):
        structFullNameTemplateArgument = self.api.StructFullNameTemplateArgument(
            self.api.TemplatedStruct_Storage_08C0ED6D(self.api.storage.Storage(42)),
            self.api.TemplatedStruct_Storage_A3A4B101(self.api.Storage("string"))
        )

        writer = zserio.BitStreamWriter()
        structFullNameTemplateArgument.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readStructFullNameTemplateArgument = self.api.StructFullNameTemplateArgument()
        readStructFullNameTemplateArgument.read(reader)
        self.assertEqual(structFullNameTemplateArgument, readStructFullNameTemplateArgument)
