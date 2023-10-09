import zserio

import Templates

class SubtypeTemplateWithBuiltinTest(Templates.TestCase):
    def testReadWrite(self):
        subtypeTemplateWithBuiltin = self.api.SubtypeTemplateWithBuiltin(
            self.api.TestStructure_uint32(13)
        )

        writer = zserio.BitStreamWriter()
        subtypeTemplateWithBuiltin.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readSubtypeTemplateWithBuiltin = self.api.SubtypeTemplateWithBuiltin()
        readSubtypeTemplateWithBuiltin.read(reader)
        self.assertEqual(subtypeTemplateWithBuiltin, readSubtypeTemplateWithBuiltin)
