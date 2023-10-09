import zserio

import Templates

class InstantiateViaSingleImportTest(Templates.TestCase):
    def testReadWrite(self):
        instantiateViaSingleImport = self.api.InstantiateViaSingleImport(
            self.api.pkg.U32(13),
            self.api.pkg.Test_string("test")
        )

        writer = zserio.BitStreamWriter()
        instantiateViaSingleImport.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateViaSingleImport = self.api.InstantiateViaSingleImport()
        readInstantiateViaSingleImport.read(reader)
        self.assertEqual(instantiateViaSingleImport, readInstantiateViaSingleImport)
