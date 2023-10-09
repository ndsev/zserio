import zserio

import Templates

class InstantiateOnlyNestedTest(Templates.TestCase):
    def testReadWrite(self):
        instantiateOnlyNested = self.api.InstantiateOnlyNested(self.api.pkg.Test_uint32(self.api.N32(13)))

        writer = zserio.BitStreamWriter()
        instantiateOnlyNested.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateOnlyNested = self.api.InstantiateOnlyNested()
        readInstantiateOnlyNested.read(reader)
        self.assertEqual(instantiateOnlyNested, readInstantiateOnlyNested)
