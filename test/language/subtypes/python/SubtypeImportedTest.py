import unittest
import zserio

from testutils import getZserioApi

class SubtypeImportedTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "subtypes.zs").subtype_imported

    def testReadWrite(self):
        subtypeImported = self.api.SubtypeImported.fromFields(
            self.api.pkg.SubTest.fromFields(13)
        )

        writer = zserio.BitStreamWriter()
        subtypeImported.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray())
        readSubtypeImported = self.api.SubtypeImported()
        readSubtypeImported.read(reader)
        self.assertEqual(subtypeImported, readSubtypeImported)
