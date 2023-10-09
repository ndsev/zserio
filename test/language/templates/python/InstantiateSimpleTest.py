import zserio

import Templates

class InstantiateSimpleTest(Templates.TestCase):
    def testReadWrite(self):
        instantiateSimple = self.api.InstantiateSimple(self.api.U32(13))

        writer = zserio.BitStreamWriter()
        instantiateSimple.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateSimple = self.api.InstantiateSimple()
        readInstantiateSimple.read(reader)
        self.assertEqual(instantiateSimple, readInstantiateSimple)
