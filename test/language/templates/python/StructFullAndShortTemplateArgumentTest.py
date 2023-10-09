import zserio

import Templates

class StructFullAndShortTemplateArgumentTest(Templates.TestCase):
    def testReadWriteFull(self):
        storage = self.api.templated_struct.Storage("String")
        structFullNameTemplateArgument = self.api.StructFullNameTemplateArgument(
            self.api.templated_struct.TemplatedStruct_Storage(storage))

        writer = zserio.BitStreamWriter()
        structFullNameTemplateArgument.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readStructFullNameTemplateArgument = self.api.StructFullNameTemplateArgument()
        readStructFullNameTemplateArgument.read(reader)
        self.assertEqual(structFullNameTemplateArgument, readStructFullNameTemplateArgument)

    def testReadWriteShort(self):
        storage = self.api.templated_struct.Storage("String")
        structShortNameTemplateArgument = self.api.templated_struct.StructShortNameTemplateArgument(
            self.api.templated_struct.TemplatedStruct_Storage(storage))

        writer = zserio.BitStreamWriter()
        structShortNameTemplateArgument.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readStructShortNameTemplateArgument = self.api.templated_struct.StructShortNameTemplateArgument()
        readStructShortNameTemplateArgument.read(reader)
        self.assertEqual(structShortNameTemplateArgument, readStructShortNameTemplateArgument)
