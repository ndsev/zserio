import unittest
import zserio

from testutils import getZserioApi

class StructureParamTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "functions.zs").structure_param

    def testMetresConverterCaller(self):
        metresConverterCaller = self._createMetresConverterCaller()
        self.assertEqual(CONVERTED_CM_VALUE, metresConverterCaller.cm)

        writer = zserio.BitStreamWriter()
        metresConverterCaller.write(writer)
        expectedWriter = zserio.BitStreamWriter()
        StructureParamTest._writeMetresConverterCallerToStream(expectedWriter)
        self.assertTrue(expectedWriter.getByteArray() == writer.getByteArray())
        self.assertTrue(expectedWriter.getBitPosition() == writer.getBitPosition())

        reader = zserio.BitStreamReader(writer.getByteArray(), writer.getBitPosition())
        readMetresConverterCaller = self.api.MetresConverterCaller.fromReader(reader)
        self.assertEqual(metresConverterCaller, readMetresConverterCaller)

    @staticmethod
    def _writeMetresConverterCallerToStream(writer):
        writer.writeBits(VALUE_A, 16)
        writer.writeBits(CONVERTED_CM_VALUE, 16)

    def _createMetresConverterCaller(self):
        metresConverter = self.api.MetresConverter(metres_=M_VALUE_TO_CONVERT, a_=VALUE_A)

        return self.api.MetresConverterCaller(metresConverter, CONVERTED_CM_VALUE)

VALUE_A = 0xABCD
M_VALUE_TO_CONVERT = 2
CONVERTED_CM_VALUE = M_VALUE_TO_CONVERT * 100
