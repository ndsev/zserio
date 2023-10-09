import zserio

import Templates

class InstantiateImportedTest(Templates.TestCase):
    def testReadWrite(self):
        instantiateImported = self.api.InstantiateImported(self.api.pkg.U32(13), self.api.Test_string("test"))

        writer = zserio.BitStreamWriter()
        instantiateImported.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateImported = self.api.InstantiateImported()
        readInstantiateImported.read(reader)
        self.assertEqual(instantiateImported, readInstantiateImported)
