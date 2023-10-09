import zserio

import Templates

class InstantiateUnusedTest(Templates.TestCase):
    def testReadWrite(self):
        # check that unused template is instantiated via the instantiate command
        u32 = self.api.U32(13)

        writer = zserio.BitStreamWriter()
        u32.write(writer)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        readU32 = self.api.U32()
        readU32.read(reader)
        self.assertEqual(u32, readU32)
