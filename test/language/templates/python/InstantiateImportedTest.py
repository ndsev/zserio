import unittest
import zserio

from testutils import getZserioApi

class InstantiateImportedTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").instantiate_imported

    def testReadWrite(self):
        instantiateImported = self.api.InstantiateImported(self.api.pkg.U32(13), self.api.Test_string("test"))

        writer = zserio.BitStreamWriter()
        instantiateImported.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateImported = self.api.InstantiateImported()
        readInstantiateImported.read(reader)
        self.assertEqual(instantiateImported, readInstantiateImported)
