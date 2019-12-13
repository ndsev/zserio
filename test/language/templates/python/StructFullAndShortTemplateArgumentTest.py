import unittest
import zserio

from testutils import getZserioApi

class StructFullAndShortTemplateArgumentTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").struct_full_and_short_template_argument

    def testReadWriteFull(self):
        storage = self.api.templated_struct.Storage.fromFields("String")
        structFullNameTemplateArgument = self.api.StructFullNameTemplateArgument.fromFields(
            self.api.templated_struct.TemplatedStruct_Storage.fromFields(storage))

        writer = zserio.BitStreamWriter()
        structFullNameTemplateArgument.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readStructFullNameTemplateArgument = self.api.StructFullNameTemplateArgument()
        readStructFullNameTemplateArgument.read(reader)
        self.assertEqual(structFullNameTemplateArgument, readStructFullNameTemplateArgument)

    def testReadWriteShort(self):
        storage = self.api.templated_struct.Storage.fromFields("String")
        structShortNameTemplateArgument = self.api.templated_struct.StructShortNameTemplateArgument.fromFields(
            self.api.templated_struct.TemplatedStruct_Storage.fromFields(storage))

        writer = zserio.BitStreamWriter()
        structShortNameTemplateArgument.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readStructShortNameTemplateArgument = self.api.templated_struct.StructShortNameTemplateArgument()
        readStructShortNameTemplateArgument.read(reader)
        self.assertEqual(structShortNameTemplateArgument, readStructShortNameTemplateArgument)
