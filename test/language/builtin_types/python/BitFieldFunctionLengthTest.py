import unittest
import os
import zserio

from testutils import getZserioApi, getApiDir

class BitFieldFunctionLengthTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.api = getZserioApi(__file__, "builtin_types.zs").bitfield_function_length

    def testBitSizeOf(self):
        container = self._createContainer()
        expectedBitSizeOfContainer = 64 + 7 * 64 + 7 + 13 + 13
        self.assertEqual(expectedBitSizeOfContainer, container.bitsizeof())

    def testReadWrite(self):
        container = self._createContainer()
        zserio.serialize_to_file(container, self.BLOB_NAME)
        readContainer = zserio.deserialize_from_file(self.api.Container, self.BLOB_NAME)
        self.assertEqual(container, readContainer)

    def _createContainer(self):
        return self.api.Container(
                0xDEAD, # id
                [0xDEAD1, 0xDEAD2, 0xDEAD3, 0xDEAD4, 0xDEAD5, 0xDEAD6, 0xDEAD7], # array[7]
                0x3F, # bitField3 (7 bits)
                0x1FFF, # bitField4 (0xDEAD & 0x0F = 0xD = 13 bits)
                0x1FFF # bitField5 (0xDEAD % 32 = 13 bits)
                )

    BLOB_NAME = os.path.join(getApiDir(os.path.dirname(__file__)), "bit_field_function_length.blob")
