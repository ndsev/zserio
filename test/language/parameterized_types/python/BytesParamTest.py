import unittest
import zserio

from testutils import getZserioApi

class BytesParamTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "parameterized_types.zs").bytes_param

    def testWriteRead(self):
        bytesData = bytearray([0xCA, 0xFE])
        bytesParam = self.api.BytesParam(bytesData, self.api.Parameterized(bytesData, 13))

        bitBuffer = zserio.serialize(bytesParam)
        readBytesParam = zserio.deserialize(self.api.BytesParam, bitBuffer)
        self.assertEqual(bytesParam, readBytesParam)
