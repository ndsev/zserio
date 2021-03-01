import unittest
import zserio

from testutils import getZserioApi

class InstantiateViaImportTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "templates.zs").instantiate_via_import

    def testReadWrite(self):
        instantiateViaImport = self.api.InstantiateViaImport(
            self.api.pkg.U32(13),
            self.api.pkg.Test_string("test")
        )

        writer = zserio.BitStreamWriter()
        instantiateViaImport.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateViaImport = self.api.InstantiateViaImport()
        readInstantiateViaImport.read(reader)
        self.assertEqual(instantiateViaImport, readInstantiateViaImport)
