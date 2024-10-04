import zserio

import Templates


class InstantiateViaImportTest(Templates.TestCase):
    def testReadWrite(self):
        instantiateViaImport = self.api.InstantiateViaImport(
            self.api.pkg.U32(13), self.api.pkg.Test_string("test")
        )

        writer = zserio.BitStreamWriter()
        instantiateViaImport.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateViaImport = self.api.InstantiateViaImport()
        readInstantiateViaImport.read(reader)
        self.assertEqual(instantiateViaImport, readInstantiateViaImport)
