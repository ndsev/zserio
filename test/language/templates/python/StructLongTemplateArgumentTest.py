import zserio

import Templates

class StructLongTemplateArgumentTest(Templates.TestCase):
    def testReadWrite(self):
        templ = self.api.TemplatedStruct_ThisIsVeryVeryVeryLongNamedStructure_ThisIsVery_(
            self.api.ThisIsVeryVeryVeryLongNamedStructure("StringT"),
            self.api.ThisIsVeryVeryVeryLongNamedStructure("StringU"),
            self.api.ThisIsVeryVeryVeryLongNamedStructure("StringV")
        )
        structLongTemplateArgument = self.api.StructLongTemplateArgument(templ)

        writer = zserio.BitStreamWriter()
        structLongTemplateArgument.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readStructLongTemplateArgument = self.api.StructLongTemplateArgument()
        readStructLongTemplateArgument.read(reader)
        self.assertEqual(structLongTemplateArgument, readStructLongTemplateArgument)
