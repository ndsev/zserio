import zserio

import Templates


class StructTemplatedTemplateArgumentTest(Templates.TestCase):
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
