import zserio

import Subtypes


class SubtypeImportedTest(Subtypes.TestCase):
    def testReadWrite(self):
        subtypeImported = self.api.SubtypeImported(self.api.pkg.SubTest(13))

        writer = zserio.BitStreamWriter()
        subtypeImported.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readSubtypeImported = self.api.SubtypeImported()
        readSubtypeImported.read(reader)
        self.assertEqual(subtypeImported, readSubtypeImported)
