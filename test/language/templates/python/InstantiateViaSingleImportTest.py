import unittest
import zserio

from testutils import getZserioApi

class InstantiateViaSingleImportTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").instantiate_via_single_import

    def testReadWrite(self):
        instantiateViaSingleImport = self.api.InstantiateViaSingleImport(
            self.api.pkg.U32(13),
            self.api.pkg.Test_string("test")
        )

        writer = zserio.BitStreamWriter()
        instantiateViaSingleImport.write(writer)
        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readInstantiateViaSingleImport = self.api.InstantiateViaSingleImport()
        readInstantiateViaSingleImport.read(reader)
        self.assertEqual(instantiateViaSingleImport, readInstantiateViaSingleImport)
