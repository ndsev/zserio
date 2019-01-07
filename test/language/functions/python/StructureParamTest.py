import unittest
import zserio

from testutils import getZserioApi

class StructureParamTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "functions.zs").structure_param

    def testMetresConverterCaller(self):
        metresConverterCaller = self._createMetresConverterCaller()
        self.assertEqual(CONVERTED_CM_VALUE, metresConverterCaller.getCm())

        writer = zserio.BitStreamWriter()
        metresConverterCaller.write(writer)
        expectedWriter = zserio.BitStreamWriter()
        StructureParamTest._writeMetresConverterCallerToStream(expectedWriter)
        self.assertTrue(expectedWriter.getByteArray() == writer.getByteArray())

        reader = zserio.BitStreamReader(writer.getByteArray())
        readMetresConverterCaller = self.api.MetresConverterCaller.fromReader(reader)
        self.assertEqual(metresConverterCaller, readMetresConverterCaller)

    @staticmethod
    def _writeMetresConverterCallerToStream(writer):
        writer.writeBits(VALUE_A, 16)
        writer.writeBits(CONVERTED_CM_VALUE, 16)

    def _createMetresConverterCaller(self):
        metresConverter = self.api.MetresConverter.fromFields(M_VALUE_TO_CONVERT, VALUE_A)

        return self.api.MetresConverterCaller.fromFields(metresConverter, CONVERTED_CM_VALUE)

VALUE_A = 0xABCD
M_VALUE_TO_CONVERT = 2
CONVERTED_CM_VALUE = M_VALUE_TO_CONVERT * 100
