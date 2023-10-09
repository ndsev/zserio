import zserio

import ParameterizedTypes

class ExternParamTest(ParameterizedTypes.TestCase):
    def testWriteRead(self):
        externData = zserio.BitBuffer(bytearray([0xCA, 0xFE]), 15)
        externParam = self.api.ExternParam(externData, self.api.Parameterized(externData, 13))

        bitBuffer = zserio.serialize(externParam)
        readExternParam = zserio.deserialize(self.api.ExternParam, bitBuffer)
        self.assertEqual(externParam, readExternParam)
