import zserio

import Templates

class SubtypeTemplateWithCompoundTest(Templates.TestCase):
    def testReadWrite(self):
        subtypeTemplateWithCompound = self.api.SubtypeTemplateWithCompound(
            self.api.Compound(13),
            self.api.TemplateCompound_Compound(self.api.Compound(42))
        )

        writer = zserio.BitStreamWriter()
        subtypeTemplateWithCompound.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readSubtypeTemplateWithCompound = self.api.SubtypeTemplateWithCompound()
        readSubtypeTemplateWithCompound.read(reader)
        self.assertEqual(subtypeTemplateWithCompound, readSubtypeTemplateWithCompound)
