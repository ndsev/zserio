import zserio

import Templates

class InstantiateNotImportedTest(Templates.TestCase):
    def testReadWrite(self):
        instantiateNotImported = self.api.InstantiateNotImported(
            self.api.pkg.Test_uint32(13),
            self.api.pkg.Test_string("test")
        )

        writer = zserio.BitStreamWriter()
        instantiateNotImported.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateNotImported = self.api.InstantiateNotImported()
        readInstantiateNotImported.read(reader)
        self.assertEqual(instantiateNotImported, readInstantiateNotImported)
