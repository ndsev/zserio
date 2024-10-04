import zserio

import ParameterizedTypes


class BytesParamTest(ParameterizedTypes.TestCase):
    def testWriteRead(self):
        bytesData = bytearray([0xCA, 0xFE])
        bytesParam = self.api.BytesParam(bytesData, self.api.Parameterized(bytesData, 13))

        bitBuffer = zserio.serialize(bytesParam)
        readBytesParam = zserio.deserialize(self.api.BytesParam, bitBuffer)
        self.assertEqual(bytesParam, readBytesParam)
