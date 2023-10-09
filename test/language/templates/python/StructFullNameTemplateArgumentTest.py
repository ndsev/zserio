import zserio

import Templates

class StructFullNameTemplateArgumentTest(Templates.TestCase):
    def testReadWrite(self):
        structFullNameTemplateArgument = self.api.StructFullNameTemplateArgument(
            self.api.TemplatedStruct_Storage_C76E422F(self.api.import_storage.Storage(42)),
            self.api.TemplatedStruct_Storage_A3A4B101(self.api.Storage("string"))
        )

        writer = zserio.BitStreamWriter()
        structFullNameTemplateArgument.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readStructFullNameTemplateArgument = self.api.StructFullNameTemplateArgument()
        readStructFullNameTemplateArgument.read(reader)
        self.assertEqual(structFullNameTemplateArgument, readStructFullNameTemplateArgument)
