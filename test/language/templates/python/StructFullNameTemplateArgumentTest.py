import unittest
import zserio

from testutils import getZserioApi

class StructFullNameTemplateArgumentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").struct_full_name_template_argument

    def testReadWrite(self):
        structFullNameTemplateArgument = self.api.StructFullNameTemplateArgument.fromFields(
            self.api.TemplatedStruct_templates_struct_full_name_template_argument_storage_Storage.fromFields(
                self.api.storage.Storage.fromFields(42)
            ),
            self.api.TemplatedStruct_Storage.fromFields(self.api.Storage.fromFields("string"))
        )

        writer = zserio.BitStreamWriter()
        structFullNameTemplateArgument.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readStructFullNameTemplateArgument = self.api.StructFullNameTemplateArgument()
        readStructFullNameTemplateArgument.read(reader)
        self.assertEqual(structFullNameTemplateArgument, readStructFullNameTemplateArgument)
