import zserio

import Templates

class InstantiateVsDefaultTest(Templates.TestCase):
    def testReadWrite(self):
        instantiateVsDefault = self.api.InstantiateVsDefault(self.api.pkg.Test_uint32(13),
                                                             self.api.TStr("test"))

        writer = zserio.BitStreamWriter()
        instantiateVsDefault.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readInstantiateVsDefault = self.api.InstantiateVsDefault()
        readInstantiateVsDefault.read(reader)
        self.assertEqual(instantiateVsDefault, readInstantiateVsDefault)
